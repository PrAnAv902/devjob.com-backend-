package com.devjobs.portal.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseType6 {
    private String body;
    private List<UserData2> users;
    private Integer responseCode;
}