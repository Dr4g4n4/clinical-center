package tim31.pswisa.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tim31.pswisa.dto.MedicalWorkerDTO;
import tim31.pswisa.model.Authority;
import tim31.pswisa.model.CheckUpType;
import tim31.pswisa.model.Clinic;
import tim31.pswisa.model.ClinicAdministrator;
import tim31.pswisa.model.MedicalWorker;
import tim31.pswisa.model.User;
import tim31.pswisa.repository.ClinicRepository;
import tim31.pswisa.repository.MedicalWorkerRepository;
import tim31.pswisa.repository.UserRepository;
import tim31.pswisa.security.TokenUtils;

@Service
public class MedicalWorkerService {

	@Autowired
	private MedicalWorkerRepository medicalWorkerRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthorityService authorityService;
	
	@Autowired
	private ClinicService clinicService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ClinicRepository clinicRepository;

	public Set<MedicalWorker> findAllByClinicId(Long id) {
		return medicalWorkerRepository.findAllByClinicId(id);
	}

	public MedicalWorker updateMedicalWorker(MedicalWorker medWorker, MedicalWorkerDTO mw) {
		medWorker.getUser().setName(mw.getUser().getName());
		medWorker.getUser().setSurname(mw.getUser().getSurname());
		medWorker.setPhone(mw.getPhone());
		medWorker = update(medWorker);
		return medWorker;
	}

	public MedicalWorker findByUser(Long id) {
		return medicalWorkerRepository.findOneByUserId(id);
	}
	
	public List<MedicalWorkerDTO>getDoctors(Clinic clinic){
		Set<MedicalWorker> temp = findAllByClinicId(clinic.getId());
		List<MedicalWorkerDTO> returnVal = new ArrayList<MedicalWorkerDTO>();

		for(MedicalWorker med : temp) {
				returnVal.add(new MedicalWorkerDTO(med));
		}
		return returnVal;
	}
	
	
	public String deleteDoctor(String email, ClinicAdministrator clinicAdministrator) {
		Clinic clinic = clinicService.findOneById(clinicAdministrator.getClinic().getId());
		User user = userService.findOneByEmail(email);
		System.out.println(email);
		System.out.println(user.getName());
		System.out.println(user.getId());
		MedicalWorker med = findByUser(user.getId());
		//if(med.getCheckUps().size() != 0) {
			clinic.getMedicalStuff().remove(med);
			clinicRepository.save(clinic);
			med.setClinic(null);
			medicalWorkerRepository.save(med);
		return "Obrisano";
		//}
		//else {
			//return "Greska";
		//}

	}
	
	public List<MedicalWorkerDTO>findDoctors(Clinic clinic, String name, String typeD){
		Set<MedicalWorker> temp = findAllByClinicId(clinic.getId());
		List<MedicalWorkerDTO> returnVal = new ArrayList<MedicalWorkerDTO>();
		
		for(MedicalWorker med : temp) {
			if(med.getUser().getName().equals(name)) {
				returnVal.add(new MedicalWorkerDTO(med));
			}
		}
		return returnVal;
	}

	public MedicalWorker findOne(Long id) {
		return medicalWorkerRepository.findById(id).orElseGet(null);
	}

	public MedicalWorker update(MedicalWorker mw) {
		return medicalWorkerRepository.save(mw);
	}

	public MedicalWorker save(MedicalWorkerDTO mw) {
		User user = userRepository.findOneByEmail(mw.getUser().getEmail());
		if (user != null) {
			return null;
		}
		MedicalWorker medicalWorker = new MedicalWorker();
		user = new User();
		user.setName(mw.getUser().getName());
		user.setSurname(mw.getUser().getSurname());
		user.setEmail(mw.getUser().getEmail());
		user.setType(mw.getUser().getType());
		medicalWorker.setUser(user);
		medicalWorker.setPhone(mw.getPhone());
		medicalWorker.setEndHr(mw.getEndHr());
		medicalWorker.setStartHr(mw.getStartHr());
		medicalWorker.getUser().setPassword(passwordEncoder.encode("sifra123"));
		medicalWorker.getUser().setFirstLogin(false);
		medicalWorker.getUser().setEnabled(true);
		medicalWorker.getUser().setActivated(true);
		List<Authority> auth = authorityService.findByname(medicalWorker.getType());
		medicalWorker.getUser().setAuthorities(auth);

		return medicalWorkerRepository.save(medicalWorker);
	}

	public MedicalWorker findOneById(Long id) {
		return medicalWorkerRepository.findOneById(id);
	}
	
	public List<MedicalWorkerDTO> searchDoctors(String[] params) {
		List<MedicalWorkerDTO> forSearch = clinicService.doctorsInClinic(params[0], params[1], params[2]);
		String name = params[3].equals("") ? "" : params[3] ;
		String surname = params[4].equals("") ? "" : params[4] ;
		int rating = 0;		
		List<MedicalWorkerDTO> ret = new ArrayList<MedicalWorkerDTO>() ;
		
		if (!params[5].equals("")) {
			rating = Integer.parseInt(params[5]) ;
		}
		
		for (MedicalWorkerDTO mw : forSearch) {
			if (checkParams(mw, name, surname, rating)) {
				ret.add(mw);
			}
		}
		
		return ret;		
		
	}
	
	public boolean checkParams(MedicalWorkerDTO mw, String name, String surname, int rating) {
				
		if(!name.equals("") && !mw.getUser().getName().equals(name)) return false;
		if(!surname.equals("") && !mw.getUser().getSurname().equals(surname)) return false;
		if(rating != 0 && mw.getRating()!= rating) return false;
		
		return true;
	}
}