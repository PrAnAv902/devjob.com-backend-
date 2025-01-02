package com.devjobs.portal.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
public class UserData2 {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String fullName;
    private String email;
    private String company;
    private String picture;
    private String resume;
    private String totalExperience;
    private String city;
    private String state;
    private String country;
    private List<String> skills;
}