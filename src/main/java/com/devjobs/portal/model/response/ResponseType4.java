package com.devjobs.portal.model.response;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseType4 {
    private List<CompanyData1> companies;
    private Integer responseCode;
}
