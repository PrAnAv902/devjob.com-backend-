package com.devjobs.portal.entities;


import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "Companies")
public class Company {
    @Id
    private ObjectId id;
    private String cloudinaryName;
    private String company;
    private String companyImage;
}
