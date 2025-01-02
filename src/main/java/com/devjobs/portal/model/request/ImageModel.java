package com.devjobs.portal.model.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ImageModel {
    private String company;
    private MultipartFile companyImage;
}