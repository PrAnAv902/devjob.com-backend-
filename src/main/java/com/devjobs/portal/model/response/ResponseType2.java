package com.devjobs.portal.model.response;

import com.devjobs.portal.entities.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseType2 {
    private String body;
    private String token;
    private UserData1 user;
    private Integer responseCode;
}
