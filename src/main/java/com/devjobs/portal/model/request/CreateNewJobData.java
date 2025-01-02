package com.devjobs.portal.model.request;


import com.devjobs.portal.entities.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CreateNewJobData {
    private String title;
    private String roleType;
    private String workingMode;
    private String salary;
    private String description;
    private String company;
    private String companyImage;
    private String city;
    private String state;
    private String country;
}

