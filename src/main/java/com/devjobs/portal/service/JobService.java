package com.devjobs.portal.service;


import com.devjobs.portal.entities.Company;
import com.devjobs.portal.entities.Job;
import com.devjobs.portal.entities.Profile;
import com.devjobs.portal.entities.User;
import com.devjobs.portal.model.request.CreateNewJobData;
import com.devjobs.portal.model.request.StatusChangeData;
import com.devjobs.portal.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class JobService {
    @Autowired
    JobRepository jobRepository;

    @Autowired
    ProfileRepository profileRepository;


    @Autowired
    private RepositoryImpl userRepositoryImpl;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public boolean creatingNewJob(User user, CreateNewJobData jobData){
        try{
            Job newJob = new Job();
            newJob.setTitle(jobData.getTitle());
            newJob.setRoleType(jobData.getRoleType());
            newJob.setWorkingMode(jobData.getWorkingMode());
            newJob.setSalary(jobData.getSalary());
            newJob.setDescription(jobData.getDescription());
            newJob.setCompany(jobData.getCompany());
            newJob.setCandidatesApplied(new ArrayList<>());
            newJob.setJobStatus("ACTIVE");
            newJob.setCreationDetails(LocalDateTime.now());
            newJob.setJobStatusChangeDetails(LocalDateTime.now());
            newJob.setCity(jobData.getCity());
            newJob.setCountry(jobData.getCountry());
            newJob.setState(jobData.getState());
            newJob.setCreatorId(user.getId());
            Company company = userRepositoryImpl.getCompany(jobData.getCompany());
            newJob.setCompanyImage(company.getCompanyImage());
            Job savedJob = jobRepository.save(newJob);
            Profile userProfile = user.getProfileId();
            List<Job> jobList = userProfile.getCreatedJobs();
            jobList.add(savedJob);
            userProfile.setCreatedJobs(jobList);
            profileRepository.save(userProfile);
            return true;
        }
        catch(Exception e){
            log.error("An error occurred while creating a new job");
            return false;
        }
    }

    @Transactional
    public boolean deleteJob(Job job){
        try{
            List<User> allSeekers = userRepositoryImpl.getAllUserWithRoleSeeker();
            for(User user:allSeekers){
                Profile userProfile = user.getProfileId();
                List<Job> jobList1 = userProfile.getAppliedJobs();
                jobList1.removeIf(tjob-> tjob.getId().toString().equals(job.getId().toString()));
                userProfile.setAppliedJobs(jobList1);
                List<Job> jobList2 = userProfile.getSavedJobs();
                jobList2.removeIf(tjob-> tjob.getId().toString().equals(job.getId().toString()));
                userProfile.setSavedJobs(jobList2);
                profileRepository.save(userProfile);
            }
            return true;
        }
        catch(Exception e){
            log.error("An error occurred while deleting job");
            return false;
        }
    }

    @Transactional
    public boolean apply(Job job,User user,User recruiter){
        try{
            List<User> newCandidatesApplied = job.getCandidatesApplied();
            newCandidatesApplied.add(user);
            job.setCandidatesApplied(newCandidatesApplied);
            jobRepository.save(job);

            Profile recruiterProfile = recruiter.getProfileId();
            List<Job> jobList1 = recruiterProfile.getCreatedJobs();
            jobList1.removeIf(tjob-> tjob.getId().toString().equals(job.getId().toString()));
            jobList1.add(job);
            recruiterProfile.setCreatedJobs(jobList1);
            profileRepository.save(recruiterProfile);

            Profile userProfile = user.getProfileId();
            List<Job> jobList2 = userProfile.getAppliedJobs();
            jobList2.add(job);
            userProfile.setAppliedJobs(jobList2);
            profileRepository.save(userProfile);

            return true;
        }
        catch(Exception e){
            log.error("An error occurred while applying a job");
            return false;
        }
    }


    @Transactional
    public boolean save(Job job,User user){
        try{
            Profile userProfile = user.getProfileId();
            List<Job> jobList2 = userProfile.getSavedJobs();
            jobList2.add(job);
            userProfile.setSavedJobs(jobList2);
            profileRepository.save(userProfile);
            return true;
        }
        catch(Exception e){
            log.error("An error occurred while saving a job");
            return false;
        }
    }

    @Transactional
    public boolean unSave(Job job,User user){
        try{
            Profile userProfile = user.getProfileId();
            List<Job> jobList2 = userProfile.getSavedJobs();
            jobList2.removeIf(tjob-> tjob.getId().toString().equals(job.getId().toString()));
            userProfile.setSavedJobs(jobList2);
            profileRepository.save(userProfile);
            return true;
        }
        catch(Exception e){
            log.error("An error occurred while unsaving a job");
            return false;
        }
    }


    @Transactional
    public boolean jobStatusChange(Job job,String newStatus,User user){
        try{
            job.setJobStatus(newStatus);
            jobRepository.save(job);
            if(user.getProfileId().getCreatedJobs().removeIf(jobe-> jobe.getId().toString().equals(job.getId().toString()))){
                List<Job> jobs = user.getProfileId().getCreatedJobs();
                jobs.add(job);
                user.getProfileId().setCreatedJobs(jobs);
                profileRepository.save(user.getProfileId());
            }

            List<User> seekerUsers = userRepositoryImpl.getAllUserWithRoleSeeker();
            for(User userr:seekerUsers){
                boolean flag = false;
                if(userr.getProfileId().getAppliedJobs().removeIf(jobe-> jobe.getId().toString().equals(job.getId().toString()))){
                    List<Job> jobs = userr.getProfileId().getAppliedJobs();
                    jobs.add(job);
                    userr.getProfileId().setAppliedJobs(jobs);
                    flag = true;
                }
                if(userr.getProfileId().getSavedJobs().removeIf(jobe-> jobe.getId().toString().equals(job.getId().toString()))){
                    List<Job> jobs = userr.getProfileId().getSavedJobs();
                    jobs.add(job);
                    userr.getProfileId().setSavedJobs(jobs);
                    flag = true;
                }
                if(flag) profileRepository.save(userr.getProfileId());
            }
           return true;
        }
        catch(Exception e){
            log.error("An error occurred while change job status");
            return false;
        }
    }

    @Transactional
    public boolean editJob(User user, CreateNewJobData jobData, Job newJob){
        try{
            newJob.setTitle(jobData.getTitle());
            newJob.setRoleType(jobData.getRoleType());
            newJob.setWorkingMode(jobData.getWorkingMode());
            newJob.setSalary(jobData.getSalary());
            newJob.setDescription(jobData.getDescription());
            newJob.setCompany(jobData.getCompany());
            newJob.setCity(jobData.getCity());
            newJob.setCountry(jobData.getCountry());
            newJob.setState(jobData.getState());
            Company company = userRepositoryImpl.getCompany(jobData.getCompany());
            newJob.setCompanyImage(company.getCompanyImage());
            Job job = jobRepository.save(newJob);

            if(user.getProfileId().getCreatedJobs().removeIf(jobe-> jobe.getId().toString().equals(job.getId().toString()))){
                List<Job> jobs = user.getProfileId().getCreatedJobs();
                jobs.add(job);
                user.getProfileId().setCreatedJobs(jobs);
                profileRepository.save(user.getProfileId());
            }

            List<User> seekerUsers = userRepositoryImpl.getAllUserWithRoleSeeker();
            for(User userr:seekerUsers){
                boolean flag = false;
                if(userr.getProfileId().getAppliedJobs().removeIf(jobe-> jobe.getId().toString().equals(job.getId().toString()))){
                    List<Job> jobs = userr.getProfileId().getAppliedJobs();
                    jobs.add(job);
                    userr.getProfileId().setAppliedJobs(jobs);
                    flag = true;
                }
                if(userr.getProfileId().getSavedJobs().removeIf(jobe-> jobe.getId().toString().equals(job.getId().toString()))){
                    List<Job> jobs = userr.getProfileId().getSavedJobs();
                    jobs.add(job);
                    userr.getProfileId().setSavedJobs(jobs);
                    flag = true;
                }
                if(flag) profileRepository.save(userr.getProfileId());
            }
            return true;
        }
        catch(Exception e){
            log.error("An error occurred while editing a job");
            return false;
        }
    }

}

