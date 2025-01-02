package com.devjobs.portal.service;

import com.devjobs.portal.entities.Profile;
import com.devjobs.portal.entities.User;
import com.devjobs.portal.model.request.LogInData;
import com.devjobs.portal.model.request.ResetPasswordData;
import com.devjobs.portal.model.request.SignUpData;
import com.devjobs.portal.repositories.ProfileRepository;
import com.devjobs.portal.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public boolean signingUpWithUserData(SignUpData signUpData){
        try{
            User newUser = new User();
            newUser.setEmail(signUpData.getEmail());
            newUser.setRole(signUpData.getRole());
            newUser.setFullName(signUpData.getFullName());
            newUser.setPassword(passwordEncoder.encode(signUpData.getPassword()));

            if(signUpData.getRole().equals("SEEKER") || signUpData.getRole().equals("RECRUITER")){
                Profile newProfile = new Profile();
                if(signUpData.getRole().equals("SEEKER")){
                    newProfile.setCompany("");
                    newProfile.setPicture("");
                    newProfile.setResume("");
                    newProfile.setCity("");
                    newProfile.setCountry("");
                    newProfile.setState("");
                    newProfile.setSkills(new ArrayList<>());
                    newProfile.setAppliedJobs(new ArrayList<>());
                    newProfile.setSavedJobs(new ArrayList<>());
                    newProfile.setTotalExperience("");
                }
                else{
                    newProfile.setCreatedJobs(new ArrayList<>());
                }
                Profile savedProfile = profileRepository.save(newProfile);
                newUser.setProfileId(savedProfile);
            }
            userRepository.save(newUser);
            return true;
        }
        catch(Exception e){
            log.error("An error occurred while signing up a user",e);
            return false;
        }
    }

    public boolean changePassword(ResetPasswordData body, User user){
        try{
            user.setPassword(passwordEncoder.encode(body.getPassword()));
            userRepository.save(user);
            return true;
        }
        catch(Exception e){
            log.error("An error occurred while changing password",e);
            return false;
        }
    }

    public boolean updateProfile(String city, String state, String country, String company, String totalExperience, List<String> skills, Profile profile){
        try{
            if(city!=null) profile.setCity(city);
            if(state!=null) profile.setState(state);
            if(country!=null) profile.setCountry(country);
            if(company!=null) profile.setCompany(company);
            if(totalExperience!=null) profile.setTotalExperience(totalExperience);
            if(skills!=null) profile.setSkills(skills);
            profileRepository.save(profile);
            return true;
        }
        catch(Exception e){
            log.error("An error occurred while updating user profile");
            return false;
        }
    }
}
