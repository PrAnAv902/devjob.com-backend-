package com.devjobs.portal.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseType5 {
    private String body;
    private UserData1 user;
    private Integer responseCode;
}