package com.devjobs.portal.controllers;


import com.devjobs.portal.entities.Company;
import com.devjobs.portal.entities.Job;
import com.devjobs.portal.entities.Profile;
import com.devjobs.portal.entities.User;
import com.devjobs.portal.model.request.CreateNewJobData;
import com.devjobs.portal.model.request.IdOnly;
import com.devjobs.portal.model.request.SearchData;
import com.devjobs.portal.model.request.StatusChangeData;
import com.devjobs.portal.model.response.*;
import com.devjobs.portal.repositories.JobRepository;
import com.devjobs.portal.repositories.ProfileRepository;
import com.devjobs.portal.repositories.RepositoryImpl;

import com.devjobs.portal.repositories.UserRepository;
import com.devjobs.portal.service.JobService;
import com.devjobs.portal.service.ResponseBuilderService;
import com.devjobs.portal.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/recruiter")
public class RecruiterController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RepositoryImpl userRepositoryImpl;

    @Autowired
    private JobService jobService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ResponseBuilderService responseBuilderService;

    @Autowired
    private UserRepository userRepository;



    @PostMapping("/create-new-job")
    public ResponseEntity<?> createNewJob(@RequestBody CreateNewJobData jobData){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepositoryImpl.getUser(email);

        ResponseType5 response = new ResponseType5();

        if(jobData.getCity()==null || jobData.getSalary()==null || jobData.getCompany()==null || jobData.getCountry()==null || jobData.getDescription()==null ||
                jobData.getTitle()==null  || jobData.getRoleType()==null || jobData.getState()==null || jobData.getWorkingMode()==null || jobData.getCompanyImage()==null ||
                jobData.getCity().isBlank() || jobData.getSalary().isBlank()|| jobData.getCompany().isBlank() || jobData.getCountry().isBlank() || jobData.getDescription().isBlank() ||
                jobData.getTitle().isBlank() || jobData.getRoleType().isBlank() || jobData.getState().isBlank() || jobData.getWorkingMode().isBlank() || jobData.getCompanyImage().isBlank()){
            response.setBody("Please provide complete info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        if(!jobService.creatingNewJob(user,jobData)){
            response.setBody("Please provide complete info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        UserData1 userData = responseBuilderService.jobtoPojoBuild2(user);
        response.setBody("Job Created");
        response.setResponseCode(HttpStatus.OK.value());
        response.setUser(userData);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping("/delete-job/{jobId}")
    public ResponseEntity<?> deleteJob(@PathVariable ObjectId jobId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepositoryImpl.getUser(email);
        ResponseType1 response = new ResponseType1();
        Job job = userRepositoryImpl.getJob(jobId);
        if(job==null){
            response.setBody("Please provide correct info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        boolean flag = false;
        List<Job> createdJobs = user.getProfileId().getCreatedJobs();
        for(Job it:createdJobs){
            if(it.getId().toString().equals(job.getId().toString())){
                flag = true;
                break;
            }
        }

        if(!flag){
            response.setBody("You don't have enough access");
            response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
            return  new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
        }

        if(!jobService.deleteJob(job)){
            response.setBody("Please provide correct info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        //deleting from users->profile[recruiter]->created->array
        Profile userProfile = user.getProfileId();
        List<Job> jobList = userProfile.getCreatedJobs();
        jobList.removeIf(tjob-> tjob.getId().toString().equals(job.getId().toString()));
        userProfile.setCreatedJobs(jobList);
        profileRepository.save(userProfile);
        //deleting from job entity
        jobRepository.delete(job);

        response.setBody("Job deleted");
        response.setResponseCode(HttpStatus.OK.value());
        return  new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/fetch-companies")
    public ResponseEntity<?> fetchAllCompanies(){
        ResponseType4 responseType4 = new ResponseType4();
        List<Company> comapnies = userRepositoryImpl.getAllCompanies();
        List<CompanyData1> newCompanies = responseBuilderService.jobtoPojoBuild3(comapnies);
        responseType4.setCompanies(newCompanies);
        responseType4.setResponseCode(HttpStatus.OK.value());
        return new ResponseEntity<>(responseType4,HttpStatus.OK);
    }

    @PutMapping("/fetch-applicants-list")
    public ResponseEntity<?> fetchApplicants(@RequestBody IdOnly body){
        ObjectId jobId = body.getJobId();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ResponseType6 response = new ResponseType6();
        if(jobId==null){
            response.setBody("Please provide correct info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        User user = userRepositoryImpl.getUser(email);

        Job job = userRepositoryImpl.getJob(jobId);
        if(job==null){
            response.setBody("Please provide correct info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        boolean flag = false;
        for(Job jobe:user.getProfileId().getCreatedJobs()){
            if(jobe.getId().toString().equals(job.getId().toString())){
                flag = true;
                break;
            }
        }

        if(!flag){
            response.setBody("Don't have access");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        List<UserData2> users = responseBuilderService.jobtoPojoBuild4(job.getCandidatesApplied());
        response.setBody("Applicants fetched");
        response.setResponseCode(HttpStatus.OK.value());
        response.setUsers(users);
        return  new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("/change-job-status")
    public ResponseEntity<?> changeJobStatus(@RequestBody StatusChangeData body){
        ObjectId jobId = body.getJobId();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepositoryImpl.getUser(email);
        ResponseType1 response = new ResponseType1();
        if(jobId==null || body.getJobStatus()==null || body.getJobStatus().isBlank()){
            response.setBody("Please provide correct info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        Job job = userRepositoryImpl.getJob(jobId);
        if(job==null){
            response.setBody("Please provide correct info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        boolean flag = false;
        for(Job jobe:user.getProfileId().getCreatedJobs()){
            if(jobe.getId().toString().equals(job.getId().toString())){
                flag = true;
                break;
            }
        }

        if(!flag){
            response.setBody("Don't have access");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        if(!Objects.equals(body.getJobStatus(), "ACTIVE") && !"CLOSED".equals(body.getJobStatus())){
            response.setBody("Please provide correct info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        if(!job.getJobStatus().equals(body.getJobStatus())){
            boolean res = jobService.jobStatusChange(job, body.getJobStatus(),user);
            if(!res){
                response.setBody("Don't have access");
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
            }
        }

        response.setBody("Status changed");
        response.setResponseCode(HttpStatus.OK.value());
        return  new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("/edit-job-data")
    public ResponseEntity<?> editJobData(@RequestBody IdOnly body){
        ObjectId jobId = body.getJobId();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ResponseType7 response = new ResponseType7();
        if(jobId==null){
            response.setBody("Please provide correct info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        User user = userRepositoryImpl.getUser(email);

        Job job = userRepositoryImpl.getJob(jobId);
        if(job==null){
            response.setBody("Please provide correct info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        boolean flag = false;
        for(Job jobe:user.getProfileId().getCreatedJobs()){
            if(jobe.getId().toString().equals(job.getId().toString())){
                flag = true;
                break;
            }
        }
        if(!flag){
            response.setBody("Don't have access");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        JobData2 newJob = responseBuilderService.jobtoPojoBuild5(job);
        response.setBody("Job details fetched");
        response.setResponseCode(HttpStatus.OK.value());
        response.setJob(newJob);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @PostMapping("/edit-job/{jobId}")
    public ResponseEntity<?> editJob(@PathVariable ObjectId jobId,@RequestBody CreateNewJobData jobData){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepositoryImpl.getUser(email);
        Job job  = userRepositoryImpl.getJob(jobId);
        ResponseType5 response = new ResponseType5();

        if(jobData.getCity()==null || jobData.getSalary()==null || jobData.getCompany()==null || jobData.getCountry()==null || jobData.getDescription()==null ||
                jobData.getTitle()==null  || jobData.getRoleType()==null || jobData.getState()==null || jobData.getWorkingMode()==null || jobData.getCompanyImage()==null ||
                jobData.getCity().isBlank() || jobData.getSalary().isBlank()|| jobData.getCompany().isBlank() || jobData.getCountry().isBlank() || jobData.getDescription().isBlank() ||
                jobData.getTitle().isBlank() || jobData.getRoleType().isBlank() || jobData.getState().isBlank() || jobData.getWorkingMode().isBlank() || jobData.getCompanyImage().isBlank()){
            response.setBody("Please provide complete info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        if(job==null){
            response.setBody("Please provide correct info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        boolean flag = false;
        for(Job jobb:user.getProfileId().getCreatedJobs()){
            if(jobb.getId().toString().equals(jobId.toString())){
                flag = true;
                break;
            }
        }

        if(!flag){
            response.setBody("Don't have access");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        if(!jobService.editJob(user,jobData,job)){
            response.setBody("Please provide complete info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        UserData1 userData = responseBuilderService.jobtoPojoBuild2(user);
        response.setBody("Changes Done");
        response.setResponseCode(HttpStatus.OK.value());
        response.setUser(userData);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("/search-candidate")
    public ResponseEntity<?> searchApplicants(@RequestBody SearchData search){
        SecurityContextHolder.getContext().getAuthentication();
        ResponseType6 response = new ResponseType6();
        List<User> users = userRepositoryImpl.getUserForSearchName(search.getSearch());
        List<UserData2> newUsers = responseBuilderService.jobtoPojoBuild4(users);
        response.setUsers(newUsers);
        response.setBody("Please provide correct info");
        response.setResponseCode(HttpStatus.OK.value());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
