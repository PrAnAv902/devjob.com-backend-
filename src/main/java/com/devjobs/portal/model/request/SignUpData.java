package com.devjobs.portal.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpData {
    private String fullName;
    private String email;
    private String password;
    private String role;
    private Integer otp;
}
