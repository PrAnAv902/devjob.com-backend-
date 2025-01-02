package com.devjobs.portal.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordData {
    private String email;
    private String password;
    private Integer otp;
}
