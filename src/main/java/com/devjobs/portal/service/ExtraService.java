package com.devjobs.portal.service;


import org.springframework.stereotype.Service;

@Service
public class ExtraService {

    public Integer randomNumberGeneration(Integer maximum,Integer minimum){
        int max = maximum;
        int min = minimum;
        int range = max - min + 1;
        return (int)(Math.random() * range) + min;
    }
}
