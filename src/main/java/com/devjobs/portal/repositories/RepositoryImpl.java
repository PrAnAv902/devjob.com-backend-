package com.devjobs.portal.repositories;


import com.devjobs.portal.entities.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class RepositoryImpl {

    private final MongoTemplate mongoTemplate;

    @Autowired
    RepositoryImpl(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    public User getUser(String email){
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        List<User> user =  mongoTemplate.find(query, User.class);
        if(user.isEmpty()) return null;
        return user.get(0);
    }

    public boolean checkOtp(String email,Integer otp){
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        List<Otp> userOtp =  mongoTemplate.find(query, Otp.class);
        if(userOtp.isEmpty()) return false;
        userOtp.sort((a,b)-> b.getCreatedAt().compareTo(a.getCreatedAt()));
        Duration duration = Duration.between(userOtp.get(0).getCreatedAt(), LocalDateTime.now());
        return (duration.getSeconds()<=300 && userOtp.get(0).getOtp().equals(otp));
    }

    public Job getJob(ObjectId jobId){
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(jobId));
        List<Job> job =  mongoTemplate.find(query, Job.class);
        if(job.isEmpty()) return null;
        return job.get(0);
    }

    public User getJobRecruiter(Job job){
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(job.getCreatorId()));
        List<User> users =  mongoTemplate.find(query, User.class);
        if(users==null) return null;
        return users.get(0);
    }

    public List<User> getAllUserWithRoleSeeker(){
        Query query = new Query();
        query.addCriteria(Criteria.where("role").is("SEEKER"));
        return mongoTemplate.find(query, User.class);
    }



    public List<Job> getJobsForHome(){
        Query query = new Query();
        query.addCriteria(Criteria.where("jobStatus").is("ACTIVE"));
        List<Job> jobs =  mongoTemplate.find(query, Job.class);
        jobs.sort((a,b) -> b.getCreationDetails().compareTo(a.getCreationDetails()));
        List<Job> data = new ArrayList<>();
        for(int i=0;jobs.size()>i && i<=3;i++) data.add(jobs.get(i));
        return data;
    }

    public List<Job> getJobsForSearchName(String search){
        String data = search.replace("$","").replace("^","").replace("%","").replace("]","").replace("[","").replace("\\","").replace("-","");
        if(data.isEmpty()) return new ArrayList<>();
        Query query = new Query();
        query.addCriteria(Criteria.where("jobStatus").exists(true));
        List<Job> jobs = mongoTemplate.find(query, Job.class);
        List<Job> newJobs = new ArrayList<>();
        for(Job job:jobs){
            if(job.getTitle().toLowerCase().contains(data.toLowerCase()) || job.getCompany().toLowerCase().contains(data.toLowerCase())){
                newJobs.add(job);
            }
        }
        return newJobs;
    }

    public List<Job> getJobsForSearchLocation(String search){
        String data = search.replace("$","").replace("^","").replace("%","").replace("]","").replace("[","").replace("\\","").replace("-","");
        if(data.isEmpty()) return new ArrayList<>();
        Query query = new Query();
        query.addCriteria(Criteria.where("jobStatus").exists(true));
        List<Job> jobs = mongoTemplate.find(query, Job.class);
        List<Job> newJobs = new ArrayList<>();
        for(Job job:jobs){
            if(job.getCountry().toLowerCase().contains(data.toLowerCase()) || job.getCity().toLowerCase().contains(data.toLowerCase())){
                newJobs.add(job);
            }
        }
        return newJobs;
    }

    public Company getCompany(String companyName){
        Query query = new Query();
        query.addCriteria(Criteria.where("company").is(companyName));
        List<Company> company =  mongoTemplate.find(query, Company.class);
        if(company.isEmpty()) return null;
        return company.get(0);
    }

    public List<Company> getAllCompanies(){
        Query query = new Query();
        query.addCriteria(Criteria.where("company").exists(true));
        return  mongoTemplate.find(query, Company.class);
    }

    public List<User> getUserForSearchName(String search){
        String data = search.replace("$","").replace("^","").replace("%","").replace("]","").replace("[","").replace("\\","").replace("-","");
        if(data.isEmpty()) return new ArrayList<>();
        Query query = new Query();
        query.addCriteria(Criteria.where("role").is("SEEKER"));
        List<User> users = mongoTemplate.find(query, User.class);
        List<User> newUsers = new ArrayList<>();
        for(User user:users){
            if(user.getFullName().toLowerCase().contains(data.toLowerCase())
                    || user.getProfileId().getCompany().toLowerCase().contains(data.toLowerCase())
                    || user.getProfileId().getCountry().toLowerCase().contains(data.toLowerCase())
                    || user.getProfileId().getCity().toLowerCase().contains(data.toLowerCase())){
                newUsers.add(user);
            }
        }
        return newUsers;
    }
}
