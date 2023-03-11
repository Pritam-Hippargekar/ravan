package com.ayush.ravan.elsticsearch.search.security;


import co.arctern.api.emr.search.domain.PatientHistory;
import co.arctern.api.emr.security.authenticationHandler.AuthHandler;
import co.arctern.api.emr.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class ElasticApiAuthentication {

    @Autowired
    private AuthHandler authHandler;

    @SuppressWarnings("Duplicates")
    public Boolean userValidate(List<PatientHistory> patientHistories, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> srr = new ArrayList<>();
        for (GrantedAuthority authority : authorities){
            srr.add(authority.getAuthority());
        }
        /**
         * @Access --- Full all access to back
         * ..........
         */
        if (srr.contains("BACK_OFFICE") || srr.contains("ROLE_ADMIN") || srr.contains("TRANSCRIBER_ADMIN") || srr.contains("TRANSCRIBER_MEDICINE") || srr.contains("TRANSCRIBER_LAB") || srr.contains("TRANSCRIBER_OTHER") || srr.contains("TRANSCRIBER_QA")){
            return true;
        }

        if (srr.contains("DOCTOR") || srr.contains("ASSISTANT") ){
            Boolean flag = false;
            for (PatientHistory patientHistory : patientHistories){
                if (!user.getClinicid().contains(patientHistory.getConsultation().getDoctorInClinic().getId())){
                    flag = false;
                    break;
                }else {
                    flag = true;
                }
            }
            return flag;
        }

        return false;
    }

}
