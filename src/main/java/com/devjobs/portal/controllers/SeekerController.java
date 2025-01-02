package com.devjobs.portal.controllers;


import com.devjobs.portal.entities.Job;
import com.devjobs.portal.entities.Profile;
import com.devjobs.portal.entities.User;
import com.devjobs.portal.model.request.ImageModel;
import com.devjobs.portal.model.response.ResponseType1;
import com.devjobs.portal.model.response.ResponseType5;
import com.devjobs.portal.model.response.UserData1;
import com.devjobs.portal.repositories.ProfileRepository;
import com.devjobs.portal.repositories.RepositoryImpl;
import com.devjobs.portal.service.CloudinaryService;
import com.devjobs.portal.service.JobService;
import com.devjobs.portal.service.ResponseBuilderService;
import com.devjobs.portal.service.UserService;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/seeker")
public class SeekerController {

    @Autowired
    private RepositoryImpl userRepositoryImpl;

    @Autowired
    private JobService jobService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private UserService userService;

    @Autowired
    private ResponseBuilderService responseBuilderService;


    @PutMapping("/apply/{jobId}")
    public ResponseEntity<?> jobApply(@PathVariable ObjectId jobId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepositoryImpl.getUser(email);

        ResponseType5 response = new ResponseType5();
        Job job = userRepositoryImpl.getJob(jobId);
        if(job==null){
            response.setBody("Please provide correct info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        if("CLOSED".equals(job.getJobStatus())){
            response.setBody("No longer accepting responses");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        List<User> appliedUsers = job.getCandidatesApplied();
        for(User it: appliedUsers){
            if(it.getId().toString().equals(user.getId().toString())){
                response.setBody("Already applied");
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
            }
        }
        User recruiter = userRepositoryImpl.getJobRecruiter(job);

        if(!jobService.apply(job,user,recruiter)){
            response.setBody("Please provide correct info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        UserData1 newUser = responseBuilderService.jobtoPojoBuild2(user);
        response.setBody("Applied");
        response.setResponseCode(HttpStatus.OK.value());
        response.setUser(newUser);
        return  new ResponseEntity<>(response,HttpStatus.OK);
    }


    @PutMapping("/save/{jobId}")
    public ResponseEntity<?> jobSave(@PathVariable ObjectId jobId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepositoryImpl.getUser(email);

        ResponseType5 response = new ResponseType5();
        Job job = userRepositoryImpl.getJob(jobId);
        if(job==null){
            response.setBody("Please provide correct info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        List<Job> savedJobs = user.getProfileId().getSavedJobs();
        for(Job it: savedJobs){
            if(it.getId().toString().equals(job.getId().toString())){
                response.setBody("Already saved");
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
            }
        }
        if(!jobService.save(job,user)){
            response.setBody("Please provide correct info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        UserData1 newUser = responseBuilderService.jobtoPojoBuild2(user);
        response.setBody("Saved");
        response.setResponseCode(HttpStatus.OK.value());
        response.setUser(newUser);
        return  new ResponseEntity<>(response,HttpStatus.OK);
    }


    @PutMapping("/unsave/{jobId}")
    public ResponseEntity<?> jobUnSave(@PathVariable ObjectId jobId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepositoryImpl.getUser(email);

        ResponseType5 response = new ResponseType5();
        Job job = userRepositoryImpl.getJob(jobId);
        if(job==null){
            response.setBody("Please provide correct info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        if(!jobService.unSave(job,user)){
            response.setBody("Please provide correct info");
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        UserData1 newUser = responseBuilderService.jobtoPojoBuild2(user);
        response.setBody("Unsaved");
        response.setResponseCode(HttpStatus.OK.value());
        response.setUser(newUser);
        return  new ResponseEntity<>(response,HttpStatus.OK);

    }


    @PostMapping("/update-profile")
    public ResponseEntity<?> createNewCompany(
             @RequestParam(required = false) MultipartFile picture
            ,@RequestParam(required = false) MultipartFile resume
            ,@RequestParam("fullName") String fullName
            ,@RequestParam(required = false) List<String> skills
            ,@RequestParam(required = false) String city
            ,@RequestParam(required = false) String state
            ,@RequestParam(required = false) String country
            ,@RequestParam(required = false) String totalExperience
            ,@RequestParam(required = false) String company) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepositoryImpl.getUser(email);
        ResponseType5 responseType1 = new ResponseType5();

        if(fullName==null || fullName.equals("")){
            responseType1.setBody("Please provide complete info");
            responseType1.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(responseType1,HttpStatus.BAD_REQUEST);
        }

        Tika tika = new Tika();
        if(picture!=null && (!"image/png".equals(tika.detect(picture.getBytes()))  && !"image/jpg".equals(tika.detect(picture.getBytes()))  && !"image/jpeg".equals(tika.detect(picture.getBytes())) )){
            responseType1.setBody("Picture format .jpeg .jpg .png only");
            responseType1.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(responseType1,HttpStatus.BAD_REQUEST);
        }

        if(resume!=null && !"application/pdf".equals(tika.detect(resume.getBytes()))){
            responseType1.setBody("Resume format .pdf only");
            responseType1.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(responseType1,HttpStatus.BAD_REQUEST);
        }

        if(!userService.updateProfile(city,state,country,company,totalExperience,skills,user.getProfileId())){
            responseType1.setBody("Please provide complete info");
            responseType1.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(responseType1,HttpStatus.BAD_REQUEST);
        }

        if(picture!=null && !cloudinaryService.uploadResumeOrPicture(picture,user,true)){
            responseType1.setBody("Please provide complete info");
            responseType1.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(responseType1,HttpStatus.BAD_REQUEST);
        }

        if(resume!=null && !cloudinaryService.uploadResumeOrPicture(resume,user,false)){
            responseType1.setBody("Please provide complete info");
            responseType1.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(responseType1,HttpStatus.BAD_REQUEST);
        }

        UserData1 newUser = responseBuilderService.jobtoPojoBuild2(user);
        responseType1.setUser(newUser);
        responseType1.setBody("Profile Updated");
        responseType1.setResponseCode(HttpStatus.OK.value());
        return new ResponseEntity<>(responseType1,HttpStatus.OK);
    }


    @PutMapping("/get-saved-jobs")
    public void allSavedJobs(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepositoryImpl.getUser(email);
        ResponseType5 responseType1 = new ResponseType5();
    }
}
