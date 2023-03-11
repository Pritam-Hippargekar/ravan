package co.arctern.api.emr.controller;

import co.arctern.api.emr.domain.dto.DoctorDetailsDto;
import co.arctern.api.emr.domain.dto.DoctorTagsDto;
import co.arctern.api.emr.domain.dto.NextAvailabeTimeSlot;
import co.arctern.api.emr.domain.projection.DoctorForDoxper;
import co.arctern.api.emr.domain.projection.DoctorForElasticSearch;
import co.arctern.api.emr.domain.projection.DoctorList;
import co.arctern.api.emr.domain.projection.DoctorTagsDetails;
import co.arctern.api.emr.domain.projection.DoctorsForMessageRouter;
import co.arctern.api.emr.options.ConsultationType;
import co.arctern.api.emr.search.domain.Doctor;
import co.arctern.api.emr.search.domain.projection.Doctors;
import co.arctern.api.emr.service.api.DoctorRepository;
import co.arctern.api.emr.service.middlelayer.DoctorAndClinicService;
import co.arctern.api.emr.service.middlelayer.DoctorAppointmentDashboardService;
import co.arctern.api.emr.service.middlelayer.DoctorDetailsService;
import co.arctern.api.emr.service.middlelayer.DoctorReviewService;
import co.arctern.api.emr.transformer.DoctorTransformer;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

@BasePathAwareController
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorTransformer doctorTransformer;

    @Autowired
    private ProjectionFactory projectionFactory;

    @Autowired
    private ElasticsearchTemplate doctorSearchRepository;

    @Autowired
    private DoctorDetailsService doctorDetailsService;

    @Autowired
    private PagedResourcesAssembler<Doctors> doctorPagedResourcesAssembler;

    @Autowired
    private DoctorAppointmentDashboardService doctorAppointmentDashboardService;

    @RequestMapping("/doctor/all")
    @Transactional
    @CrossOrigin
    public ResponseEntity<List<Doctor>> findAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        List<co.arctern.api.emr.domain.Doctor> all = doctorRepository.findAllDoctorsIsListedTrue();
        for (co.arctern.api.emr.domain.Doctor doctor : all) {
            Doctor doctorTemp = doctorTransformer.transformToESDoctor(projectionFactory.createProjection(DoctorForElasticSearch.class, doctor), true);
            doctorTemp.getDoctorInClinics().stream().forEach(doctorInClinic -> {
                doctorInClinic.setClinicTimings(null);
            });
            doctors.add(doctorTemp);
        }
        return ResponseEntity.ok(doctors);
    }

    @PostMapping("/doctors/populate-availability/v1")
    @Transactional
    @CrossOrigin
    public ResponseEntity populateDoctorAvailabilityV1() {
        return ResponseEntity.ok(doctorDetailsService.populateDoctorAvailabilityV1());
    }

    @RequestMapping("/doctor-mark-listed")
    @CrossOrigin
    public ResponseEntity<String> markDoctorsListed() {
        doctorDetailsService.markDoctorsListed();
        return ResponseEntity.ok("Success");
    }

    @RequestMapping("/doctor/{id}")
    @Transactional
    @CrossOrigin
    public @ResponseBody
    ResponseEntity<Doctor> findDoctorById(@PathVariable Long id) {
        co.arctern.api.emr.domain.Doctor doctor = doctorRepository.findById(id).get();
        doctor.setDoctorInClinics(doctor.getDoctorInClinics().stream().filter(a->a.getIsActive()!=null && a.getIsActive()).collect(Collectors.toSet()));
        Doctor doctorES = doctorTransformer.transformToESDoctor(projectionFactory.createProjection(DoctorForElasticSearch.class, doctor),false);
        return ResponseEntity.ok(doctorES);
    }

    @GetMapping("/doctor/{id}/details")
    @CrossOrigin
    public ResponseEntity<DoctorDetailsDto> findDoctorDetailsById(@PathVariable Long id) throws Exception {
        DoctorDetailsDto doctorDetailsDto = doctorDetailsService.findDoctorDetailsById(id);
        return ResponseEntity.ok(doctorDetailsDto);
    }

    @PostMapping("/doctor/details")
    @CrossOrigin
    public ResponseEntity<Boolean> updateDoctorDetailsById(@RequestBody DoctorDetailsDto doctorDetailsDto) throws Exception {
        return ResponseEntity.ok(doctorDetailsService.addOrUpdateDoctorDetailsById(doctorDetailsDto));
    }

    /**
     * To add or update doctor from doxper
     * This is api crated as part to meddo-doxper integration
     * @param doctorDetailsDto
     * @return Doctor Object
     * @throws Exception
     */
    @PostMapping("/doxper/doctor/details")
    @CrossOrigin
    public ResponseEntity<DoctorForDoxper> updateDoctorDetailsByIdV1(@RequestBody DoctorDetailsDto doctorDetailsDto) throws Exception {
        return ResponseEntity.ok(doctorDetailsService.addOrUpdateDoctorDetailsByIdV1(doctorDetailsDto));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_GROWTH')")
    @CrossOrigin
    @PostMapping("/doctor/tags")
    public ResponseEntity<List<DoctorTagsDetails>> createOrUpdateDoctorTags(@RequestBody DoctorTagsDto doctorTagsDto){
        return ResponseEntity.ok(doctorDetailsService.createOrUpdateDoctorTags(doctorTagsDto));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_GROWTH')")
    @GetMapping("/doctor/{id}/details/v1")
    @CrossOrigin
    public ResponseEntity<DoctorDetailsDto> findDoctorMetaInformationAndTagsById(@PathVariable Long id) throws Exception {
        DoctorDetailsDto doctorDetailsDto = doctorDetailsService.findDoctorMetaInformationAndTagsById(id);
        return ResponseEntity.ok(doctorDetailsDto);
    }

    @RequestMapping("/doctor/code/{code}")
    @Transactional
    @CrossOrigin
    public ResponseEntity<Doctor> findDoctorById(@PathVariable String code) {
        co.arctern.api.emr.domain.Doctor doctor = doctorRepository.findByCode(code).get();
        Doctor doctorES = doctorTransformer.transformToESDoctor(projectionFactory.createProjection(DoctorForElasticSearch.class, doctor),false);
        return ResponseEntity.ok(doctorES);
    }

    @GetMapping("/doctor/searches")
    @Transactional
    @CrossOrigin
    public ResponseEntity<?> getSearchWiseDoctors(
            @RequestParam(name = "speciality", required = false) Long[] specialities,
            @RequestParam(name = "available", required = false) String available,
            @RequestParam(name = "latitude", required = false) Double latitude,
            @RequestParam(name = "longitude", required = false) Double longitude,
            @RequestParam(name = "search", required = false) String search,
            Pageable page) {


        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if (search != null) {
            qb.must(QueryBuilders.multiMatchQuery(search,
                    "name", "doctorInClinics.clinic.name", "doctorInClinics.clinic.address")
            );
        }

        if (specialities != null) {
            for (Long speciality : specialities) {
                qb.should(QueryBuilders.matchQuery("speciality.id", speciality));
            }
        }

        if (available != null) {
            //TODO:
            SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
            String day = simpleDateformat.format(new Date()).toUpperCase();

            switch (available) {
                case "NOW":

            }
        }


        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
//                .withFilter(boolQuery().must(termQuery("id", 1)))
                .withFilter(qb)
                .withPageable(page)
                .build();

        Page<Doctors> map = doctorSearchRepository.queryForPage(searchQuery, co.arctern.api.emr.search.domain.Doctor.class)
                .map(doctor -> projectionFactory.createProjection(Doctors.class, doctor));
        return new ResponseEntity<>(doctorPagedResourcesAssembler.toResource(map), HttpStatus.OK);
    }

    @GetMapping("/doctors/active-all")
    @Transactional
    @CrossOrigin
    public ResponseEntity<Page<DoctorsForMessageRouter>> findAllActiveDoctors(Pageable pageable) {
        return ResponseEntity.ok(doctorDetailsService.findAllActiveDoctors(pageable));
    }

    @Autowired
    DoctorReviewService doctorReviewService;
    @GetMapping("/test/rating")
    @CrossOrigin
    public ResponseEntity<Float> testRatingData(@RequestParam Long doctorId ){
        float positiveRatingPercentage = doctorReviewService.calculatePositiveReviewPercentagecalculatePositiveReviewPercentage(doctorId);
        return ResponseEntity.ok(positiveRatingPercentage);
    }

    @PostMapping("/doctors/populate-availability")
    @Transactional
    @CrossOrigin
    public ResponseEntity populateDoctorAvailability() {
        return ResponseEntity.ok(doctorDetailsService.populateDoctorAvailability());
    }

    @RequestMapping("/doctor/next-available-slot/{doctorId}")
    @Transactional
    @CrossOrigin
    public @ResponseBody
    ResponseEntity<NextAvailabeTimeSlot> findDoctorNextAvailableTimeSlotById(@RequestParam(value = "doctorId") Long doctorId,
                                                                             @RequestParam(value = "startDate") LocalDate startDate,
                                                                             @RequestParam(value = "endDate") LocalDate endDate,
                                                                             @RequestParam(value = "consultationType", defaultValue = "E_CONSULTATION") ConsultationType consultationType) throws ParseException {
        return ResponseEntity.ok(
                doctorAppointmentDashboardService.getNextAvailableTimeSlot(doctorId, consultationType, startDate, endDate)
        );
    }

}
