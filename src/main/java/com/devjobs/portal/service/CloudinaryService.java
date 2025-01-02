package com.devjobs.portal.service;



import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import com.devjobs.portal.entities.Company;
import com.devjobs.portal.entities.Profile;
import com.devjobs.portal.entities.User;
import com.devjobs.portal.model.request.ImageModel;
import com.devjobs.portal.repositories.CompanyRepository;
import com.devjobs.portal.repositories.ProfileRepository;
import com.devjobs.portal.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@Service
public class CloudinaryService {

    @Value("${CLOUDINARY_URL}")
    private String cloudUrl;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    ProfileRepository profileRepository;

    public boolean uploadImage(ImageModel imageModel) {
        try {
            Cloudinary cloudinary = new Cloudinary(cloudUrl);
            Map params1 = ObjectUtils.asMap(
                    "use_filename", false,
                    "unique_filename", true,
                    "overwrite", false
            );

            Company company = new Company();
            company.setCompany(imageModel.getCompany());
            Map response = cloudinary.uploader().upload(imageModel.getCompanyImage().getBytes(), params1);
            if(response.get("secure_url") == null || response.get("display_name") == null || String.valueOf(response.get("secure_url")).isBlank() || String.valueOf(response.get("display_name")).isBlank()) {
                return false;
            }
            company.setCompanyImage(String.valueOf(response.get("secure_url")).replace("/upload/","/upload/h_225,w_225/"));
            company.setCloudinaryName(String.valueOf(response.get("display_name")));
            companyRepository.save(company);
            return true;
        } catch (Exception e) {
            log.error("An error occurred while uploading image on cloudinary");
            return false;
        }
    }

    public boolean removeImage(Company company){
        try {
            Cloudinary cloudinary = new Cloudinary(cloudUrl);
            cloudinary.api().deleteResources(Arrays.asList(company.getCloudinaryName()),ObjectUtils.asMap("type", "upload", "resource_type", "image"));
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public boolean removeResumeOrPicture(String fileName){
        try {
            Cloudinary cloudinary = new Cloudinary(cloudUrl);
            cloudinary.api().deleteResources(Arrays.asList(fileName),ObjectUtils.asMap("type", "upload", "resource_type", "image"));
            return true;
        } catch (Exception exception) {
            return false;
        }
    }


    public boolean uploadResumeOrPicture(MultipartFile imageModel, User user,Boolean flag) {
        try {
            if(flag){
                boolean check = true;
                if(!"".equals(user.getProfileId().getPicture())){
                    Integer x = user.getProfileId().getPicture().lastIndexOf("/");
                    String temp = user.getProfileId().getPicture().substring(x+1);
                    x = temp.lastIndexOf(".");
                    check = removeResumeOrPicture(temp.substring(0,x));
                }
                 if(check){
                     Cloudinary cloudinary = new Cloudinary(cloudUrl);
                     Map params1 = ObjectUtils.asMap(
                             "use_filename", false,
                             "unique_filename", true,
                             "overwrite", false
                     );
                     Map response = cloudinary.uploader().upload(imageModel.getBytes(), params1);
                     if(response.get("secure_url") == null || response.get("display_name") == null || String.valueOf(response.get("secure_url")).isBlank() || String.valueOf(response.get("display_name")).isBlank()) {
                         return false;
                     }
                     Profile profile = user.getProfileId();
                     profile.setPicture(String.valueOf(response.get("secure_url")).replace("/upload/","/upload/h_225,w_225/"));
                     profileRepository.save(profile);
                 }
            }
            else {
                boolean check = true;
                if(!"".equals(user.getProfileId().getResume())){
                    Integer x = user.getProfileId().getResume().lastIndexOf("/");
                    String temp = user.getProfileId().getResume().substring(x + 1);
                    x = temp.lastIndexOf(".");
                    check = removeResumeOrPicture(temp.substring(0, x));
                }
                if (check) {
                    Cloudinary cloudinary = new Cloudinary(cloudUrl);
                    Map params1 = ObjectUtils.asMap(
                            "use_filename", false,
                            "unique_filename", true,
                            "overwrite", false
                    );
                    Map response = cloudinary.uploader().upload(imageModel.getBytes(), params1);
                    if (response.get("secure_url") == null || response.get("display_name") == null || String.valueOf(response.get("secure_url")).isBlank() || String.valueOf(response.get("display_name")).isBlank()) {
                        return false;
                    }
                    Profile profile = user.getProfileId();
                    profile.setResume(String.valueOf(response.get("secure_url")).replace("/upload/", "/upload/h_225,w_225/"));
                    profileRepository.save(profile);
                }
            }
            return true;
        } catch (Exception e) {
            log.error("An error occurred while updating image on cloudinary");
            return false;
        }
    }

}