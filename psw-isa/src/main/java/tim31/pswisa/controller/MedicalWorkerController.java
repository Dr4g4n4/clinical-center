package tim31.pswisa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tim31.pswisa.model.MedicalWorker;
import tim31.pswisa.model.Patient;
import tim31.pswisa.model.User;
import tim31.pswisa.service.LoggingService;
import tim31.pswisa.service.MedicalWorkerService;
import tim31.pswisa.service.UserService;

@RestController
public class MedicalWorkerController {

	@Autowired
	public MedicalWorkerService medicalWorkerService;
	public UserService userService;
	
	@GetMapping(value="getMedicalWorker/{email}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MedicalWorker> getMedicalWorker(@PathVariable String email) 
	{
		User user = userService.findOneById(email);
		MedicalWorker medicalWorker = medicalWorkerService.findByUser(user.getId());
		if(medicalWorker == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		else {
			return new ResponseEntity<>(medicalWorker,HttpStatus.OK);
		}
	}
	
	@PostMapping(value="/updateMedicalWorker/{email}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MedicalWorker> updateMedicalWorker(@RequestBody MedicalWorker mw, @PathVariable String email) 
	{
		User user = userService.findOneById(email);
		MedicalWorker medWorker = medicalWorkerService.findByUser(user.getId());
		medWorker.getUser().setName(mw.getUser().getName());
		medWorker.getUser().setSurname(mw.getUser().getSurname());
		medWorker.setPhone(mw.getPhone());
		medWorker.getUser().setPassword(mw.getUser().getPassword());
		medWorker = medicalWorkerService.update(medWorker);
		if(medWorker == null) {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}else
			return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
