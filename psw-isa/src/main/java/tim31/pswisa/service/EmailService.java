package tim31.pswisa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import tim31.pswisa.dto.AbsenceDTO;
import tim31.pswisa.model.Absence;
import tim31.pswisa.model.Clinic;
import tim31.pswisa.model.MedicalWorker;
import tim31.pswisa.model.Patient;
import tim31.pswisa.model.User;
import tim31.pswisa.repository.AbsenceRepository;
import tim31.pswisa.repository.MedicalWorkerRepository;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private Environment env;

	@Autowired
	private UserService userService;

	@Autowired
	private MedicalWorkerService medicalWorkerService;

	@Autowired
	private AbsenceService absenceService;

	@Autowired
	private MedicalWorkerRepository medicalWorkerRepository;

	@Autowired
	private AbsenceRepository absenceRepository;

	@Async
	public void sendAccountConfirmationEmail(String email, String text) throws MailException, InterruptedException {
		System.out.println("Sending email...");
		User u = userService.findOneByEmail(email);
		String path = "http://localhost:3000/activateAccount/" + u.getId();
		// String path = "<html><a href='" + varifyUrl + "'>" + varifyUrl + "</a></html>
		// ";

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo("pswisa.tim31.2019@gmail.com");
		msg.setFrom(env.getProperty("spring.mail.username"));
		msg.setSubject("Account confirmation");
		if (text.equals("approved"))
			msg.setText("Please confirm your Clinical center account by clicking on link below. \n\n" + path
					+ "\n\nAdministration team");
		else
			msg.setText(
					"Your request for registration to Clinical center can't be approved. The reason for rejection is:\n\n"
							+ text + "\n\nAdministration team");
		javaMailSender.send(msg);

		System.out.println("Email sent.");
	}

	/**
	 * This method servers for sending email notification to clinic administrators
	 * when doctor booked appointment or operation
	 * 
	 * @param clinic    - clinic where doctor works
	 * @param medWorker - doctor who wants to book appointment or operation
	 * @param patient   - patient who has to be examinated
	 * @return - (void) This method has no return value
	 */
	@Async
	public void sendNotificationToAmin(Clinic clinic, MedicalWorker medWorker, Patient patient)
			throws MailException, InterruptedException {
		// Set<ClinicAdministrator> clinicAdministrators = clinic.getClAdmins();
		SimpleMailMessage msg = new SimpleMailMessage();
		// for(ClinicAdministrator ca : clinicAdministrators) {
		System.out.println("Sending email...");
		msg.setTo("pswisa.tim31.2019@gmail.com");
		msg.setFrom(env.getProperty("spring.mail.username"));
		msg.setSubject("New request for operation or appointment");
		msg.setText("There is new request for operation or appointment by doctor " + medWorker.getUser().getName() + " "
				+ medWorker.getUser().getSurname() + " for patient " + patient.getUser().getName() + " "
				+ patient.getUser().getSurname());
		javaMailSender.send(msg);
		System.out.println("Email sent.");
		// }

	}

	/**
	 * This method servers for sending email to medical worker after accepting or
	 * refusing request for vacation when doctor booked appointment or operation
	 * 
	 * @param user   - logged clinic administrator
	 * @param a      - absence of doctor
	 * @param reason - reason for refusing or "" if it is accepted
	 * @return - (void) This method has no return value
	 */
	@Async
	public void sendReasonToMw(User user, AbsenceDTO a, String reason) throws MailException, InterruptedException {
		// send email to medicalWorker
		String email = a.getMedicalWorker().getUser().getEmail();
		User medWorker = userService.findOneByEmail(email);
		MedicalWorker medicalWorker = medicalWorkerService.findByUser(medWorker.getId());
		Absence absence = absenceService.findOneById(a.getId());
		System.out.println(reason);
		if (reason.equals("ok")) {
			// not refuse request for vacation
			medicalWorker.getHollydays().add(absence);
			absence.setAccepted("ACCEPTED");
			absence = absenceRepository.save(absence);
			medicalWorkerRepository.save(medicalWorker);
			SimpleMailMessage msg = new SimpleMailMessage();
			System.out.println("Sending email...");
			msg.setTo("pswisa.tim31.2019@gmail.com");
			msg.setFrom(env.getProperty("spring.mail.username"));
			msg.setSubject("Accepped request for " + a.getTypeOfAbsence());
			msg.setText("Your request is accepted.");
			javaMailSender.send(msg);
			System.out.println("Email sent.");
		} else {
			reason.replace('%', ' ');
			absence.setAccepted("PASSED");
			absence = absenceRepository.save(absence);
			SimpleMailMessage msg = new SimpleMailMessage();
			System.out.println("Sending email...");
			msg.setTo("pswisa.tim31.2019@gmail.com");
			msg.setFrom(env.getProperty("spring.mail.username"));
			msg.setSubject("Refused request for " + a.getTypeOfAbsence());
			msg.setText(reason);
			javaMailSender.send(msg);
			System.out.println("Email sent.");
		}

	}

}
