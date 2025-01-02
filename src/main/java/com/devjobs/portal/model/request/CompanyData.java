package com.devjobs.portal.model.request;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class CompanyData {
    private  String company;
    private File companyImage;
}
