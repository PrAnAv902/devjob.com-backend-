package com.devjobs.portal.entities;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "Otps")
@Getter
@Setter
public class Otp {
    @Id
    private ObjectId id;

    private String email;

    private Integer otp;

    private LocalDateTime createdAt;
}
