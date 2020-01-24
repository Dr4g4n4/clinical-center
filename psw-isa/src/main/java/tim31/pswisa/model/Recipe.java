package tim31.pswisa.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Recipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToMany
	@JoinTable(name = "workers", joinColumns = @JoinColumn(name = "recipe_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "medical_worker_id", referencedColumnName = "id"))
	private Set<MedicalWorker> medicalWorkers;

	/*
	 * @JsonBackReference(value = "recipe_doc_mov")
	 * 
	 * @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) private
	 * MedicalWorker doctor;
	 */
	@JsonBackReference(value = "recipe_code_mov")
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Codebook code;

	@JsonBackReference(value = "report_recipe_mov")
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Report report;

	@JsonBackReference(value = "nurse_recipe_mov")
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private MedicalWorker nurse;

	@Column(name = "verified", unique = false, nullable = false)
	private Boolean verified;
	
	@Version
	private Long version;

	public Recipe() {

	}

	public Codebook getCode() {
		return code;
	}

	public void setCode(Codebook code) {
		this.code = code;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<MedicalWorker> getMedicalWorkers() {
		return medicalWorkers;
	}

	public void setMedicalWorkers(Set<MedicalWorker> medicalWorkers) {
		this.medicalWorkers = medicalWorkers;
	}

	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public MedicalWorker getNurse() {
		return nurse;
	}

	public void setNurse(MedicalWorker nurse) {
		this.nurse = nurse;
	}

}
