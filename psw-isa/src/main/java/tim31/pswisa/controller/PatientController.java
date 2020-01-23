package tim31.pswisa.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tim31.pswisa.dto.MedicalRecordDTO;
import tim31.pswisa.dto.PatientDTO;
import tim31.pswisa.model.MedicalRecord;
import tim31.pswisa.model.Patient;
import tim31.pswisa.model.User;
import tim31.pswisa.security.TokenUtils;
import tim31.pswisa.service.MedicalRecordService;
import tim31.pswisa.service.PatientService;
import tim31.pswisa.service.UserService;

@RestController
public class PatientController {

	@Autowired
	private PatientService patientService;

	@Autowired
	private UserService userService;

	@Autowired
	private MedicalRecordService medicalRecordService;

	@Autowired
	TokenUtils tokenUtils;

	@GetMapping(value = "/patientsRequests", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PatientDTO>> getNewUserRequests() {
		List<Patient> patients = patientService.findAllByActive(userService.findAllByActivated(false));
		List<PatientDTO> ret = new ArrayList<PatientDTO>();
		for (Patient patient : patients) {
			ret.add(new PatientDTO(patient));
		}
		return new ResponseEntity<>(ret, HttpStatus.OK);
	}

	@PostMapping(value = "/findPatients", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PatientDTO>> findPatientsController(@RequestBody String[] params,
			HttpServletRequest request) {

		List<PatientDTO> retVal = patientService.findPatients(params[0], params[1], params[2]);
		if (retVal == null) {
			return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
		} else {
			return new ResponseEntity<>(retVal, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/filterPatients", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PatientDTO>> filterPatientsController(@RequestBody String[] params) {

		List<PatientDTO> retVal = patientService.filterPatients(params[0]);
		if (retVal == null) {
			return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
		} else {
			return new ResponseEntity<>(retVal, HttpStatus.OK);
		}
	}

	@GetMapping(value = "/getPatients", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PatientDTO>> getPatientController() {
		List<PatientDTO> patients = patientService.getAllPatients();
		if (patients.size() != 0) {
			return new ResponseEntity<>(patients, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
		}
	}

	@GetMapping(value = "/getPatientProfile", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Patient> getPatient(HttpServletRequest request) {
		String jwt_token = tokenUtils.getToken(request);
		String email = tokenUtils.getUsernameFromToken(jwt_token);
		ResponseEntity<Patient> ret;
		User up = userService.findOneByEmail(email);
		Patient p = patientService.findOneByUserId(up.getId());
		if (p != null)
			ret = new ResponseEntity<>(p, HttpStatus.OK);
		else
			ret = new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return ret;
	}

	@PostMapping(value = "/editPatient", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Patient> editPatient(@RequestBody Patient p) {
		User temp = userService.findOneByEmail(p.getUser().getEmail());
		Patient patient = patientService.findOneByUserId(temp.getId());

		if (patient != null) {
			patientService.editP(patient, p);
			patientService.save(patient);
			return new ResponseEntity<>(patient, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);

	}

	@PostMapping(value = "/editMedicalRecord", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MedicalRecordDTO> editMedicalRecord(@RequestBody MedicalRecordDTO mr) {
		MedicalRecord medicalRecord = medicalRecordService.update(mr);
		if (medicalRecord != null) {
			return new ResponseEntity<MedicalRecordDTO>(new MedicalRecordDTO(medicalRecord),HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
	}
	
	/**
	 * method for getting medical record of one patient
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/getMedicalRecord", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MedicalRecordDTO> getMedicalRecord(HttpServletRequest request) {
		String jwt_token = tokenUtils.getToken(request);
		String email = tokenUtils.getUsernameFromToken(jwt_token);
		MedicalRecordDTO ret = patientService.getMedicalRecord(email);		
		if (ret != null) {
			return new ResponseEntity<MedicalRecordDTO>(ret,HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

}