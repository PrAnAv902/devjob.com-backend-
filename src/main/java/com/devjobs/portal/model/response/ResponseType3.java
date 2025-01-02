package com.devjobs.portal.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseType3 {
    private List<JobData1> jobs;
    private Integer responseCode;
}
