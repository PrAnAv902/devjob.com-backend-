package com.devjobs.portal.model.response;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserData1 {
    private String fullName;
    private String email;
    private String role;
    private String company;
    private String picture;
    private String resume;
    private String totalExperience;
    private String city;
    private String state;
    private String country;
    private List<String> skills;
    private List<JobData1> createdJobs;
    private List<JobData1> appliedJobs;
    private List<JobData1> savedJobs;
}
