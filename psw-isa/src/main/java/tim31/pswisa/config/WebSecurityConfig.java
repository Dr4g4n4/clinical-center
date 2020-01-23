package tim31.pswisa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import tim31.pswisa.security.TokenUtils;
import tim31.pswisa.security.auth.RestAuthenticationEntryPoint;
import tim31.pswisa.security.auth.TokenAuthenticationFilter;
import tim31.pswisa.service.LoggingService;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	// Implementacija PasswordEncoder-a koriscenjem BCrypt hashing funkcije.
	// BCrypt po defalt-u radi 10 rundi hesiranja prosledjene vrednosti.
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	private LoggingService jwtUserDetailsService;

	// Neautorizovani pristup zastcenim resursima
	@Autowired
	private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	// Definisemo nacin autentifikacije
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
	}

	@Autowired
	TokenUtils tokenUtils;

	// Definisemo prava pristupa odredjenim URL-ovima
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				// komunikacija izmedju klijenta i servera je stateless
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

				// za neautorizovane zahteve posalji 401 gresku
				.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint).and()

				// svim korisnicima dopusti da pristupe putanjama /auth/**, /h2-console/** i
				// /api/foo
				.authorizeRequests().antMatchers("/register").permitAll().antMatchers("/login").permitAll()
				.antMatchers("/api/foo").permitAll().antMatchers("/getUser").permitAll()

				// svaki zahtev mora biti autorizovan
				.anyRequest().authenticated().and()

				.cors().and()

				// presretni svaki zahtev filterom
				.addFilterBefore(new TokenAuthenticationFilter(tokenUtils, jwtUserDetailsService),
						BasicAuthenticationFilter.class);

		http.csrf().disable();
	}

	// Generalna bezbednost aplikacije
	@Override
	public void configure(WebSecurity web) {
		// TokenAuthenticationFilter ce ignorisati sve ispod navedene putanje
		web.ignoring().antMatchers(HttpMethod.POST, "/login", "/clinic/changeNameOfType", "/clinic/searchOneType/*",
				"/register", "/clinic", "/addAdmin", "/codebook", "/codebook/*", "/updateMedicalWorker",
				"/updateAdministrator", "/clinic/updateClinic", "/sendConfirm", "/addMedicalWorker", "/activateEmail/*",
				"/checkup/addReport", "/clinic/addRoom", "/clinic/deleteRoom", "/checkUpType/deleteType/*", "/checkup/getAllQuickApp/**" ,
				"/checkup/bookQuickApp/*",
				"/checkup/addAppointment", "/checkUpType/addType", "/clinic/searchClinic", "/searchDoctors", "/clinic/getSelectedDoctor" ,
				"/clinic/clinicDoctors", "/clinic/filterClinic/**", "/clinic/addRooms/*", "/editPatient", "/checkup/checkupRequest" ,
				"/changePassword", "/checkup/addRecipes/*", "/verifyRecipe/*", "/clinic/filterRooms", "/clinic/allDocsOneClinic/**" ,
				"/clinic/searchRooms", "/clinic/deleteRoom/*", "/deleteDoctor", "/findDoctors" , "/clinic/changeRoom", "/findPatients", 
				"/filterPatients", "/canAccessToMedicalRecord", "/bookForPatient" );
		web.ignoring().antMatchers(HttpMethod.GET, "/getAllDoctors", "/patientsRequests", "/getMedicalWorker", 
				"/codebook", "/getAdministrator", "/getUser", "/clinic/getClinic", "/clinic/getDoctors", 
				"/checkup/addAppointment", "/checkUpType/addType", "/clinic/searchClinic", "/searchDoctors",
				"/clinic/clinicDoctors", "/clinic/filterClinic/**", "/clinic/addRooms/*", "/editPatient",
				"/changePassword", "/checkup/addRecipes/*", "/verifyRecipe/*", "/clinic/filterRooms",
				"/clinic/searchRooms", "/clinic/deleteRoom/*", "/deleteDoctor", "/findDoctors", "/clinic/changeRoom",
				"/findPatients", "/filterPatients", "/canAccessToMedicalRecord", "/bookForPatient",
				"/editMedicalRecord", "/checkup/update", "/changeDate/*", "/checkup/addDoctors/*", "/clinic/getRevenue", "/requestVacation/*",
				"/checkUpType/allTypesOneClinic/**", "/clinic/getDetails/*", "/getMedicalRecord" ,
				"/getPatientProfile", "/clinic/getAllTypes", "/clinic/getRooms", "/patientsRequests",
				"/checkUpType/addTypes", "/clinic/getClinics", "/getTypes", "/checkUpType/allTypes", "/checkup/*",
				"/getRecipes", "/clinic/getClinicsByType/*", "/getPatients" , "/requestsForRoom" , "/clinic/getClinicRaiting",
				"/clinic/getReportForMonth", "/clinic/getReportForWeek", "/getRequestForVacation", "/clinic/getRooms/*/*/*", "/clinic/roomAvailability/*/*", "/getAllAvailable/*/*/*");
		// web.ignoring().antMatchers(HttpMethod.GET, "/", "/webjars/**", "/*.html",
		// "/favicon.ico", "/**/*.html",
		// "/**/*.css", "/**/*.js");
	}

}
