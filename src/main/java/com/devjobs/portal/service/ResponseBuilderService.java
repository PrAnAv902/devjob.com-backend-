package com.devjobs.portal.service;
import com.devjobs.portal.entities.Company;
import com.devjobs.portal.entities.Job;
import com.devjobs.portal.entities.User;
import com.devjobs.portal.model.request.CompanyData;
import com.devjobs.portal.model.response.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResponseBuilderService {
    public List<JobData1> jobtoPojoBuild(List<Job> jobs){
        List<JobData1> newJobs = new ArrayList<>();
        for(Job job:jobs){
            JobData1 jobData = new JobData1();
            jobData.setId(job.getId());
            jobData.setTitle(job.getTitle());
            jobData.setCompany(job.getCompany());
            jobData.setSalary(job.getSalary());
            jobData.setRoleType(job.getRoleType());
            jobData.setWorkingMode(job.getWorkingMode());
            jobData.setCity(job.getCity());
            jobData.setCountry(job.getCountry());
            jobData.setJobStatus(job.getJobStatus());
            jobData.setCompanyImage(job.getCompanyImage());
            jobData.setCreationDetails(job.getCreationDetails());
            newJobs.add(jobData);
        }
        return newJobs;
    }

    public UserData1 jobtoPojoBuild2(User user){
        UserData1 newUser = new UserData1();
        newUser.setFullName(user.getFullName());
        newUser.setRole(user.getRole());
        newUser.setEmail(user.getEmail());
        if("SEEKER".equals(user.getRole())){
            newUser.setCompany(user.getProfileId().getCompany());
            newUser.setCity(user.getProfileId().getCity());
            newUser.setCountry(user.getProfileId().getCountry());
            newUser.setState(user.getProfileId().getState());
            newUser.setPicture(user.getProfileId().getPicture());
            newUser.setSkills(user.getProfileId().getSkills());
            newUser.setResume(user.getProfileId().getResume());
            newUser.setTotalExperience(user.getProfileId().getTotalExperience());
            newUser.setAppliedJobs(jobtoPojoBuild(user.getProfileId().getAppliedJobs()));
            newUser.setSavedJobs(jobtoPojoBuild(user.getProfileId().getSavedJobs()));
        }
        else if("RECRUITER".equals(user.getRole())) {
            newUser.setCreatedJobs(jobtoPojoBuild(user.getProfileId().getCreatedJobs()));
        }
        return newUser;
    }

    public List<CompanyData1> jobtoPojoBuild3(List<Company> companies){
        List<CompanyData1> newCompanies = new ArrayList<>();
        for(Company company:companies){
            CompanyData1 companyData = new CompanyData1();
            companyData.setCompany(company.getCompany());
            companyData.setCompanyImage(company.getCompanyImage());
            newCompanies.add(companyData);
        }
        return newCompanies;
    }

    public List<UserData2> jobtoPojoBuild4(List<User> users){
        List<UserData2> newUsers = new ArrayList<>();
        for(User user:users){
            UserData2 newUser = new UserData2();
            newUser.setId(user.getId());
            newUser.setFullName(user.getFullName());
            newUser.setEmail(user.getEmail());
            newUser.setCompany(user.getProfileId().getCompany());
            newUser.setCity(user.getProfileId().getCity());
            newUser.setCountry(user.getProfileId().getCountry());
            newUser.setState(user.getProfileId().getState());
            newUser.setPicture(user.getProfileId().getPicture());
            newUser.setSkills(user.getProfileId().getSkills());
            newUser.setResume(user.getProfileId().getResume());
            newUser.setTotalExperience(user.getProfileId().getTotalExperience());
            newUsers.add(newUser);
        }
        return newUsers;
    }

    public JobData2 jobtoPojoBuild5(Job job){
        JobData2 jobData = new JobData2();
        jobData.setId(job.getId());
        jobData.setTitle(job.getTitle());
        jobData.setCompany(job.getCompany());
        jobData.setSalary(job.getSalary());
        jobData.setRoleType(job.getRoleType());
        jobData.setWorkingMode(job.getWorkingMode());
        jobData.setCity(job.getCity());
        jobData.setCountry(job.getCountry());
        jobData.setState(job.getState());
        jobData.setDescription(job.getDescription());
        return jobData;
    }

    public JobData3 jobtoPojoBuild6(Job job){
        JobData3 jobData = new JobData3();
        jobData.setId(job.getId());
        jobData.setTitle(job.getTitle());
        jobData.setCompany(job.getCompany());
        jobData.setSalary(job.getSalary());
        jobData.setRoleType(job.getRoleType());
        jobData.setWorkingMode(job.getWorkingMode());
        jobData.setCity(job.getCity());
        jobData.setCountry(job.getCountry());
        jobData.setState(job.getState());
        jobData.setDescription(job.getDescription());
        jobData.setAppliedCount(String.valueOf(job.getCandidatesApplied().size()));
        jobData.setJobStatus(job.getJobStatus());
        jobData.setCreationDetails(job.getCreationDetails());
        jobData.setCompanyImage(job.getCompanyImage());

        return jobData;
    }
}
