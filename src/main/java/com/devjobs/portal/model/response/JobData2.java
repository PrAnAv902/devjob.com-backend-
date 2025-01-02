package com.devjobs.portal.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;


@Getter
@Setter
public class JobData2 {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String title;
    private String roleType;
    private String workingMode;
    private String salary;
    private String company;
    private String city;
    private String country;
    private String state;
    private String description;
}
