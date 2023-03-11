package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.Clinic;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;

@Projection(types = {Clinic.class})
public interface Clinics {
 Long getId();

 String getName();

 Boolean getIsHospital();

 Double getLatitude();

 Double getLongitude();

 Double getDistance();

 String getAddress();

 String getShortAddress();

 String getPhoneNumber();

 String getClinicImage();

 Boolean getIsTest();

 Float getcGST();

 Float getsGST();

 Timestamp getLastModified();

String getCity();

 Long getVersion();


}
