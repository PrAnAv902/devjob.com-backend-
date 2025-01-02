package com.devjobs.portal.entities;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Profiles")
@Getter
@Setter
public class Profile {
    @Id
    private ObjectId id;

    private String company;

    private String picture;

    private String resume;

    private String totalExperience;

    private String city;

    private String state;

    private String country;

    private List<String> skills;

    private List<Job> appliedJobs;

    private List<Job> savedJobs;

    private List<Job> createdJobs;
}
