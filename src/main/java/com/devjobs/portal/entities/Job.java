package com.devjobs.portal.entities;


import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "Jobs")
@Getter
@Setter
public class Job {
    @Id
    private ObjectId id;

    @NonNull
    private String title;

    @NonNull
    private String roleType;

    @NonNull
    private String workingMode;

    @NonNull
    private String salary;

    @NonNull
    private String description;

    @NonNull
    private String company;

    @NonNull
    private String companyImage;

    private List<User> candidatesApplied;

    @NonNull
    private String jobStatus;

    @NonNull
    private String city;

    @NonNull
    private String state;

    @NonNull
    private String country;

    @NonNull
    private ObjectId creatorId;

    private LocalDateTime creationDetails;

    private LocalDateTime jobStatusChangeDetails;


}
