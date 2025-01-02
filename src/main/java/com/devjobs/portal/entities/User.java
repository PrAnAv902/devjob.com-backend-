package com.devjobs.portal.entities;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Users")
@Getter
@Setter
public class User {
    @Id
    private ObjectId id;
    @NonNull
    private String fullName;
    @NonNull
    private String email;
    @NonNull
    private String password;
    @NonNull
    private String role;
    @DBRef
    private Profile profileId;
}
