package com.ayush.ravan.elsticsearch;

package co.arctern.api.emr.service.middlelayer;

import co.arctern.api.emr.CustomException.DeclineReasonNotFoundException;
import co.arctern.api.emr.CustomException.DoctorInClinicNotFoundException;
import co.arctern.api.emr.CustomException.DoctorUnAvailabilityBadRequestException;
import co.arctern.api.emr.CustomException.DoctorUnAvailabilityNotFoundException;
import co.arctern.api.emr.SeviceController.ScribblePrescriptionPad;
import co.arctern.api.emr.domain.*;
import co.arctern.api.emr.domain.dto.ClinicTimingDto;
import co.arctern.api.emr.domain.dto.DistanceUtil;
import co.arctern.api.emr.domain.dto.DoctorMarkAvailabilityDto;
import co.arctern.api.emr.domain.dto.PrescriptionPadDto;
import co.arctern.api.emr.domain.projection.*;
import co.arctern.api.emr.options.ConsultationStatus;
import co.arctern.api.emr.options.ConsultationType;
import co.arctern.api.emr.options.DeclineReasonType;
import co.arctern.api.emr.options.DoctorAvailabilityStatus;
import co.arctern.api.emr.search.NextAvailableTimeSlot;
import co.arctern.api.emr.search.TypeCastInToPage;
import co.arctern.api.emr.search.domain.Doctor;
import co.arctern.api.emr.search.domain.projection.Doctors;
import co.arctern.api.emr.search.domain.projection.DoctorsBasedOnPopularity;
import co.arctern.api.emr.search.domain.projection.DoctorsForSearch;
import co.arctern.api.emr.security.TokenDecoder;
import co.arctern.api.emr.service.api.*;
import co.arctern.api.emr.transformer.DoctorTransformer;
import co.arctern.api.emr.utility.DateUtil;
import com.google.common.collect.Lists;
//import jdk.internal.org.jline.utils.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageImpl;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;

@Service
@Transactional
@Slf4j
public class DoctorAndClinicService {

    @Autowired
    ProjectionFactory projectionFactory;

    @Autowired
    TokenDecoder tokenDecoder;

    @Autowired
    ClinicPickupScheduleRepository clinicPickupScheduleRepository;

    @Autowired
    private DoctorTransformer doctorTransformer;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    ClinicRepository clinicRepository;

    @Autowired
    TypeCastInToPage typeCastInToPage;

    @Autowired
    ConsultationRepository consultationRepository;

    @Autowired
    private NextAvailableTimeSlot nextAvailableTimeSlot;

    @Autowired
    PopularityCalculatorService popularityCalculatorService;

    @Autowired
    private PagedResourcesAssembler<Doctors> doctorPagedResourcesAssemblerForDoctors;

    @Autowired
    private PagedResourcesAssembler<DoctorsForSearch> doctorPagedResourcesAssemblerForDoctorsForSearch;

    @Autowired
    private PagedResourcesAssembler<DoctorsBasedOnPopularity> doctorPagedResourcesAssemblerForDoctorBasedOnPopularity;

    @Autowired
    private PagedResourcesAssembler<DoctorsForWeb> doctorPagedResourcesAssemblerForWeb;

    @Autowired
    private PagedResourcesAssembler<ClinicsForElasticSearch> clinicsPagedResourcesAssemblerForDoctors;

    @Autowired
    private PagedResourcesAssembler<DoctorsForPatientApp> doctorPagedResourcesAssembler;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    DoctorInClinicRepository doctorInClinicRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    ScribblePrescriptionPad scribblePrescriptionPad;

    @Autowired
    DoctorUnAvailabilityRepository doctorUnAvailabilityRepository;

    @Autowired
    DeclineReasonRepository declineReasonRepository;

    @Autowired
    DoctorUnAvailabilityFlowRepository doctorUnAvailabilityFlowRepository;

    @Autowired
    ZohoService zohoService;

    @Value("${arctern.imageproxy.url:https://diagnostic-report.staging.meddo.tech/api/v1/diagnosticreportsproxy/file?fileName=}")
    private String proxyUrl;

    @Value("${doctor.search.radius.filter:50L}")
    private Long doctorSearchRadiusFilter;

    @Transactional
    public List<ClinicForAdminDashboard> fetchDoctorsOrClinics(String name) {
        return doctorInClinicRepository.findDistinctByDoctorNameContainingOrClinicNameContaining(name, name)
                .stream()
                .map(doctorInClinic -> {
                    return projectionFactory.createProjection(ClinicForAdminDashboard.class, doctorInClinic.getClinic());
                }).collect(Collectors.toList());

    }

    @Transactional
    public Boolean isClinicSlugExists(String slug) {
        return clinicRepository.existsBySlug(slug);
    }

    @Transactional
    public Boolean isDoctorSlugExists(String slug) {
        return doctorRepository.existsBySlug(slug);
    }


    @Transactional
    public List<ClinicForAdminDashboard> fetchByDicAndName(String name) {
        return doctorInClinicRepository.findDistinctByDoctorNameContainingOrClinicNameContainingAndIdIn(name, name, tokenDecoder.getDoctorInClinic())
                .stream()
                .map(doctorInClinic -> {
                    return projectionFactory.createProjection(ClinicForAdminDashboard.class, doctorInClinic.getClinic());
                }).collect(Collectors.toList());

    }

    @Transactional
    public Resources<?> fetchAllDoctorsByFilterAndSort(Long[] specialities,
                                                       String available,
                                                       LocalDate currentDate,
                                                       String[] cities,
                                                       String[] clusters,
                                                       Double latitude,
                                                       Double longitude,
                                                       String sortFieldInput,
                                                       Boolean asc,
                                                       String search,
                                                       Long clinicId,
                                                       Pageable page,
                                                       Boolean isV2,
                                                       String slug, Boolean isClinic,
                                                       Boolean isCamp,
                                                       Long doctorId,
                                                       Boolean forDoctor,
                                                       Boolean isListed,
                                                       Boolean isAvailableOnline,
                                                       Boolean isAvailableAtClinic,
                                                       String auth) {

        List<Long> recentlyVisitedDoctorIds = getRecentlyVisitedDoctors(auth);
        SearchQuery searchQuery = null;
        AggregatedPage<Doctor> doctors = null;
        log.info(" fetchAllDoctorsByFilterAndSort :: sort by "+ sortFieldInput);
        if (!sortFieldInput.equals("nextAvailabeTimeSlot")) {
            log.info(" fetchAllDoctorsByFilterAndSort :: ! nextAvailabeTimeSlot");
            searchQuery = getNativeQuery(specialities, cities, clusters, latitude, longitude, sortFieldInput, asc, search, clinicId, page, slug, isCamp, doctorId, forDoctor, isListed, isAvailableOnline, isAvailableAtClinic, available, recentlyVisitedDoctorIds);
            doctors = elasticsearchTemplate.queryForPage(searchQuery, co.arctern.api.emr.search.domain.Doctor.class);
        }else{
            log.info(" fetchAllDoctorsByFilterAndSort  :: next availabe time sort :: start");
            searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(applyFilters(clinicId, search, specialities, cities, clusters, slug, isCamp, doctorId, forDoctor, recentlyVisitedDoctorIds, isListed, isAvailableOnline, isAvailableAtClinic, available, longitude, latitude))
                    .build();
            List<Doctor> doctorList = elasticsearchTemplate.queryForList(searchQuery, co.arctern.api.emr.search.domain.Doctor.class);
            log.info(" fetchAllDoctorsByFilterAndSort  :: doctorList size "+ doctorList.size());
            List<DoctorsForSearch> doctorForSearchList = new ArrayList<>();
            doctorList.forEach((Doctor doctor) -> {
                doctor.setDoctorInClinics(doctor.getDoctorInClinics().stream().filter(a -> a.getIsActive() != null && a.getIsActive()).collect(Collectors.toList()));
                DoctorsForSearch doctorsForSearch =  projectionFactory.createProjection(DoctorsForSearch.class, doctor);
                doctorForSearchList.add(doctorsForSearch);
            });
            log.info(" fetchAllDoctorsByFilterAndSort  :: doctorForSearchList size before sort "+ doctorForSearchList.size());

            doctorForSearchList.stream().sorted(Comparator.comparing(slot -> {
                if (slot.getNextAvailabeTimeSlot().getNextAvailableNormalSlot() == null
                        && slot.getNextAvailabeTimeSlot().getNextAvailableTeleSlot() == null)
                    return new DateTime().plusDays(30).toDate().getTime();

                if (slot.getNextAvailabeTimeSlot().getNextAvailableNormalSlot() != null) {
                    return new DateTime(slot.getNextAvailabeTimeSlot()
                            .getNextAvailableNormalSlot()
                            .substring(0, 16).replace(' ', 'T'))
                            .toDate().getTime();
                }

                return new DateTime(slot.getNextAvailabeTimeSlot()
                        .getNextAvailableTeleSlot().substring(0, 16).replace(' ', 'T'))
                        .toDate().getTime();
            })).collect(Collectors.toList());

            log.info(" fetchAllDoctorsByFilterAndSort  :: doctorForSearchList size after sort "+ doctorForSearchList.size());

            //  doctorForSearchListResult = new PageImpl<DoctorsForSearch>(doctorForSearchList,page, page.getPageSize());
           /* PageImpl<DoctorsForSearch> doctorsForSearches = new PageImpl<>(doctorForSearchList, page, page.getPageSize());
            List<DoctorsForSearch> doctorsForSearchTempList =  doctorsForSearches.getContent();*/

            return doctorPagedResourcesAssemblerForDoctorsForSearch.toResource(new PageImpl<DoctorsForSearch>(doctorForSearchList, page, doctorList.size()));
        }
//        ArrayList<Doctor> doctorsList = Lists.newArrayList(elasticsearchTemplate.stream(searchQuery, Doctor.class));
        log.info("  isClinic "+ isClinic);
        if (isClinic) {
            List<co.arctern.api.emr.search.domain.Clinic> clinics = new ArrayList<co.arctern.api.emr.search.domain.Clinic>();
            doctors.stream().map(a -> a.getDoctorInClinics().stream().map(b -> b.getClinic()).collect(Collectors.toList())).collect(Collectors.toList()).stream()
                    .forEach(a -> clinics.addAll(a));
            return clinicsPagedResourcesAssemblerForDoctors.toResource(typeCastInToPage.convertToPageForClinic(clinics.stream().distinct().collect(Collectors.toList()), page));
        }
//        nextAvailableTimeSlot.getNextAvailable(doctors.getContent());
        log.info(" popularityCalculatorService.calculatePopularityForDoctors available "+ available +"  isV2 "+isV2) ;
        popularityCalculatorService.calculatePopularityForDoctors(doctors.getContent());

        return (!isV2) ? getResourcesForV1(latitude, longitude, sortFieldInput, page, doctors)
                : doctorPagedResourcesAssemblerForWeb.toResource(typeCastInToPage.mapToPageForWeb(doctors, latitude, longitude));

    }

    @Transactional
    public Resources<?> fetchAllDoctorsByPopularity(Long[] specialities,
                                                    String available,
                                                    LocalDate currentDate,
                                                    String[] cities,
                                                    String[] clusters,
                                                    Double latitude,
                                                    Double longitude,
                                                    String sortFieldInput,
                                                    Boolean asc,
                                                    String search,
                                                    Long clinicId,
                                                    Pageable page,
                                                    Boolean isV2,
                                                    String slug, Boolean isClinic,
                                                    Boolean isCamp,
                                                    Long doctorId,
                                                    Boolean forDoctor,
                                                    Boolean isListed,
                                                    Boolean isAvailableOnline,
                                                    Boolean isAvailableAtClinic,
                                                    String auth) {
        List<Long> recentlyVisitedDoctorIds = getRecentlyVisitedDoctors(auth);

        log.info(" fetchAllDoctorsByFilterAndSort :: ! nextAvailabeTimeSlot");
        SearchQuery searchQuery = getNativeQuery(specialities, cities, clusters, latitude, longitude, sortFieldInput, asc, search, clinicId, page, slug, isCamp, doctorId, forDoctor, isListed, isAvailableOnline, isAvailableAtClinic, available, recentlyVisitedDoctorIds);
        AggregatedPage<Doctor> doctors = elasticsearchTemplate.queryForPage(searchQuery, co.arctern.api.emr.search.domain.Doctor.class);
        log.info("Elastic Search Query executed successfully.");
        popularityCalculatorService.calculatePopularityForDoctors(doctors.getContent());
        log.info("Dynamically Calculated Popularity For Doctors.");
        return getResourcesForDoctorBasedOnPopularity(latitude, longitude, sortFieldInput, page, doctors);
    }

    private NativeSearchQuery getNativeQuery(Long[] specialities, String[] cities, String[] clusters, Double latitude,
                                             Double longitude, String sortFieldInput, Boolean asc, String search, Long clinicId, Pageable page,
                                             String slug, Boolean isCamp, Long doctorId, Boolean forDoctor, Boolean isListed, Boolean isAvailableOnline,
                                             Boolean isAvailableAtClinic, String doctorAvailabilityFilter, List<Long> recentlyVisitedDoctorIds) {
        return new NativeSearchQueryBuilder()
                .withQuery(applyFilters(clinicId, search, specialities, cities, clusters, slug, isCamp, doctorId, forDoctor, recentlyVisitedDoctorIds,
                        isListed, isAvailableOnline, isAvailableAtClinic, doctorAvailabilityFilter, longitude, latitude))
                .withSort(applySortParameters(isAvailableOnline, asc, sortFieldInput, latitude, longitude))
                .withPageable(page)
                .build();
    }

    private List<Long> getRecentlyVisitedDoctors(String auth) {
        List<Long> recentlyVisitedDoctorIds = new ArrayList<>();
        if (auth != null && tokenDecoder.isUserPatient()) {
            recentlyVisitedDoctorIds = consultationRepository.fetchByPatientIdsAndAppointmentDateBetween(tokenDecoder.getListOfPatient(), new Date(Instant.now().minus(30, ChronoUnit.DAYS).toEpochMilli()), new Date(Instant.now().toEpochMilli()))
                    .stream()
                    .map(consultation -> consultation.getDoctorInClinic().getDoctor().getId())
                    .collect(Collectors.toList());
        }
        return recentlyVisitedDoctorIds;
    }

    /*
        This method sorts and returns the all doctors data on selected sorting
     */
    private PagedResources<Resource<DoctorsForSearch>> getResourcesForV1(Double latitude, Double longitude, String sortFieldInput, Pageable page, AggregatedPage<Doctor> doctors) {
        if (!sortFieldInput.equals("nextAvailabeTimeSlot"))
            return doctorPagedResourcesAssemblerForDoctorsForSearch.toResource(typeCastInToPage.mapToPagesWithDoctorsForSearchProjection(doctors, latitude, longitude));

        log.info(" getResourcesForV1 :: getInvoke");
        /* Actual list */
        Page<DoctorsForSearch> pagedData = typeCastInToPage.mapToPagesWithDoctorsForSearchProjection(doctors, latitude, longitude);
        List<DoctorsForSearch> doctorForSearchList = pagedData.getContent();
        /*        Actual list befor sorting         */
        doctorForSearchList = doctorForSearchList.stream().sorted(Comparator.comparing(slot -> {
            if (slot.getNextAvailabeTimeSlot().getNextAvailableNormalSlot() == null
                    && slot.getNextAvailabeTimeSlot().getNextAvailableTeleSlot() == null)
                return new DateTime().plusDays(30).toDate().getTime();

            if (slot.getNextAvailabeTimeSlot().getNextAvailableNormalSlot() != null) {
                return new DateTime(slot.getNextAvailabeTimeSlot()
                        .getNextAvailableNormalSlot()
                        .substring(0, 16).replace(' ', 'T'))
                        .toDate().getTime();
            }

            return new DateTime(slot.getNextAvailabeTimeSlot()
                    .getNextAvailableTeleSlot().substring(0, 16).replace(' ', 'T'))
                    .toDate().getTime();
        })).collect(Collectors.toList());
        log.info("nextAvailabeTimeSlot page size "+page.getPageSize() +"totalElements "+pagedData.getNumberOfElements()+" "+pagedData.getTotalPages());
        return doctorPagedResourcesAssemblerForDoctorsForSearch.toResource(new PageImpl<DoctorsForSearch>(doctorForSearchList,page,pagedData.getNumberOfElements() ));
    }

    private PagedResources<Resource<DoctorsBasedOnPopularity>> getResourcesForDoctorBasedOnPopularity(Double latitude, Double longitude, String sortFieldInput, Pageable page, AggregatedPage<Doctor> doctors) {
        return doctorPagedResourcesAssemblerForDoctorBasedOnPopularity.toResource(typeCastInToPage.mapToPagesWithDoctorsBasedOnPopularityForSearchProjection(doctors, latitude, longitude));
    }

    @Transactional
    public SortBuilder applySortParameters(Boolean isAvailableOnline, Boolean asc, String sortFieldInput, Double latitude, Double longitude) {
        SortOrder order = (asc) ? SortOrder.ASC : SortOrder.DESC;
        return extractSortField(sortFieldInput, isAvailableOnline, order, latitude, longitude);
    }

    @Transactional
    public SortBuilder applySortParameters(Boolean isAvailableOnline, Boolean isAvailableAtClinic, Boolean asc, String sortFieldInput, Double latitude, Double longitude) {
        SortOrder order = (asc) ? SortOrder.ASC : SortOrder.DESC;
        // if user is not looking for clinic/online doctor defalut will be clinic doctor.
        isAvailableAtClinic  = isAvailableAtClinic(isAvailableAtClinic,isAvailableOnline);
        return extractSortField(sortFieldInput, isAvailableOnline, isAvailableAtClinic, order, latitude, longitude);
    }

    protected SortBuilder extractSortField(String sortFieldInput, Boolean isAvailableOnline, SortOrder order, Double latitude, Double longitude) {
        switch (sortFieldInput) {
            case "fee":
                return SortBuilders.fieldSort((isAvailableOnline != null && isAvailableOnline) ? "doctorInClinics.econsultationFee" : "doctorInClinics.consultationFee")
                        .order(order)
                        .setNestedPath("doctorInClinics");
            case "location":
                return SortBuilders.geoDistanceSort("doctorInClinics.clinic.location", latitude, longitude)
                        .unit(DistanceUnit.KILOMETERS)
                        .order(order);
           /* case "popularity":
                return SortBuilders.fieldSort("popularity").order(order);*/

            case "name":
                return SortBuilders.fieldSort("name").order(order);
            case "rating":
                return SortBuilders.fieldSort("rating").order(order);
            case "yearsOfExperience":
                return SortBuilders.fieldSort("doctorInClinics.year_of_exp").order(order);
            default:
                if (latitude == null && longitude == null) {
                    if (isAvailableOnline != null && isAvailableOnline) {
                        return getSortFieldForDefaultNoLatLong("doc['doctorInClinics.tele_consult_slot_available_today_score'].value", order);
                    }

                    return getSortFieldForDefaultNoLatLong("doc['doctorInClinics.slot_available_today_score'].value", order);

                } else {
                    if (isAvailableOnline != null && isAvailableOnline) {
                        return getSortFieldForDefaultLatLong("doc['doctorInClinics.tele_consult_slot_available_today_score'].value", latitude, longitude, order);
                    }

                    return getSortFieldForDefaultLatLong("doc['doctorInClinics.slot_available_today_score'].value", latitude, longitude, order);

                }
        }
    }

    /**
     * Overloaded method extractSortField with additional parameter isAvailableAtClinic
     * @param sortFieldInput
     * @param isAvailableOnline
     * @param order
     * @param latitude
     * @param longitude
     * @return
     */
    protected SortBuilder extractSortField(String sortFieldInput, Boolean isAvailableOnline,  Boolean isAvailableAtClinic, SortOrder order, Double latitude, Double longitude) {
        switch (sortFieldInput) {
            case "fee":
                return SortBuilders.fieldSort((isAvailableOnline != null && isAvailableOnline) ? "doctorInClinics.econsultationFee" : "doctorInClinics.consultationFee")
                        .order(order)
                        .setNestedPath("doctorInClinics");
            case "location":
                return SortBuilders.geoDistanceSort("doctorInClinics.clinic.location", latitude, longitude)
                        .unit(DistanceUnit.KILOMETERS)
                        .order(order);
           /* case "popularity":
                return SortBuilders.fieldSort("popularity").order(order);*/

            case "name":
                return SortBuilders.fieldSort("name").order(order);
            case "rating":
                return SortBuilders.fieldSort("rating").order(order);
            case "yearsOfExperience":
                return SortBuilders.fieldSort("doctorInClinics.year_of_exp").order(order);
            default:
                if (latitude == null && longitude == null) {
                    if (isAvailableOnline != null && isAvailableOnline) {
                        return getSortFieldForDefaultNoLatLong("doc['doctorInClinics.tele_consult_slot_available_today_score'].value", order);
                    }

                    return getSortFieldForDefaultNoLatLong("doc['doctorInClinics.slot_available_today_score'].value", order);

                } else {
                    if(isAvailableAtClinic != null && isAvailableAtClinic){
                        return getSortFieldForDefaultLatLong("doc['doctorInClinics.slot_available_today_score'].value", latitude, longitude, order, true);
                    }else {
                        return getSortFieldForDefaultLatLong("doc['doctorInClinics.tele_consult_slot_available_today_score'].value", latitude, longitude, order, false);
                    }
                }
        }
    }


    protected ScriptSortBuilder getSortFieldForDefaultLatLong(String key, Double latitude, Double longitude, SortOrder order) {
        return SortBuilders.scriptSort(new Script(Script.DEFAULT_SCRIPT_TYPE, Script.DEFAULT_SCRIPT_LANG, "def score = 0;"
                + "def distance = (doc['doctorInClinics.clinic.location'].arcDistance(params.latitude,params.longitude))/1000;"
                + "if (distance <= 3 ) score = 5;"
                + "else if (distance > 3 && distance <=6) score =  4.6;"
                + "else if (distance > 6 && distance <=10 ) score = 4;"
                + "else if (distance > 10 && distance <=15) score =  3;"
                + "else if (distance > 15 && distance <=25 ) score = 2;"
                + "else if (distance > 25 && distance <=50 ) score = 1;"
                + "else score = 0;"
                + "return (score * 0.3) + " + key + " + doc['doctorInClinics.total_score'].value * 1.6 ;",
                new HashMap<String, Object>() {
                    {
                        put("latitude", latitude);
                        put("longitude", longitude);
                    }
                }), ScriptSortBuilder.ScriptSortType.NUMBER).order(order);
    }

    /**
     * Overloaded method with additional paramter availableAtClinic
     * @param key
     * @param latitude
     * @param longitude
     * @param order
     * @param isAvailableAtClinic
     * @return
     */
    protected ScriptSortBuilder getSortFieldForDefaultLatLong(String key, Double latitude, Double longitude, SortOrder order, Boolean isAvailableAtClinic) {
        float distanceMultiplier = getDistanceMultiplier(isAvailableAtClinic);
        return SortBuilders.scriptSort(new Script(Script.DEFAULT_SCRIPT_TYPE, Script.DEFAULT_SCRIPT_LANG, "def score = 0;"
                + "def distance = (doc['doctorInClinics.clinic.location'].arcDistance(params.latitude,params.longitude))/1000;"
                + "if (distance <= 3 ) score = 5;"
                + "else if (distance > 3 && distance <=6) score =  4.6;"
                + "else if (distance > 6 && distance <=10 ) score = 4;"
                + "else if (distance > 10 && distance <=15) score =  3;"
                + "else if (distance > 15 && distance <=25 ) score = 2;"
                + "else if (distance > 25 && distance <=50 ) score = 1;"
                + "else score = 0;"
                + "return (score * "+ distanceMultiplier + ") + " + key + " + doc['doctorInClinics.total_score'].value * 1.6 ;",
                new HashMap<String, Object>() {
                    {
                        put("latitude", latitude);
                        put("longitude", longitude);
                    }
                }), ScriptSortBuilder.ScriptSortType.NUMBER).order(order);
    }


    /**
     * Return score multipler for distance score, it will be higher for inclinic doctor i.e 0.5 now and for online 0.3
     * @param isAvailableAtClinic
     * @return float multiplier
     */
    protected float getDistanceMultiplier(Boolean isAvailableAtClinic){
        if(isAvailableAtClinic !=null && isAvailableAtClinic){
            return  0.5f;
        }else{
            return 0.3f;
        }
    }

    /**
     * return true if both flag false or isAvailableAtClinic true otherwise false
     * @param isAvailableAtClinic
     * @param isAvailableOnline
     * @return true/false
     */
    protected boolean isAvailableAtClinic(Boolean isAvailableAtClinic, Boolean isAvailableOnline){

        if((isAvailableAtClinic == null || BooleanUtils.isFalse(isAvailableAtClinic)) && (isAvailableOnline == null || BooleanUtils.isFalse(isAvailableOnline))){
            return true;
        }else if(BooleanUtils.isTrue(isAvailableAtClinic)){
            return true;
        }else{
            return false;
        }
    }

    protected SortBuilder getSortFieldForDefaultNoLatLong(String key, SortOrder order) {
        return SortBuilders.scriptSort(
                new Script(
                        Script.DEFAULT_SCRIPT_TYPE,
                        Script.DEFAULT_SCRIPT_LANG,
                        "return " + key + "+ doc['doctorInClinics.total_score'].value * 2 ",
                        new HashMap<String, Object>()
                ), ScriptSortBuilder.ScriptSortType.NUMBER).order(order);
    }



    @Transactional
    public BoolQueryBuilder applyFilters(Long clinicId, String search, Long[] specialities,
                                         String[] cities, String[] clusters, String slug, Boolean isCamp,
                                         Long doctorId, Boolean forDoctor, List<Long> recentlyVisitedDoctorIds,
                                         Boolean isListed, Boolean isAvailableOnline, Boolean isAvailableAtClinic,
                                         String doctorAvailabilityFilter, Double longitude, Double latitude) {
        if (search != null) {
            search = search.toLowerCase();
            if (search.startsWith("dr")) {
                search = search.replaceFirst(".", "");
                search = search.replaceFirst("^dr", "");
            }
        }
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(QueryBuilders.matchQuery("isCamp", isCamp));
        qb.must(QueryBuilders.matchQuery("isActive", true));
        qb.mustNot(QueryBuilders.termQuery("doctorInClinics.usingMeddoLite", true));
        if (forDoctor) {
            if (BooleanUtils.isTrue(isListed)) {
                BoolQueryBuilder qbForIsListed = QueryBuilders.boolQuery();
                qb.must(qbForIsListed.should(QueryBuilders.matchQuery("doctorInClinics.isListed", true)));
            }
            if (isAvailableOnline != null) {
                qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("doctorInClinics.isAvailableOnline", isAvailableOnline)));
            }
            if (isAvailableAtClinic != null) {
                qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("doctorInClinics.isAvailableAtClinic", isAvailableAtClinic)));
            }
            if (clinicId != null) {
                BoolQueryBuilder qbForClinicId = QueryBuilders.boolQuery();
                qb.must(qbForClinicId.should(QueryBuilders.matchQuery("doctorInClinics.clinic.id", clinicId)));
            }

            if (doctorAvailabilityFilter!=null && !doctorAvailabilityFilter.isEmpty()) {
                if (isAvailableAtClinic!=null && isAvailableAtClinic) {
                    switch (doctorAvailabilityFilter.toLowerCase().trim()) {
                        case "today":
                            qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("availableTodayInClinic", true)));
                        case "tomorrow":
                            qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("availableTomorrowInClinic", true)));
                        case "next7days":
                            qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("availableNext7DayInClinic", true)));
                    }
                }
                if (isAvailableOnline!=null && isAvailableOnline) {
                    switch (doctorAvailabilityFilter.toLowerCase().trim()) {
                        case "today":
                            qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("availableTodayOnline", true)));
                        case "tomorrow":
                            qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("availableTomorrowOnline", true)));
                        case "next7days":
                            qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("availableNext7DayOnline", true)));
                    }
                }
                if (isAvailableAtClinic == null && isAvailableOnline == null) {
                    switch (doctorAvailabilityFilter.toLowerCase().trim()) {
                        case "today":
                            qb.should(QueryBuilders.termQuery("availableTodayOnline", true)).should(QueryBuilders.termQuery("availableTodayOnline", true));
                        case "tomorrow":
                            qb.should(QueryBuilders.termQuery("availableTomorrowOnline", true)).should(QueryBuilders.termQuery("availableTomorrowInClinic", true));
                        case "next7days":
                            qb.should(QueryBuilders.termQuery("availableNext7DayOnline", true)).should(QueryBuilders.termQuery("availableNext7DayInClinic", true));
                    }
                }
            }
        }
        if (doctorId != null) {
            qb.must(QueryBuilders.matchQuery("id", doctorId));
        }

        if (doctorSearchRadiusFilter != null && longitude != null && latitude != null) {
            if (isAvailableAtClinic !=null && isAvailableAtClinic) {
                qb.must(QueryBuilders
                        .geoDistanceQuery("doctorInClinics.clinic.location")
                        .point(latitude, longitude)
                        .distance(doctorSearchRadiusFilter, DistanceUnit.KILOMETERS));
            }
        }

        if (search != null && !search.isEmpty()) {

            BoolQueryBuilder searchQb = QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("name", search).operator(Operator.OR).boost(10f))
                    .should(QueryBuilders.matchPhrasePrefixQuery("name", search).boost(5f))
                    .should(QueryBuilders.matchQuery("doctorInClinics.clinic.name", search).operator(Operator.AND).boost(3f))
                    .should(QueryBuilders.matchPhrasePrefixQuery("doctorInClinics.clinic.name", search).boost(1f));

            Map<String, Object> map = new HashMap<>();
            String drPrefix = "Dr " + search.trim();
            String drDotPrefix = "Dr. " + search.trim();
            map.put("searchQueryDrPrefix", drPrefix);
            map.put("searchQueryDrPrefixLength", drPrefix.length());
            map.put("searchQueryDrDotPrefix", drDotPrefix);
            map.put("searchQueryDrDotPrefixLength", drDotPrefix.length());

            Script nameScript = new Script(
                    ScriptType.INLINE,
                    "painless",
                    "def str = doc['name.keyword'].value.trim(); "
                            + "str.length() >= params.searchQueryDrDotPrefixLength ? (str.substring(0, params.searchQueryDrPrefixLength).equalsIgnoreCase(params.searchQueryDrPrefix) || str.substring(0, params.searchQueryDrDotPrefixLength).equalsIgnoreCase(params.searchQueryDrDotPrefix) ? "
                            + "str.substring(0, str.length()).equalsIgnoreCase(params.searchQueryDrPrefix) || str.substring(0, str.length()).equalsIgnoreCase(params.searchQueryDrDotPrefix) ? 5 : 2 "
                            + ": 1) : 1",
                    map
            );

            Script recentlyVisitedScript = new Script(
                    ScriptType.INLINE,
                    "painless",
                    "for(entry in params.visitCount.entrySet()){ if(doc['id'].value.toString().equals(entry.getKey())) { return entry.getValue()+1; } } return 1;",
                    Collections.singletonMap(
                            "visitCount",
                            recentlyVisitedDoctorIds.stream()
                                    .collect(Collectors.groupingBy(e -> e.toString(), Collectors.counting()))
                    )
            );

            List<FunctionScoreQueryBuilder.FilterFunctionBuilder> functionBuilderList = new ArrayList<>();
            functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(nameScript)));
            if (!CollectionUtils.isEmpty(recentlyVisitedDoctorIds)) {
                functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(recentlyVisitedScript)));
            }

            qb.must(QueryBuilders.functionScoreQuery(searchQb, functionBuilderList.toArray(new FunctionScoreQueryBuilder.FilterFunctionBuilder[0])));
        }

        if (specialities != null) {

            BoolQueryBuilder specialityQb = QueryBuilders.boolQuery();
            for (Long speciality : specialities) {
                specialityQb.should(QueryBuilders.matchQuery("speciality.id", speciality));
            }
            qb.must(specialityQb);
        }

        if (cities != null) {
            BoolQueryBuilder cityQb = QueryBuilders.boolQuery();
            for (String city : cities) {
                cityQb.should(QueryBuilders.matchQuery("doctorInClinics.clinic.city", city));
            }
            qb.must(cityQb);
        }

        if (slug != null) {
            BoolQueryBuilder slugQb = QueryBuilders.boolQuery();
            slugQb.should(QueryBuilders.multiMatchQuery(slug, "slug", "doctorInClinics.clinic.slug"));
            qb.must(slugQb);
        }

        if (clusters != null) {
            BoolQueryBuilder clusterQb = QueryBuilders.boolQuery();
            for (String cluster : clusters) {
                clusterQb.should(QueryBuilders.matchQuery("doctorInClinics.clinic.cluster", cluster));
            }
            qb.must(clusterQb);
        }
        return qb;
    }

    @Transactional
    public List<Doctor> applyAvailabilityFilter(String available, List<Doctor> doctorsList) {

        nextAvailableTimeSlot.getNextAvailable(doctorsList);
        List<co.arctern.api.emr.search.domain.Doctor> doctorList = null;
        switch (available) {
            case "today": {
                doctorList = nextAvailableTimeSlot.TodayAvailableDoctor(doctorsList);
            }

            case "tomorrow": {
                doctorList = nextAvailableTimeSlot.tomorrowAvailableDoctor(doctorsList);
            }

            case "now": {
                doctorList = nextAvailableTimeSlot.nowAvailableDoctor(doctorsList);
            }

            case "weekend": {
                doctorList = nextAvailableTimeSlot.weekEndDoctorAvailable(doctorsList);
            }

            case "weekdays": {
                doctorList = nextAvailableTimeSlot.weekDaysDoctorAvailable(doctorsList);
            }

            case "next7days": {
                doctorList = nextAvailableTimeSlot.next7DaysDoctorAvailable(doctorsList);
            }

            default: {
                doctorList = nextAvailableTimeSlot.TodayAvailableDoctor(doctorsList);
            }
            doctorsList.retainAll(doctorList);
            return doctorsList;
        }
    }

    @Transactional
    public List<ClinicsForAssistantDashboard> getDoctorInClinics(Long[] dicIds) {
        List<ClinicsForAssistantDashboard> list = new ArrayList<>();
        List<Long> dicIdsList = Arrays.asList(dicIds);
        List<Clinic> clinics = doctorInClinicRepository.findAllByIdIn(dicIds).stream().map(a -> a.getClinic()).collect(Collectors.toList());
        LocalDate startDate = DateUtil.localDateTodayInUtc;
        LocalDate endDate = startDate.plusDays(1);
        for (Clinic clinic : clinics) {
            clinic.setDoctorInClinics(clinic
                    .getDoctorInClinics()
                    .stream()
                    .filter(doctorInClinic -> dicIdsList.contains(doctorInClinic.getId()))
                    .map(doctorInClinic -> {
                        doctorInClinic.setConsultations(new HashSet<>(consultationRepository.findByStatusAndDoctorInClinicIdAndAppointmentTimeGreaterThanEqualAndAppointmentTimeLessThan(ConsultationStatus.CHECKED_IN, doctorInClinic.getId(), Date.valueOf(startDate), Date.valueOf(endDate))));
                        return doctorInClinic;
                    }).collect(Collectors.toSet()));
            list.add(projectionFactory.createProjection(ClinicsForAssistantDashboard.class, clinic));
        }
        return list;
    }

    @Transactional
    public Doctor fetchDoctorBySlug(String slug) throws Exception {
        co.arctern.api.emr.domain.Doctor doctor = doctorRepository.findBySlug(slug);
        if (doctor == null) {
            throw new Exception("No Doctor found");
        }
        doctor.setDoctorInClinics(doctor.getDoctorInClinics().stream().filter(a -> a.getIsActive() != null && a.getIsActive()).collect(Collectors.toSet()));
        Doctor doctorES = doctorTransformer.transformToESDoctor(projectionFactory.createProjection(DoctorForElasticSearch.class, doctor), false);
        doctorES.setDoctorInClinics(doctorES.getDoctorInClinics().stream().filter(a -> a.getIsListed()).collect(Collectors.toList()));
        return doctorES;
    }

    @Transactional
    public List<ClinicsForElasticSearch> fetchClinics(Long labId) {
        return clinicPickupScheduleRepository.findByDiagnosticLabId(labId).stream()
                .map(a -> projectionFactory.createProjection(ClinicsForElasticSearch.class, a.getClinic()))
                .filter(distinctByKey(ClinicsForElasticSearch::getId))
                .collect(Collectors.toList());
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public DoctorInClinicForConsumer getDoctorInClinicById(Long dicId) {
        DoctorInClinic doctorInClinic = doctorInClinicRepository.findById(dicId).get();
        return projectionFactory.createProjection(DoctorInClinicForConsumer.class, doctorInClinic);
    }

    public DoctorInClinicWoConsultationAndClinic getDoctorInClinicByDoxperUserName(String doxperUserName) {
        DoctorInClinic doctorInClinic = doctorInClinicRepository.findByDoxperUserName(doxperUserName).get();
        return projectionFactory.createProjection(DoctorInClinicWoConsultationAndClinic.class, doctorInClinic);

    }

    @Transactional
    public List<Long> findActiveDics() {
        return doctorInClinicRepository.findActiveDics();
    }

    public List<DoctorsForWeb> fetchDoctorsForWeb(List<Long> Ids) {
        return doctorRepository.findAllByIdInAndIsActiveTrue(Ids).stream().map(doctorsForWeb -> projectionFactory.createProjection(DoctorsForWeb.class, doctorsForWeb)).collect(Collectors.toList());
    }

    public DoctorFromDic getDoctorIdFromDicId(Long dicId) throws DoctorInClinicNotFoundException {
        Optional<DoctorInClinic> doctorInClinicOptional = doctorInClinicRepository.findById(dicId);
        if (!doctorInClinicOptional.isPresent()) {
            throw new DoctorInClinicNotFoundException();
        }
        return projectionFactory.createProjection(DoctorFromDic.class, doctorInClinicOptional.get().getDoctor());
    }

    public List<DoctorInClinics> fetchDoctorInClinicByIds(Long[] ids) {
        return doctorInClinicRepository.findAllByIdIn(ids).stream().
                map(clinic -> projectionFactory.createProjection(DoctorInClinics.class, clinic)).collect(Collectors.toList());

    }

    public DoctorInClinics updatePrescriptionForDic(PrescriptionPadDto prescriptionPadDto) throws DoctorInClinicNotFoundException, MalformedURLException {
        if (prescriptionPadDto.getUrl() == null || prescriptionPadDto.getUrl().trim().isEmpty()) {
            throw new RuntimeException("No prescription pad url found");
        }
        DoctorInClinic doctorInClinic = doctorInClinicRepository.findById(prescriptionPadDto.getDicId()).orElseThrow(DoctorInClinicNotFoundException::new);
        String url = scribblePrescriptionPad.generateScribbleUrlFromPresignedRequest(new URL(prescriptionPadDto.getUrl()));
        String[] splitted = url.split("/");
        if (splitted.length >= 1) {
            String filename = splitted[splitted.length - 1];
            doctorInClinic.setPrescriptionPadUrl(proxyUrl + filename);
        }
        return projectionFactory.createProjection(DoctorInClinics.class, doctorInClinicRepository.save(doctorInClinic));
    }

    @Transactional
    public DoctorInClinics doctorMarkUnAvailability(DoctorMarkAvailabilityDto doctorMarkAvailabilityDto) throws Exception {
        DoctorInClinic doctorInClinic = doctorInClinicRepository.findById(doctorMarkAvailabilityDto.getDicId()).orElseThrow(DoctorInClinicNotFoundException::new);
        Instant instant = Instant.now().minusSeconds(60L);
        if(doctorMarkAvailabilityDto.getStartDate() != null && doctorMarkAvailabilityDto.getEndDate() != null && doctorMarkAvailabilityDto.getStartDate().toInstant().getEpochSecond() > doctorMarkAvailabilityDto.getEndDate().toInstant().getEpochSecond()) throw new DoctorUnAvailabilityBadRequestException();
        if(doctorMarkAvailabilityDto.getStartDate() != null && instant.getEpochSecond() > doctorMarkAvailabilityDto.getStartDate().toInstant().getEpochSecond()) throw new DoctorUnAvailabilityBadRequestException();
        doctorInClinic.setStartDate(doctorMarkAvailabilityDto.getStartDate());
        doctorInClinic.setEndDate(doctorMarkAvailabilityDto.getEndDate());
        if (!doctorMarkAvailabilityDto.getDoctorUnAvailability()) {
            doctorInClinic.setStartDate(null);
            doctorInClinic.setEndDate(null);
        }
        doctorInClinic.setDoctorUnAvailability(doctorMarkAvailabilityDto.getDoctorUnAvailability());
        DoctorInClinic save = doctorInClinicRepository.save(doctorInClinic);
        DoctorUnAvailability doctorUnAvailability = setDoctorUnAvailability(doctorMarkAvailabilityDto, save);
        setDoctorUnAvailabilityFlows(doctorUnAvailability);
        if (doctorMarkAvailabilityDto.getDoctorUnAvailability()) {
            ConsultationStatus[] status = {ConsultationStatus.OPEN, ConsultationStatus.CHECKED_IN, ConsultationStatus.PENDING};
            consultationRepository.findByDoctorInClinicIdAndStatusInAndAppointmentTimeGreaterThanEqualAndAppointmentTimeLessThan(doctorMarkAvailabilityDto.getDicId(), status, doctorMarkAvailabilityDto.getStartDate(), doctorMarkAvailabilityDto.getEndDate());            zohoService.createTaskForDoctorUnAvailability(consultationRepository.findByDoctorInClinicIdAndStatusInAndAppointmentTimeGreaterThanEqualAndAppointmentTimeLessThan(doctorMarkAvailabilityDto.getDicId(), status, doctorMarkAvailabilityDto.getStartDate(), doctorMarkAvailabilityDto.getEndDate()), doctorInClinic, doctorMarkAvailabilityDto, doctorUnAvailability);
        }
        return projectionFactory.createNullableProjection(DoctorInClinics.class, doctorInClinic);
    }

    private DoctorUnAvailabilityFlow setDoctorUnAvailabilityFlows(DoctorUnAvailability doctorUnAvailability) {
        DoctorUnAvailabilityFlow doctorUnAvailabilityFlow = new DoctorUnAvailabilityFlow();
        doctorUnAvailabilityFlow.setStatus(doctorUnAvailability.getStatus());
        doctorUnAvailabilityFlow.setDoctorUnAvailability(doctorUnAvailability);
        return doctorUnAvailabilityFlowRepository.save(doctorUnAvailabilityFlow);

    }

    private DoctorUnAvailability setDoctorUnAvailability(DoctorMarkAvailabilityDto doctorMarkAvailabilityDto, DoctorInClinic doctorInClinic) throws DeclineReasonNotFoundException, DoctorUnAvailabilityNotFoundException {
        DoctorUnAvailability doctorUnAvailability = getDoctorUnAvailability(doctorMarkAvailabilityDto, doctorInClinic);
        if (doctorMarkAvailabilityDto.getStartDate() != null) {
            doctorUnAvailability.setStartDate(doctorMarkAvailabilityDto.getStartDate());
        }
        if (doctorMarkAvailabilityDto.getEndDate() != null) {
            doctorUnAvailability.setEndDate(doctorMarkAvailabilityDto.getEndDate());
        }
        doctorUnAvailability.setType((doctorMarkAvailabilityDto.getType() == null) ? ConsultationType.BOTH : doctorMarkAvailabilityDto.getType());
        doctorUnAvailability.setStatus((doctorMarkAvailabilityDto.getDoctorUnAvailability() != null && doctorMarkAvailabilityDto.getDoctorUnAvailability()) ? DoctorAvailabilityStatus.UNAVAILABLE : DoctorAvailabilityStatus.AVAILABLE);
        doctorUnAvailability.setDoctorInClinic(doctorInClinic);
        if (doctorMarkAvailabilityDto.getDeclineReasonId() != null) {
            DeclineReason declineReason = declineReasonRepository.findById(doctorMarkAvailabilityDto.getDeclineReasonId()).orElseThrow(DeclineReasonNotFoundException::new);
            doctorUnAvailability.setDeclineReason(declineReason);
        } else if(doctorMarkAvailabilityDto.getDeclineReason() != null && !doctorMarkAvailabilityDto.getDeclineReason().trim().isEmpty()){
            List<DeclineReason> declineReasonList = declineReasonRepository.findByDeclineReasonTypeAndReasonAndIsActiveTrue(DeclineReasonType.DOCTOR_UNAVAILABILITY, doctorMarkAvailabilityDto.getDeclineReason());
            DeclineReason declineReason = (CollectionUtils.isEmpty(declineReasonList)) ? new DeclineReason():declineReasonList.stream().findFirst().get();
            declineReason.setDeclineReasonType(DeclineReasonType.DOCTOR_UNAVAILABILITY);
            declineReason.setReason(doctorMarkAvailabilityDto.getDeclineReason());
            declineReason.setIsActive(true);
            doctorUnAvailability.setDeclineReason(declineReasonRepository.save(declineReason));
        }
        if (doctorMarkAvailabilityDto.getAppointmentStatus() != null) {
            doctorUnAvailability.setAppointmentStatus(doctorMarkAvailabilityDto.getAppointmentStatus());
        }
        return doctorUnAvailabilityRepository.save(doctorUnAvailability);
    }

    private DoctorUnAvailability getDoctorUnAvailability(DoctorMarkAvailabilityDto doctorMarkAvailabilityDto, DoctorInClinic doctorInClinic) throws DoctorUnAvailabilityNotFoundException {
        if (!doctorMarkAvailabilityDto.getDoctorUnAvailability()) {
            List<DoctorUnAvailability> byDoctorInClinicId = doctorUnAvailabilityRepository.findByDoctorInClinicIdAndStatusAndOrderByCreatedAtDescAt(doctorInClinic.getId(), DoctorAvailabilityStatus.UNAVAILABLE);
            if (!CollectionUtils.isEmpty(byDoctorInClinicId)) {
                return byDoctorInClinicId.stream().findFirst().orElseThrow(DoctorUnAvailabilityNotFoundException::new);
            }
        }
        return new DoctorUnAvailability();
    }

    public Boolean getDoctorUnAvailability(DoctorInClinic doctorInClinic) throws DoctorUnAvailabilityNotFoundException {
        if(doctorInClinic.getDoctorUnAvailability() == null || doctorInClinic.getEndDate() == null || doctorInClinic.getStartDate() == null) return false;
        if(doctorInClinic.getDoctorUnAvailability() && doctorInClinic.getStartDate().toInstant().getEpochSecond() > Instant.now().getEpochSecond() && Instant.now().getEpochSecond() < doctorInClinic.getEndDate().toInstant().getEpochSecond()) return false;
        if(!doctorInClinic.getDoctorUnAvailability()) return false;
        List<DoctorUnAvailability> byDoctorInClinicId = doctorUnAvailabilityRepository.findByDoctorInClinicIdAndStatusAndOrderByCreatedAtDescAt(doctorInClinic.getId(), DoctorAvailabilityStatus.UNAVAILABLE);
        if (CollectionUtils.isEmpty(byDoctorInClinicId)) return false;
        if(Instant.now().getEpochSecond() < doctorInClinic.getEndDate().toInstant().plusNanos(60000000000L).getEpochSecond()) return doctorInClinic.getDoctorUnAvailability();
        doctorInClinic.setEndDate(null);
        doctorInClinic.setStartDate(null);
        doctorInClinic.setDoctorUnAvailability(false);
        doctorInClinicRepository.save(doctorInClinic);
        DoctorUnAvailability doctorUnAvailability = byDoctorInClinicId.stream().findFirst().orElseThrow(DoctorUnAvailabilityNotFoundException::new);
        doctorUnAvailability.setType(doctorUnAvailability.getType());
        doctorUnAvailability.setStatus(DoctorAvailabilityStatus.AVAILABLE);
        setDoctorUnAvailabilityFlows(doctorUnAvailabilityRepository.save(doctorUnAvailability));
        return doctorInClinic.getDoctorUnAvailability();
    }

    public List<DoctorInClinicForOrder> getDoctorsFromDicList(Long[] dicIds) {
        return doctorInClinicRepository.findAllByIdIn(dicIds).stream().map(a->projectionFactory.createNullableProjection(DoctorInClinicForOrder.class, a)).collect(Collectors.toList());
    }

    @Transactional
    public Map<Long, List<DoctorInClinicSearchProjection>> fetchDoctorsBySearchType(String name, SearchType searchType) {
        List<DoctorInClinic> searchResults = new ArrayList<>();
        if (searchType.equals(SearchType.DOCTOR)){
            searchResults = doctorInClinicRepository.findByDoctorNameContaining(name);
        }else if (searchType.equals(SearchType.DOCTOR_IN_CLINIC)){
            searchResults = doctorInClinicRepository.findByClinicNameContaining(name);
        }

        return searchResults.stream().map(doctorInClinic -> {
            return projectionFactory.createProjection(DoctorInClinicSearchProjection.class, doctorInClinic);
        }).collect(Collectors.groupingBy(x->x.getClinic().getId()));
    }

    public Page<DoctorInClinicSearch> fetchActiveClinicsForSearch(Pageable pageable) {
        return doctorInClinicRepository.fetchActiveClinicsForSearch(pageable);
    }


    /**
     *Get distance from lat long
     *
     * @param dicId
     * @param latitude
     * @param longitude
     * @return return distance in KM
     */
    public Double getClinicDistance(Long dicId,  Double latitude, Double longitude) {

        DoctorInClinic doctorInClinic = doctorInClinicRepository.findById(dicId).get();
        Clinic clinic = doctorInClinic.getClinic();
        Double clinicLatitude = clinic.getLatitude();
        Double clinicLongitude  = clinic.getLongitude();
        return DistanceUtil.haversine(latitude, longitude, clinicLatitude, clinicLongitude);
    }

    public ClinicTimingDto getClinicTime(DoctorInClinic doctorInClinic) throws DoctorUnAvailabilityNotFoundException {
        ClinicTimingDto clinicTimingDto = new ClinicTimingDto();
        Map<Time, List<DoctorInClinicTimeSlot>> collect = doctorInClinic.getDoctorInClinicTimeSlots().stream().filter(a->a.getTimeSlot() != null).sorted(Comparator.comparingLong(a->a.getTimeSlot().getStart().getTime())).collect(Collectors.groupingBy(a -> a.getTimeSlot().getStart()));

        if(CollectionUtils.isEmpty(collect)) return clinicTimingDto;
        Optional<Time> first = collect.keySet().stream().findFirst();
        if(!first.isPresent()) {
            return clinicTimingDto;
        }
        clinicTimingDto.setStartTiming(first.get());
        long count = collect.keySet().stream().count();
        clinicTimingDto.setEndTiming(collect.keySet().stream().skip(count - 1).findFirst().get());
        return clinicTimingDto;
    }

    public List<Long> fetchClinicIdsFromDicIds(Long[] dicIds) {
        return doctorInClinicRepository.findByIdIn(dicIds).stream().filter(a->a.getClinic() != null).map(a -> a.getClinic().getId()).collect(Collectors.toList());
    }

    public List<Long> fetchDicIdListFromDicId(Long dicId) {
        return doctorInClinicRepository.fetchById(dicId).getClinic().getDoctorInClinics().stream().map(DoctorInClinic::getId).collect(Collectors.toList());
    }
}

