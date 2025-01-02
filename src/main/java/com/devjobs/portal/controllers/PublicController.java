package com.devjobs.portal.controllers;


import com.devjobs.portal.entities.Job;
import com.devjobs.portal.entities.User;
import com.devjobs.portal.model.request.*;
import com.devjobs.portal.model.response.*;
import com.devjobs.portal.repositories.RepositoryImpl;
import com.devjobs.portal.service.*;
import com.devjobs.portal.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private OtpService otpService;

    @Autowired
    EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RepositoryImpl userRepositoryImpl;

    @Autowired
    private UserService userService;

    @Autowired
    private ResponseBuilderService responseBuilderService;

    @Autowired
    private ExtraService extraService;

    @PutMapping("/signup-otp")
    public ResponseEntity<?> signUpOtp(@RequestBody EmailOnly body){
        ResponseType1 response = new ResponseType1();
        String email = body.getEmail();
        if(userRepositoryImpl.getUser(body.getEmail())!=null){
            response.setBody("User already registered");
            response.setResponseCode(HttpStatus.CONFLICT.value());
            return new ResponseEntity<>(response,HttpStatus.CONFLICT);
        }

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern p = Pattern.compile(emailRegex);
        if(p.matcher(email).matches()){
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            response.setBody("Invalid email");
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        int otpGenerator = extraService.randomNumberGeneration(9999,1000);
        String data = "Hi," +  System.lineSeparator()   +  System.lineSeparator() +  System.lineSeparator() + "Your OTP is " + String.valueOf(otpGenerator) + "." +  System.lineSeparator() +  System.lineSeparator()  + "Regards" +  System.lineSeparator()  + "Devjobs Team";
        emailService.sendMail(email,"OTP for e-mail authentication",data);
        otpService.createOtpEntry(otpGenerator,email);
        response.setResponseCode(HttpStatus.OK.value());
        response.setBody("Otp Sent");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpData signUpData){
        ResponseType1 response = new ResponseType1();
        if(signUpData.getRole()==null || signUpData.getEmail()==null || signUpData.getPassword()==null || signUpData.getFullName()==null || signUpData.getOtp()==null
    || signUpData.getRole().isBlank() || signUpData.getFullName().isBlank() || signUpData.getPassword().isBlank() || signUpData.getEmail().isBlank() || (!signUpData.getRole().equals("ADMIN") &&  !signUpData.getRole().equals("SEEKER") && !signUpData.getRole().equals("RECRUITER"))){
            response.setBody("Incorrect info provided");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        if(userRepositoryImpl.getUser(signUpData.getEmail())!=null){
            response.setBody("User already registered");
            response.setResponseCode(HttpStatus.CONFLICT.value());
            return new ResponseEntity<>(response,HttpStatus.CONFLICT);
        }
        if(!userRepositoryImpl.checkOtp(signUpData.getEmail(),signUpData.getOtp())){
            response.setBody("Regenerate Otp & try again");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        if(!userService.signingUpWithUserData(signUpData)){
            response.setBody("Incorrect info provided");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        response.setBody("Signup complete!!");
        response.setResponseCode(HttpStatus.CREATED.value());
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> logIn(@RequestBody LogInData logInData){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(logInData.getEmail(),logInData.getPassword()));
            UserDetails user = userDetailsService.loadUserByUsername(logInData.getEmail());
            String jwt = jwtUtil.generateToken(user.getUsername());
            ResponseType2 response = new ResponseType2();
            response.setBody("Logged in Successfully");
            response.setToken(jwt);
            response.setResponseCode(HttpStatus.OK.value());
            User userr  = userRepositoryImpl.getUser(user.getUsername());
            UserData1 newUser = responseBuilderService.jobtoPojoBuild2(userr);
            response.setUser(newUser);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch(Exception e){
             log.error("Exception occurred while createAuthenticationToken");
             ResponseType1 response = new ResponseType1();
             response.setBody("Incorrect info provided");
             response.setResponseCode(HttpStatus.BAD_REQUEST.value());
             return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/fetch-jobs-for-home")
    public ResponseEntity<?> fetchJobsForHome(){
        List<Job> jobs = userRepositoryImpl.getJobsForHome();
        List<JobData1> newJobs = responseBuilderService.jobtoPojoBuild(jobs);
        ResponseType3 response = new ResponseType3();
        response.setJobs(newJobs);
        response.setResponseCode(HttpStatus.OK.value());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("/fetch-search-job-name")
    public ResponseEntity<?> fetchSearchJobName(@RequestBody SearchData search){
        List<Job> jobs = userRepositoryImpl.getJobsForSearchName(search.getSearch());
        List<JobData1> newJobs = responseBuilderService.jobtoPojoBuild(jobs);
        ResponseType3 response = new ResponseType3();
        response.setJobs(newJobs);
        response.setResponseCode(HttpStatus.OK.value());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("/fetch-search-job-location")
    public ResponseEntity<?> fetchSearchJobLocation(@RequestBody SearchData search){
        List<Job> jobs = userRepositoryImpl.getJobsForSearchLocation(search.getSearch());
        List<JobData1> newJobs = responseBuilderService.jobtoPojoBuild(jobs);
        ResponseType3 response = new ResponseType3();
        response.setJobs(newJobs);
        response.setResponseCode(HttpStatus.OK.value());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("/reset-password-token")
    public ResponseEntity<?> generateResetPasswordToken(@RequestBody EmailOnly body){
        ResponseType1 response = new ResponseType1();
        String email = body.getEmail();
        if(userRepositoryImpl.getUser(body.getEmail())==null){
            response.setBody("User not registered");
            response.setResponseCode(HttpStatus.CONFLICT.value());
            return new ResponseEntity<>(response,HttpStatus.CONFLICT);
        }
        int otpGenerator = extraService.randomNumberGeneration(99999999,10000000);
        String url = "https://devjobscom.vercel.app/reset-password/" + String.valueOf(otpGenerator);
        String data = "Hi," +  System.lineSeparator()   +  System.lineSeparator() +  System.lineSeparator() + "Access this link " +  url + " to reset your password." +  System.lineSeparator() +  System.lineSeparator()  + "Regards" +  System.lineSeparator()  + "Devjobs Team";
        emailService.sendMail(email,"Link for password reset",data);
        otpService.createOtpEntry(otpGenerator,email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordData body){
        ResponseType1 response = new ResponseType1();
        String email = body.getEmail();
        if(body.getEmail()==null || body.getPassword()==null || body.getPassword().isBlank() || body.getEmail().isBlank() || body.getOtp()==null){
            response.setBody("Incomplete info provided");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        User user = userRepositoryImpl.getUser(body.getEmail());
        if(user==null){
            response.setBody("User not registered");
            response.setResponseCode(HttpStatus.CONFLICT.value());
            return new ResponseEntity<>(response,HttpStatus.CONFLICT);
        }

        if(!userRepositoryImpl.checkOtp(body.getEmail(),body.getOtp())){
            response.setBody("Session expired, please try again");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        if(!userService.changePassword(body,user)){
            response.setBody("Incorrect info provided");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        response.setBody("Reset Done!!");
        response.setResponseCode(HttpStatus.CREATED.value());
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @PutMapping("/specific-job-details")
    public ResponseEntity<?> specificJobDetails(@RequestBody IdOnly body){
         ObjectId jobId = body.getJobId();
         ResponseType8 response = new ResponseType8();
         Job job = userRepositoryImpl.getJob(jobId);
         if(job==null){
             response.setBody("Please provide correct info");
             response.setResponseCode(HttpStatus.BAD_REQUEST.value());
             return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
         }
         JobData3 newJob = responseBuilderService.jobtoPojoBuild6(job);
        response.setBody("Data fetched");
        response.setJob(newJob);
        response.setResponseCode(HttpStatus.OK.value());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
