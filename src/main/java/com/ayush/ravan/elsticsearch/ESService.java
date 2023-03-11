package com.ayush.ravan.elsticsearch;
package co.arctern.api.emr.service.middlelayer;

import co.arctern.api.emr.CustomException.BadParameterException;
import co.arctern.api.emr.domain.dto.NextAvailabeTimeSlot;
import co.arctern.api.emr.options.DoxperStatus;
import co.arctern.api.emr.options.OrderBy;
import co.arctern.api.emr.options.SearchFor;
import co.arctern.api.emr.response.GlobalSearchResponse;
import co.arctern.api.emr.response.GlobalSearchServiceabilityResponse;
import co.arctern.api.emr.search.domain.Doctor;
import co.arctern.api.emr.search.response.DiagnosticType;
import co.arctern.api.emr.search.response.Speciality;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.sort.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

@Service
@Slf4j
public class ESService {


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private DoctorAndClinicService doctorAndClinicService;

    @Autowired
    private DoctorAppointmentDashboardService doctorAppointmentDashboardService;

    @Value("${doctor.search.radius.filter:50L}")
    private Long doctorSearchRadiusFilter;


    public QueryBuilder applyFilters(Long clinicId, String search, Long[] specialities, String[] cities, String[] clusters, String slug, Boolean isCamp, Long doctorId, Boolean isListed, Boolean isAvailableOnline) {

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

        if (doctorId != null) {
            qb.must(QueryBuilders.matchQuery("id", doctorId));
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

            List<FunctionScoreQueryBuilder.FilterFunctionBuilder> functionBuilderList = new ArrayList<>();
            functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(nameScript)));

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

    public AggregatedPage<Doctor> getDoctors(Long clinicId, String search, Long[] specialities, String[] cities, String[] clusters
            , String slug, Boolean isCamp, Long doctorId, Boolean isListed, Boolean isAvailableOnline, Boolean isAvailableAtClinic, String doctorAvailabilityFilter, OrderBy orderBy
            , String sortBy, Double latitude, Double longitude, Pageable page) {
        AggregatedPage<Doctor> doctors = null;
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(doctorAndClinicService.applyFilters(
                        clinicId, search, specialities, cities, clusters, slug, isCamp, doctorId
                        , false, null, isListed, isAvailableOnline, isAvailableAtClinic, doctorAvailabilityFilter, longitude, latitude
                ))
                .withSort(doctorAndClinicService.applySortParameters(
                        isAvailableOnline, orderBy.equals(OrderBy.ASC), sortBy, latitude, longitude
                ))
                .withPageable(page)
                .build();

        doctors = elasticsearchTemplate.queryForPage(searchQuery, co.arctern.api.emr.search.domain.Doctor.class);
        return doctors;
    }

    public List<Doctor> fetchAllDoctors(String keyword, Boolean isActive, Boolean isListed, Boolean isAvailableOnline, Boolean isAvailableAtClinic) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(buildQuery(keyword, null, null, isActive, isListed, isAvailableOnline, isAvailableAtClinic)).build();
        List<Doctor> doctorList = elasticsearchTemplate.queryForList(searchQuery, Doctor.class);

        return doctorList;
    }

    public Page<Doctor> fetchDoctors(String keyword, Double longitude, Double latitude, Boolean isActive, Boolean isListed, Boolean isAvailableOnline, Boolean isAvailableAtClinic, String sortBy, String sortDirection, String auth, Pageable pageable) throws BadParameterException {

        if (sortBy!=null && sortBy.equalsIgnoreCase("location") && (longitude == null || latitude ==null)){
            throw new BadParameterException();
        } else {
            sortBy = "popularity";
        }

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(buildQuery(keyword, longitude, latitude, isActive, isListed, isAvailableOnline, isAvailableAtClinic))
                .withSort(applySorting(sortBy, sortDirection, longitude, latitude, isAvailableOnline))
                .withPageable(pageable)
                .build();
        log.info(" Elastic search query " + searchQuery.getQuery().toString());
        Page<Doctor> doctors = elasticsearchTemplate.queryForPage(searchQuery, Doctor.class);
        return doctors;
    }

    /**
     * Prepare doctor search query
     * @param keyword
     * @param available
     * @param specialities
     * @param longitude
     * @param latitude
     * @param isActive
     * @param isListed
     * @param isAvailableOnline
     * @param isAvailableAtClinic
     * @param sortBy
     * @param sortDirection
     * @param auth
     * @param pageable
     * @return Elastic search query
     * @throws BadParameterException
     */
    public Page<Doctor> fetchDoctorsV2(String keyword,  String available, Long[] specialities, Double longitude, Double latitude, Boolean isActive, Boolean isListed, Boolean isAvailableOnline,
                                       Boolean isAvailableAtClinic, String sortBy, String sortDirection, String auth, Pageable pageable)
            throws BadParameterException {
        if (sortBy!=null && sortBy.equalsIgnoreCase("location") && (longitude == null || latitude ==null)){
            throw new BadParameterException();
        }

        if( keyword != null && keyword.length() >= 4 ) {
            List<String> drValues = Arrays.asList("Dr ", "Dr.");
            if (drValues.stream().map(x -> x.substring(0, 3)).anyMatch(keyword.substring(0, 3)::equalsIgnoreCase)) {
                keyword = keyword.substring(3, keyword.length());
            }
        }

        boolean asc = getSortDirection(sortDirection);
        boolean searchWithoutKeyword = StringUtils.isEmpty(keyword);
        SearchQuery searchQuery = null;
        if(searchWithoutKeyword){
            searchQuery = new NativeSearchQueryBuilder().withQuery(buildQueryV2(keyword, available, specialities, longitude, latitude, isActive, isListed, isAvailableOnline, isAvailableAtClinic))
                    .withSort(doctorAndClinicService.applySortParameters(isAvailableOnline, isAvailableAtClinic, asc, sortBy, latitude, longitude))
                    .withPageable(pageable)
                    .build();

        }else {
            searchQuery = new NativeSearchQueryBuilder().withQuery(buildQueryV2(keyword, available, specialities, longitude, latitude, isActive, isListed, isAvailableOnline, isAvailableAtClinic))
                    .withPageable(pageable)
                    .build();
        }

        log.info(" Elastic search query " + searchQuery.getQuery().toString());
        Page<Doctor> doctors = elasticsearchTemplate.queryForPage(searchQuery, Doctor.class);

        return  doctors;
    }

    /**
     * check order acs then return true otherwise false
     * @param sortDirection
     * @return true/false
     */
    private boolean getSortDirection(String sortDirection){
        if(StringUtils.isNotEmpty(sortDirection)){
            if(sortDirection.equalsIgnoreCase("asc")){
                return true;
            }
        }
        return false;
    }

    public List<Doctor> fetchDoctorsV1(String keyword, Double longitude, Double latitude, Boolean isActive, Boolean isListed, Boolean isAvailableOnline, Boolean isAvailableAtClinic) throws BadParameterException {

        if (longitude == null || latitude == null) {
            throw new BadParameterException();
        }

        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(buildQuery(keyword, longitude, latitude, isActive, isListed, isAvailableOnline, isAvailableAtClinic)).build();
        return elasticsearchTemplate.queryForList(searchQuery, Doctor.class);

    }

    public BoolQueryBuilder buildQuery(String keyword, Double longitude, Double latitude, Boolean isActive, Boolean isListed, Boolean isAvailableOnline, Boolean isAvailableAtClinic) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();

        if (keyword != null && keyword.toLowerCase().startsWith("dr")) {
            keyword = keyword.replaceFirst(".", "");
            keyword = keyword.replaceFirst("^dr", "");
        }
        if (isActive != null) {
            BoolQueryBuilder qbForIsActive = QueryBuilders.boolQuery();
            qb.must(qbForIsActive.should(QueryBuilders.matchQuery("isActive", isActive)));
        }
        if (isListed != null) {
            BoolQueryBuilder qbForIsListed = QueryBuilders.boolQuery();
            qb.must(qbForIsListed.should(QueryBuilders.matchQuery("doctorInClinics.isListed", true)));
        }
        if (isAvailableOnline != null) {
            qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("doctorInClinics.isAvailableOnline", isAvailableOnline)));
        }
        if (isAvailableAtClinic != null) {
            qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("doctorInClinics.isAvailableAtClinic", isAvailableAtClinic)));
        }

        if (doctorSearchRadiusFilter != null && longitude != null && latitude != null) {
            if (isAvailableAtClinic !=null && isAvailableAtClinic) {
                qb.must(QueryBuilders
                        .geoDistanceQuery("doctorInClinics.clinic.location")
                        .point(latitude, longitude)
                        .distance(doctorSearchRadiusFilter, DistanceUnit.KILOMETERS));
            }
        }
        if (keyword != null && !keyword.isEmpty()) {

            BoolQueryBuilder searchQb = QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("name", keyword).operator(Operator.OR).boost(10f))
                    .should(QueryBuilders.matchPhrasePrefixQuery("name", keyword).boost(5f))
                    .should(QueryBuilders.matchQuery("doctorInClinics.clinic.name", keyword).operator(Operator.AND).boost(3f))
                    .should(QueryBuilders.matchPhrasePrefixQuery("doctorInClinics.clinic.name", keyword).boost(1f));

            Map<String, Object> map = new HashMap<>();
            String drPrefix = "Dr " + keyword.trim();
            String drDotPrefix = "Dr. " + keyword.trim();
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

            List<FunctionScoreQueryBuilder.FilterFunctionBuilder> functionBuilderList = new ArrayList<>();
            functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(nameScript)));

            qb.must(QueryBuilders.functionScoreQuery(searchQb, functionBuilderList.toArray(new FunctionScoreQueryBuilder.FilterFunctionBuilder[0])));
        }

        return qb;
    }

    /**
     * Elastic search query
     * @param keyword
     * @param available
     * @param specialities
     * @param longitude
     * @param latitude
     * @param isActive
     * @param isListed
     * @param isAvailableOnline
     * @param isAvailableAtClinic
     * @return elastic search query
     */
    public BoolQueryBuilder buildQueryV2(String keyword,  String available, Long[] specialities, Double longitude, Double latitude, Boolean isActive, Boolean isListed, Boolean isAvailableOnline, Boolean isAvailableAtClinic) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();

        if (keyword != null && keyword.toLowerCase().startsWith("dr")) {
            keyword = keyword.replaceFirst(".", "");
            keyword = keyword.replaceFirst("^dr", "");
        }
        if (isActive != null) {
            BoolQueryBuilder qbForIsActive = QueryBuilders.boolQuery();
            qb.must(qbForIsActive.should(QueryBuilders.matchQuery("isActive", isActive)));
        }
        if (isListed != null) {
            BoolQueryBuilder qbForIsListed = QueryBuilders.boolQuery();
            qb.must(qbForIsListed.should(QueryBuilders.matchQuery("doctorInClinics.isListed", true)));
        }

        if (isAvailableOnline != null && isAvailableOnline && isAvailableAtClinic != null && isAvailableAtClinic) {
            //Do nothing
            if(available != null ) {
                switch (available.toLowerCase().trim()) {
                    case "today":
                        qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("availableTodayInClinic", true).operator(Operator.OR))
                                .should(QueryBuilders.matchQuery("availableTodayOnline", true)));
                    case "tomorrow":
                        qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("availableTomorrowInClinic", true).operator(Operator.OR))
                                .should(QueryBuilders.matchQuery("availableTomorrowOnline", true)));
                    case "next7days":
                        qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("availableNext7DayInClinic", true).operator(Operator.OR))
                                .should(QueryBuilders.matchQuery("availableNext7DayOnline", true)));
                }
            }
        } else {
            if (isAvailableOnline != null) {
                qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("doctorInClinics.isAvailableOnline", isAvailableOnline)));
            }
            if (isAvailableAtClinic != null) {
                qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("doctorInClinics.isAvailableAtClinic", isAvailableAtClinic)));
            }

            if (isAvailableAtClinic!=null && available !=null && isAvailableAtClinic) {
                switch (available.toLowerCase().trim()) {
                    case "today":
                        qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("availableTodayInClinic", true)));
                        break;
                    case "tomorrow":
                        qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("availableTomorrowInClinic", true)));
                        break;
                    case "next7days":
                        qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("availableNext7DayInClinic", true)));
                        break;
                }
            }else if (isAvailableOnline!=null && available !=null && isAvailableOnline) {
                switch (available.toLowerCase().trim()) {
                    case "today":
                        qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("availableTodayOnline", true)));
                        break;
                    case "tomorrow":
                        qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("availableTomorrowOnline", true)));
                        break;
                    case "next7days":
                        qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("availableNext7DayOnline", true)));
                        break;
                }
            }else{
                if(available != null ) {
                    switch (available.toLowerCase().trim()) {
                        case "today":
                            qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("availableTodayInClinic", true).operator(Operator.OR))
                                    .should(QueryBuilders.matchQuery("availableTodayOnline", true)));
                            break;
                        case "tomorrow":
                            qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("availableTomorrowInClinic", true).operator(Operator.OR))
                                    .should(QueryBuilders.matchQuery("availableTomorrowOnline", true)));
                            break;
                        case "next7days":
                            qb.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("availableNext7DayInClinic", true).operator(Operator.OR))
                                    .should(QueryBuilders.matchQuery("availableNext7DayOnline", true)));
                            break;
                    }
                }
            }

        }

        if (specialities != null) {
            BoolQueryBuilder specialityQb = QueryBuilders.boolQuery();
            for (Long speciality : specialities) {
                specialityQb.should(QueryBuilders.matchQuery("speciality.id", speciality));
            }
            qb.must(specialityQb);
        }

        if (doctorSearchRadiusFilter != null && longitude != null && latitude != null) {
            if (isAvailableAtClinic !=null && isAvailableAtClinic) {
                qb.must(QueryBuilders
                        .geoDistanceQuery("doctorInClinics.clinic.location")
                        .point(latitude, longitude)
                        .distance(doctorSearchRadiusFilter, DistanceUnit.KILOMETERS));
            }
        }
        if (keyword != null && !keyword.isEmpty()) {

            BoolQueryBuilder searchQb = QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("name", keyword).operator(Operator.OR).boost(50f))
                    .should(QueryBuilders.prefixQuery("name", keyword).boost(40f))
                    .should(QueryBuilders.matchQuery("name", keyword).fuzziness(Fuzziness.AUTO).operator(Operator.OR).boost(30f))

                    .should(QueryBuilders.matchQuery("speciality.name", keyword).operator(Operator.OR).boost(40f))
                    .should(QueryBuilders.matchQuery("speciality.prefix", keyword).fuzziness(Fuzziness.AUTO).operator(Operator.OR).boost(30f))
                    .should(QueryBuilders.matchQuery("speciality.name", keyword)
                            .fuzziness(Fuzziness.AUTO).operator(Operator.OR).boost(30f))

                    .should(QueryBuilders.matchQuery("doctorInClinics.clinic.name", keyword)
                            .fuzziness(Fuzziness.AUTO).operator(Operator.AND).boost(10f))
                    .should(QueryBuilders.prefixQuery("doctorInClinics.clinic.name", keyword).boost(10f))

                    .should(QueryBuilders.matchQuery("qualificationsDetail.description", keyword).fuzziness(Fuzziness.AUTO).boost(5f))
                    .should(QueryBuilders.prefixQuery("qualificationsDetail.description", keyword).boost(5f))

                    .should(QueryBuilders.matchQuery("affiliation", keyword).fuzziness(Fuzziness.AUTO).boost(5f))
                    .should(QueryBuilders.prefixQuery("affiliation", keyword).boost(5f))

                    .should(QueryBuilders.matchQuery("about", keyword).fuzziness(Fuzziness.AUTO).boost(5f))
                    .should(QueryBuilders.prefixQuery("about", keyword).boost(1f));


            Map<String, Object> map = new HashMap<>();
            String drPrefix = "Dr " + keyword.trim();
            String drDotPrefix = "Dr. " + keyword.trim();
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

            List<FunctionScoreQueryBuilder.FilterFunctionBuilder> functionBuilderList = new ArrayList<>();
            functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(nameScript)));

            if(StringUtils.isNotEmpty(keyword)) {
                Script locationScore = new Script(
                        ScriptType.INLINE,
                        "painless",
                        "def distance = (doc['doctorInClinics.clinic.location'].arcDistance(params.latitude,params.longitude))/1000; "
                                + "if (distance <= 3 ) 1.1;"
                                + "else if (distance > 3 && distance <=7)  1.08;"
                                + "else if (distance > 7 && distance <=20 )  1.06;"
                                + "else 1;",
                        new HashMap<String, Object>() {
                            {
                                put("latitude", latitude);
                                put("longitude", longitude);
                            }
                        });
                functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(locationScore)));
                qb.must(QueryBuilders.functionScoreQuery(searchQb, functionBuilderList.toArray(new FunctionScoreQueryBuilder.FilterFunctionBuilder[0])));
            }
        }

        return qb;
    }


    protected SortBuilder applySorting(String sortBy, String sortDirection, Double latitude, Double longitude, Boolean isAvailableOnline) {
        SortOrder sortOrder = SortOrder.valueOf(sortDirection);
        switch (sortBy.toLowerCase()) {
            case "fee":
                return SortBuilders.fieldSort((isAvailableOnline != null && isAvailableOnline) ? "doctorInClinics.econsultationFee" : "doctorInClinics.consultationFee")
                        .order(sortOrder);

            case "location":
                GeoDistanceSortBuilder a = SortBuilders.geoDistanceSort("doctorInClinics.clinic.location", latitude, longitude)
                        .unit(DistanceUnit.KILOMETERS)
                        .order(sortOrder)
                        .ignoreUnmapped(true);
                return null;

            case "popularity":
                return SortBuilders.fieldSort("popularity").order(sortOrder);

            case "rating":
                return SortBuilders.fieldSort("rating").order(sortOrder);

            default:
                return SortBuilders.fieldSort("doctorInClinics.year_of_exp").order(sortOrder);
        }
    }

    public Page<DiagnosticType> fetchTests(String keyword, Boolean isCombo, Double longitude, Double latitude, String pinCode, String sortBy, String sortDirection, String auth, Pageable pageable) {
        SearchQuery searchQuery = null;
        if(StringUtils.isEmpty(sortBy)){
            searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(applyDiagnotisTypeFilters(keyword, isCombo, pinCode))
                    .withPageable(pageable)
                    .build();
        }else{
            searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(applyDiagnotisTypeFilters(keyword, isCombo, pinCode))
                    .withSort(applyDiagnotisTypeSorting(sortBy, sortDirection, longitude, latitude))
                    .withPageable(pageable)
                    .build();
        }

        Page<DiagnosticType> diagnosticTypes = elasticsearchTemplate.queryForPage(searchQuery, DiagnosticType.class);
        return diagnosticTypes;
    }
    public BoolQueryBuilder applyDiagnotisTypeFilters(String keyword, Boolean isCombo, String pinCode) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if (pinCode != null && !pinCode.isEmpty()) {
            BoolQueryBuilder qbForIsAvailableOnline = QueryBuilders.boolQuery();
            qb.must(qbForIsAvailableOnline.should(QueryBuilders.matchQuery("diagnosticTypeInLabs.diagnosticLab.clusterAndDiagnosticLabs.cluster.areas.pinCode", pinCode)));
        }
        if (isCombo != null && isCombo) {
            BoolQueryBuilder qbForIsCombo = QueryBuilders.boolQuery();
            qb.must(qbForIsCombo.should(QueryBuilders.matchQuery("isCombo", isCombo)));
        } else {
            BoolQueryBuilder qbForIsCombo = QueryBuilders.boolQuery();
            qb.mustNot(qbForIsCombo.should(QueryBuilders.matchQuery("isCombo", true)));
        }
        if (keyword != null && !keyword.isEmpty()) {
            BoolQueryBuilder searchQb = QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("name", keyword).operator(Operator.OR).boost(10f))
                    .should(QueryBuilders.matchPhrasePrefixQuery("name", keyword).boost(5f))
                    .should(QueryBuilders.matchQuery("alias", keyword).boost(10f))
                    .should(QueryBuilders.matchQuery("abbreviation", keyword).boost(10f));
            qb.must(searchQb);
        }
        return qb;
    }

    private SortBuilder applyDiagnotisTypeSorting(String sortBy, String sortDirection, Double longitude, Double latitude) {
        SortOrder sortOrder = SortOrder.valueOf(sortDirection);
        switch (sortBy.toLowerCase()) {
            case "fee":
                return SortBuilders.fieldSort("diagnosticTypeInLabs.price")
                        .order(sortOrder);
            case "name":
                return SortBuilders.fieldSort("name").order(sortOrder);
            default:
                return SortBuilders.fieldSort(sortBy).order(sortOrder);
        }
    }

    private SortBuilder applyDiagnotisTypeSorting(String sortBy, String sortDirection) {
        SortOrder sortOrder = SortOrder.valueOf(sortDirection);
        switch (sortBy.toLowerCase()) {
            case "fee":
                return SortBuilders.fieldSort("diagnosticTypeInLabs.price")
                        .order(sortOrder);
            case "name":
                return SortBuilders.fieldSort("name").order(sortOrder);
            default:
                return SortBuilders.fieldSort(sortBy).order(sortOrder);
        }
    }


    public GlobalSearchResponse globalSearch(String keyword, Double longitude, Double latitude, String pinCode, Boolean isAvailableOnline, Boolean isAvailableAtClinic, String sortBy, String sortDirection,
                                             String auth, Pageable pageable) throws BadParameterException {

        if( keyword != null && keyword.length() >= 3 ) {
            List<String> drValues = Arrays.asList("Dr ", "Dr.");
            if (drValues.stream().map(x -> x.substring(0, 3)).anyMatch(keyword.substring(0, 3)::equalsIgnoreCase)) {
                keyword = keyword.substring(3, keyword.length());
            }
        }

        Page<Doctor> doctors = fetchDoctors(keyword, longitude, latitude, true, true, isAvailableOnline, isAvailableAtClinic, sortBy, sortDirection, auth, pageable);
        Page<DiagnosticType> tests = null;
        Page<DiagnosticType> packages = null;
        if(StringUtils.isNotEmpty(pinCode)) {
            tests = fetchTests(keyword, false, longitude, latitude, pinCode, sortBy, sortDirection, auth, pageable);
            packages = fetchTests(keyword, true, longitude, latitude, pinCode, sortBy, sortDirection, auth, pageable);

            tests = tests.map(dt -> {
                dt.setDiagnosticTypeInLabs(Arrays.asList(dt.getDiagnosticTypeInLabs().stream().findFirst().get()));
                return dt;
            });

            packages = packages.map( pk -> {
                pk.setDiagnosticTypeInLabs(Arrays.asList(pk.getDiagnosticTypeInLabs().stream().findFirst().get()));
                return pk;
            });
        }

        return new GlobalSearchResponse(doctors, tests, packages, null);
    }

    public GlobalSearchResponse globalSearchV2(String keyword, String available, Long[] specialitiesParam, Double longitude, Double latitude, String pinCode,
                                               Boolean isAvailableOnline, Boolean isAvailableAtClinic,String searchFor, String sortBy, String sortDirection,
                                               String auth, Pageable pageable) throws BadParameterException {

        keyword = StringUtils.trim(keyword);
        if( keyword != null && keyword.length() < 3){
            keyword = ""; // do not search till 3 character
        }

        Page<Doctor> doctors = fetchDoctorsV2(keyword, available, specialitiesParam, longitude, latitude, true, true, isAvailableOnline, isAvailableAtClinic,
                sortBy, sortDirection, auth, pageable);
        //set next available timeslot for the doctor
        doctors.getContent().forEach(doctor -> {
            try {
                doctor.setNextAvailabeTimeSlot(doctorAppointmentDashboardService.findNextAvailabilityForDoctor(doctor.getId()));
            } catch (ParseException e) {
                log.error("exception to find next available slot for doctor : {} ", doctor.getId());
            }
        });

        Page<Speciality> specialities = globalSearchSpeciality(keyword, "ASC", pageable);

        Page<DiagnosticType> tests = null;
        Page<DiagnosticType> packages = null;
        if(SearchFor.ALL.name().equalsIgnoreCase(searchFor) && StringUtils.isNotEmpty(pinCode)) {
            tests = fetchTests(keyword, false, longitude, latitude, pinCode, sortBy, sortDirection, auth, pageable);
            packages = fetchTests(keyword, true, longitude, latitude, pinCode, sortBy, sortDirection, auth, pageable);

            tests = tests.map(dt -> {
                dt.setDiagnosticTypeInLabs(Arrays.asList(dt.getDiagnosticTypeInLabs().stream().findFirst().get()));
                return dt;
            });

            packages = packages.map( pk -> {
                pk.setDiagnosticTypeInLabs(Arrays.asList(pk.getDiagnosticTypeInLabs().stream().findFirst().get()));
                return pk;
            });
        }
        return new GlobalSearchResponse(doctors, tests, packages, specialities);
    }

    public Page<DiagnosticType> testSearch(String keyword, Boolean isCombo, String pinCode, String sortBy, String sortDirection, String auth, Pageable pageable) {
        return fetchTests(keyword, isCombo, null, null, pinCode, sortBy, sortDirection, auth, pageable);
    }

    public GlobalSearchServiceabilityResponse globalSearchServiceability(Double longitude, Double latitude, String pinCode, String type) throws BadParameterException {
        GlobalSearchServiceabilityResponse globalSearchServiceabilityResponse = new GlobalSearchServiceabilityResponse();

        if (type.toUpperCase().trim().equalsIgnoreCase("DOCTOR")) {
            globalSearchServiceabilityResponse.setDoctorAvailable(doctorServiceability(longitude, latitude));
        }else if (type.toUpperCase().trim().equalsIgnoreCase("TEST")) {
            globalSearchServiceabilityResponse.setTestAvailable(testServiceability(pinCode));
        } else {
            globalSearchServiceabilityResponse.setDoctorAvailable(doctorServiceability(longitude, latitude));
            globalSearchServiceabilityResponse.setTestAvailable(testServiceability(pinCode));
        }
        return globalSearchServiceabilityResponse;
    }

    public Boolean testServiceability(String pinCode) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(applyDiagnotisTypeFilters(null, null, pinCode))
                .build();
        List<DiagnosticType> diagnosticTypes = elasticsearchTemplate.queryForList(searchQuery, DiagnosticType.class);
        return !diagnosticTypes.isEmpty();
    }

    public Boolean doctorServiceability(Double longitude, Double latitude) throws BadParameterException {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(doctorSearchQueryBuilder(longitude, latitude))
                .build();
        List<Doctor> doctors = elasticsearchTemplate.queryForList(searchQuery, Doctor.class);
        return !doctors.isEmpty();
    }

    public BoolQueryBuilder doctorSearchQueryBuilder(Double longitude, Double latitude) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if (doctorSearchRadiusFilter != null && longitude != null && latitude != null) {
            qb.must(QueryBuilders
                    .geoDistanceQuery("doctorInClinics.clinic.location")
                    .point(latitude, longitude)
                    .distance(doctorSearchRadiusFilter, DistanceUnit.KILOMETERS));
        }
        return qb;
    }

    public Page<Speciality> globalSearchSpeciality(String keyword, String sortDirection, Pageable pageable) {
        SearchQuery searchQuery = null;
        if (StringUtils.isEmpty(keyword)) {
            searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(applySpecialityFiltersWoKeyWord())
                    .withSort(applySpecialitySorting("rank", sortDirection))
                    .withPageable(pageable)
                    .build();
        } else {
            searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(applySpecialityFilters(keyword))
                    .withSort(applySpecialitySorting("rank", sortDirection))
                    .withPageable(pageable)
                    .build();
        }
        log.info(" Elastic search query for speciality " + searchQuery.getQuery().toString());
        return elasticsearchTemplate.queryForPage(searchQuery, Speciality.class);
    }

    private QueryBuilder applySpecialityFiltersWoKeyWord() {
        return QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("isActive", true))
                .must(QueryBuilders.matchQuery("isPrimary", true));
    }

    private QueryBuilder applySpecialityFilters(String keyword) {
        BoolQueryBuilder searchQb = QueryBuilders.boolQuery();
        searchQb.should(QueryBuilders.matchQuery("name", keyword).operator(Operator.OR).boost(40f))
                .should(QueryBuilders.prefixQuery("name", keyword).boost(40f))
                .should(QueryBuilders.matchQuery("prefix", keyword)
                        .fuzziness(Fuzziness.AUTO).operator(Operator.OR).boost(15f))
                .should(QueryBuilders.matchQuery("name", keyword)
                        .fuzziness(Fuzziness.AUTO).operator(Operator.OR).boost(30f))
                .should(QueryBuilders.matchQuery("description", keyword)
                        .fuzziness(Fuzziness.AUTO).operator(Operator.OR).boost(10f));
        return QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("isActive", true))
                .must(QueryBuilders.matchQuery("isPrimary", true))
                .must(QueryBuilders.functionScoreQuery(searchQb));

    }

    private SortBuilder applySpecialitySorting(String sortBy, String sortDirection) {
        SortOrder sortOrder = SortOrder.valueOf(sortDirection);
        if ("rank".equals(sortBy.toLowerCase())) {
            return SortBuilders.fieldSort("rank").order(sortOrder);
        }
        return SortBuilders.fieldSort(sortBy).order(sortOrder);
    }
}

