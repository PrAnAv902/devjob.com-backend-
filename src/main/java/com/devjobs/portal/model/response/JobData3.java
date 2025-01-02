package com.devjobs.portal.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Getter
@Setter
public class JobData3 {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String title;
    private String roleType;
    private String workingMode;
    private String salary;
    private String company;
    private String companyImage;
    private String city;
    private String country;
    private String state;
    private String description;
    private String appliedCount;
    private String jobStatus;
    private LocalDateTime creationDetails;
}
