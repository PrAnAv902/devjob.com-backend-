package com.devjobs.portal.controllers;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.devjobs.portal.entities.Company;
import com.devjobs.portal.model.request.CompanyNameOnly;
import com.devjobs.portal.model.request.ImageModel;
import com.devjobs.portal.model.request.SearchData;
import com.devjobs.portal.model.response.ResponseType1;
import com.devjobs.portal.repositories.CompanyRepository;
import com.devjobs.portal.repositories.RepositoryImpl;
import com.devjobs.portal.service.CloudinaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {
   @Autowired
   private CloudinaryService cloudinaryService;

   @Autowired
   private RepositoryImpl repositoryImpl;

    @Autowired
    CompanyRepository companyRepository;

    @PostMapping("/add-company")
    public ResponseEntity<?> createNewCompany(@RequestParam("companyImage") MultipartFile companyImage,@RequestParam("company") String company){
            SecurityContextHolder.getContext().getAuthentication();
            ResponseType1 responseType1 = new ResponseType1();
            if(companyImage==null || company==null){
                responseType1.setBody("Please provide complete info");
                responseType1.setResponseCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(responseType1,HttpStatus.BAD_REQUEST);
            }
            ImageModel imageModel = new ImageModel();
            imageModel.setCompany(company);
            imageModel.setCompanyImage(companyImage);
            if(repositoryImpl.getCompany(company)!=null){
                responseType1.setBody("Company already exist");
                responseType1.setResponseCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(responseType1,HttpStatus.BAD_REQUEST);
            }
            if(!cloudinaryService.uploadImage(imageModel)){
                responseType1.setBody("Please provide complete info");
                responseType1.setResponseCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(responseType1,HttpStatus.BAD_REQUEST);
            }
            responseType1.setBody("Company created");
            responseType1.setResponseCode(HttpStatus.OK.value());
            return new ResponseEntity<>(responseType1,HttpStatus.OK);
    }

    @PostMapping("/delete-company")
    public ResponseEntity<?> removeCompany(@RequestBody CompanyNameOnly body){
        SecurityContextHolder.getContext().getAuthentication();
        String company = body.getCompanyName();
        ResponseType1 responseType1 = new ResponseType1();
        Company companyData = repositoryImpl.getCompany(company);
        if(companyData==null){
            responseType1.setBody("Company doesn't exist");
            responseType1.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(responseType1,HttpStatus.BAD_REQUEST);
        }
        if(!cloudinaryService.removeImage(companyData)){
            responseType1.setBody("Please provide correct info");
            responseType1.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(responseType1,HttpStatus.BAD_REQUEST);
        }
        companyRepository.deleteById(companyData.getId());
        responseType1.setBody("Company deleted");
        responseType1.setResponseCode(HttpStatus.OK.value());
        return new ResponseEntity<>(responseType1,HttpStatus.OK);
    }
}
