package com.ayush.ravan.elsticsearch;
package co.arctern.api.emr.service.middlelayer;

import co.arctern.api.emr.domain.*;
import co.arctern.api.emr.domain.Advice;
import co.arctern.api.emr.domain.AllergyType;
import co.arctern.api.emr.domain.Clinic;
import co.arctern.api.emr.domain.ClinicalFindingType;
import co.arctern.api.emr.domain.DiagnosisType;
import co.arctern.api.emr.domain.DiagnosticTypeInLab;
import co.arctern.api.emr.domain.Medicine;
import co.arctern.api.emr.domain.MedicineInClinic;
import co.arctern.api.emr.domain.MedicineInventory;
import co.arctern.api.emr.domain.ProcedureType;
import co.arctern.api.emr.domain.SymptomType;
import co.arctern.api.emr.domain.dto.*;
import co.arctern.api.emr.domain.projection.*;
import co.arctern.api.emr.search.domain.*;
import co.arctern.api.emr.security.TokenDecoder;
import co.arctern.api.emr.service.api.*;
import com.google.common.primitives.Longs;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchV2Service {

    private final MedicineInClinicRepository medicineInClinicRepository;
    private final MedicineRepository medicineRepository;
    private final DiagnosticTypeInLabRepository diagnosticTypeInLabRepository;
    private final ProjectionFactory projectionFactory;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final PaginationService paginationService;
    private final ProcedureTypeRepository procedureTypeRepository;
    private final AdviceRepository adviceRepository;
    private final DiagnosisTypeRepository diagnosisTypeRepository;
    private final DiagnosticTypeRepository diagnosticTypeRepository;
    private final SymptomTypeRepository symptomTypeRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final DiagnosticRepository diagnosticRepository;
    private final SymptomRepository symptomRepository;
    private final AllergyRepository allergyRepository;
    private final AllergyTypeRepository allergyTypeRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final ProcedureRepository procedureRepository;
    private final ClinicRepository clinicRepository;
    private  final ClinicalFindingTypeRepository clinicalFindingTypeRepository;
    private final ClinicalFindingRepository clinicalFindingRepository;
    private final TokenDecoder tokenDecoder;

    @Autowired
    public SearchV2Service(MedicineInClinicRepository medicineInClinicRepository,
                           AllergyRepository allergyRepository,
                           AllergyTypeRepository allergyTypeRepository,
                           MedicineRepository medicineRepository,
                           DiagnosticTypeInLabRepository diagnosticTypeInLabRepository,
                           ProjectionFactory projectionFactory,
                           ElasticsearchTemplate elasticsearchTemplate,
                           PaginationService paginationService,
                           ProcedureTypeRepository procedureTypeRepository,
                           AdviceRepository adviceRepository,
                           DiagnosisTypeRepository diagnosisTypeRepository,
                           DiagnosticTypeRepository diagnosticTypeRepository, SymptomTypeRepository symptomTypeRepository,
                           PrescriptionRepository prescriptionRepository,
                           DiagnosticRepository diagnosticRepository,
                           SymptomRepository symptomRepository,
                           DiagnosisRepository diagnosisRepository,
                           ProcedureRepository procedureRepository,
                           ClinicRepository clinicRepository,
                           TokenDecoder tokenDecoder,ClinicalFindingTypeRepository clinicalFindingTypeRepository,
                           ClinicalFindingRepository clinicalFindingRepository) {
        this.allergyRepository =  allergyRepository;
        this.allergyTypeRepository =  allergyTypeRepository;
        this.medicineInClinicRepository = medicineInClinicRepository;
        this.medicineRepository = medicineRepository;
        this.diagnosticTypeInLabRepository = diagnosticTypeInLabRepository;
        this.projectionFactory = projectionFactory;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.paginationService = paginationService;
        this.procedureTypeRepository = procedureTypeRepository;
        this.adviceRepository = adviceRepository;
        this.diagnosisTypeRepository = diagnosisTypeRepository;
        this.diagnosticTypeRepository = diagnosticTypeRepository;
        this.symptomTypeRepository = symptomTypeRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.diagnosticRepository = diagnosticRepository;
        this.symptomRepository = symptomRepository;
        this.diagnosisRepository = diagnosisRepository;
        this.procedureRepository = procedureRepository;
        this.clinicRepository = clinicRepository;
        this.clinicalFindingTypeRepository = clinicalFindingTypeRepository;
        this.clinicalFindingRepository = clinicalFindingRepository;
        this.tokenDecoder = tokenDecoder;
    }

    public PaginatedDto searchMedicines(String value, Long clinicId, org.springframework.data.domain.Pageable pageable, String auth) {

        List<Long> medicineIds = getMedicinesIdsFromElasticSearch(value, getRecentlyPrescribedMedicines(auth));

        if(CollectionUtils.isEmpty(medicineIds)){
            return getPaginatedDto(pageable, new ArrayList<>());
        }

        List<MedicineSearchDto> medicineSearchDtoList = new ArrayList<>();
        for(Long i=0L; i<medicineIds.size(); i++) {
            medicineSearchDtoList.add(MedicineSearchDto.builder().index(i).medicineId(medicineIds.get(i.intValue())).build());
        }

        List<Medicine> medicinesList = getMedicines(clinicId, medicineIds);

        List<MedicineSearchInTranscription> collect1 = medicineSearchDtoList.stream()
                .peek(medicineSearchDto -> medicinesList.parallelStream()
                        .filter(medicine -> medicine.getId().equals(medicineSearchDto.getMedicineId()))
                        .findFirst().ifPresent(medicineSearchDto::setMedicine))
                .filter(medicineSearchDto -> medicineSearchDto.getMedicine() != null)
                .sorted(Comparator.comparing(MedicineSearchDto::getIndex))
                .sorted((Comparator.comparing(o -> o.getMedicine().getIndex())))
                .filter(DiagnosticRevampService.distinctByKey(medicineSearchDto -> medicineSearchDto.getMedicine().getId()))
                .map(a -> projectionFactory.createProjection(MedicineSearchInTranscription.class, a.getMedicine()))
                .collect(Collectors.toList());

        return getPaginatedDto(pageable, collect1);
    }

    public PaginatedDto searchMedicines(String value, org.springframework.data.domain.Pageable pageable, String auth) {
        Optional<Clinic> clinic = clinicRepository.findDistinctByDoctorInClinicsIdIn(ArrayUtils.toObject(Longs.toArray(tokenDecoder.getDoctorInClinic()))).stream().findFirst();
        if(!clinic.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Clinic ID not found");
        }
        return searchMedicines(value, clinic.get().getId(), pageable, auth);
    }

    public List<Long> getRecentlyPrescribedMedicines(String auth) {
        List<Long> recentlyPrescribedMedicines = new ArrayList<>();

        if(auth != null) {
            recentlyPrescribedMedicines = prescriptionRepository.findByDoctorInClinicIn(Longs.toArray(tokenDecoder.getDoctorInClinic()), new java.sql.Date(Instant.now().minus(30, ChronoUnit.DAYS).toEpochMilli()), new Date(Instant.now().toEpochMilli()))
                    .stream()
                    .map(prescription -> (prescription.getMedicineInClinic()!=null) ? prescription.getMedicineInClinic().getMedicine().getId() : prescription.getMedicine().getId())
                    .collect(Collectors.toList());
        }

        return recentlyPrescribedMedicines;
    }

    private List<Medicine> getMedicines(Long clinicId, List<Long> medicineIds) {
        List<Medicine> medicinesList = new ArrayList<>();
        List<MedicineInClinic> micS = medicineInClinicRepository.findByMedicineIdInAndClinicIdAndIsActiveTrue(medicineIds, clinicId);
        for (MedicineInClinic mic : micS) {
            Set<MedicineInventory> medicineInventories = mic.getMedicineInventories();
            Medicine medicine = mic.getMedicine();
            if (medicine.getIsDeliverable() == null || !medicine.getIsDeliverable() || medicine.getPrice() == null || medicine.getDisplayPrice() == null)
                continue;
            if (medicineInventories != null && !medicineInventories.isEmpty()) {
                Set<MedicineInventory> collect = medicineInventories.stream().filter(a -> a.getQuantity() > 0).collect(Collectors.toSet());
                if (!CollectionUtils.isEmpty(collect)) {
                    //medicine-inventories exist with quantity > 0
                    medicine.setIsPresent(true);
                    medicine.setIndex(1);
                    mic.setMedicineInventories(collect);
                    medicineIds.add(medicine.getId());
                } else {
                    //quantity in medicine-inventories is zero
                    medicine.setIsPresent(false);
                    medicine.setIndex(2);
                    mic.setMedicineInventories(collect);
                }
            } else {
                //medicine-inventories are null or empty
                medicine.setIsPresent(false);
                medicine.setIndex(3);
            }
            medicine.setName(medicine.getName());
            medicine.setDisplayPrice(medicine.getDisplayPrice());
            medicine.setIsDeliverable(medicine.getIsDeliverable());
            medicine.setPrice(medicine.getPrice());
            medicine.setMedicineInClinic(mic);
            medicinesList.add(medicine);
        }

        List<Medicine> medicines = (medicineIds != null && !medicineIds.isEmpty()) ? medicineRepository.findByIdIn(medicineIds) : medicineRepository.findByIdInAndIsActiveTrue(medicineIds);
        medicines.removeAll(micS.stream().map(MedicineInClinic::getMedicine).collect(Collectors.toList()));
        for (Medicine medicine : medicines) {
            if (medicine.getIsDeliverable() == null || !medicine.getIsDeliverable() || medicine.getPrice() == null || medicine.getDisplayPrice() == null)
                continue;
            // medicines without micS
            medicine.setIndex(4);
            medicine.setIsPresent(false);
            medicine.setName(medicine.getName());
            medicine.setDisplayPrice(medicine.getDisplayPrice());
            medicine.setIsDeliverable(medicine.getIsDeliverable());
            medicine.setPrice(medicine.getPrice());
            medicinesList.add(medicine);
        }
        return medicinesList;
    }

    public List<Long> getMedicinesIdsFromElasticSearch(String value, List<Long> recentlyPrescribledMedicines) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery()
                .should(QueryBuilders.prefixQuery("medicine.name", value).boost(10f))
                .should(QueryBuilders.matchPhrasePrefixQuery("medicine.name", value).slop(5).boost(5f));

        if(value.length() >= 5){
            qb.should(QueryBuilders.matchQuery("medicine.name", value).operator(Operator.AND).fuzziness(Fuzziness.AUTO).boost(2f));
        }

        List<FunctionScoreQueryBuilder.FilterFunctionBuilder> functionBuilderList = new ArrayList<>();
        functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(getStartsWithScript(value, "medicine.name"))));
        if(!CollectionUtils.isEmpty(recentlyPrescribledMedicines)){
            functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(getRecentlyPrescribedScript(recentlyPrescribledMedicines))));
        }

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.functionScoreQuery(qb, functionBuilderList.toArray(new FunctionScoreQueryBuilder.FilterFunctionBuilder[0]))).build();
        if(elasticsearchTemplate.count(nativeSearchQuery, ESSearchMedicine.class) > 1000){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too many records. Please be more specific in search");
        }

        List<Long> medicineIds = new ArrayList<>();

        elasticsearchTemplate.stream(nativeSearchQuery, ESSearchMedicine.class).forEachRemaining(esSearchMedicine -> {
            if(medicineIds.contains(esSearchMedicine.getId())){
                return;
            }
            medicineIds.add(esSearchMedicine.getId());
        });

        return medicineIds;
    }

    public PaginatedDto searchMedicines(String value, Pageable pageable) {

        List<Long> medicineIds = getMedicinesIdsFromElasticSearch(value);

        if(CollectionUtils.isEmpty(medicineIds)){
            return getPaginatedDto(pageable, new ArrayList<>());
        }

        List<MedicineSearchDto> medicineSearchDtoList = new ArrayList<>();
        for(Long i=0L; i<medicineIds.size(); i++) {
            medicineSearchDtoList.add(MedicineSearchDto.builder().index(i).medicineId(medicineIds.get(i.intValue())).build());
        }

        List<MedicineForTranscription> medicinesList = medicineRepository.findByIdInAndIsActiveTrueAndIdIsNotNull(medicineIds);

        List<MedicineForTranscription> collect = medicineSearchDtoList.stream()
                .peek(medicineSearchDto -> medicinesList.parallelStream()
                        .filter(medicine -> medicine.getId().equals(medicineSearchDto.getMedicineId()))
                        .findFirst().ifPresent(medicineSearchDto::setMedicineProjection))
                .filter(medicineSearchDto -> medicineSearchDto.getMedicineProjection() != null)
                .sorted(Comparator.comparing(MedicineSearchDto::getIndex))
                .filter(DiagnosticRevampService.distinctByKey(medicineSearchDto -> medicineSearchDto.getMedicineProjection().getId()))
                .map(MedicineSearchDto::getMedicineProjection)
                .collect(Collectors.toList());

        return getPaginatedDto(pageable, collect);
    }

    public List<Long> getMedicinesIdsFromElasticSearch(String value) {
        return getMedicinesIdsFromElasticSearch(value, new ArrayList<>());
    }

    public PaginatedDto searchDiagnosticTypes(String value, List<Long> labIds, Pageable pageable, String auth) {
        List<Long> diagnosticTypeIds = getDiagnosticTypeIdsFromElasticSearch(value, getRecentlyPrescribedDiagnosticTypes(auth));

        List<DiagnosticTypeSearchDto> diagnosticTypeSearchDtoList = new ArrayList<>();
        for(Long i=0L; i<diagnosticTypeIds.size(); i++){
            diagnosticTypeSearchDtoList.add(DiagnosticTypeSearchDto.builder().index(i).diagnosticTypeId(diagnosticTypeIds.get(i.intValue())).build());
        }

        List<DiagnosticTypeInLab> initialCollect = diagnosticTypeInLabRepository.fetchDtilForSearchV2(diagnosticTypeIds, labIds);

        List<DiagnosticTypes> collect = diagnosticTypeSearchDtoList.stream()
                .peek(diagnosticTypeSearchDto -> initialCollect.parallelStream()
                        .filter(diagnosticTypeInLab -> diagnosticTypeInLab.getDiagnosticType().getId().equals(diagnosticTypeSearchDto.getDiagnosticTypeId()))
                        .findFirst().ifPresent(diagnosticTypeInLab1 -> diagnosticTypeSearchDto.setDiagnosticType(diagnosticTypeInLab1.getDiagnosticType())))
                .filter(diagnosticTypeSearchDto -> diagnosticTypeSearchDto.getDiagnosticType() != null)
                .sorted(Comparator.comparing(DiagnosticTypeSearchDto::getIndex))
                .map(diagnosticTypeSearchDto -> projectionFactory.createProjection(DiagnosticTypes.class, diagnosticTypeSearchDto.getDiagnosticType()))
                .collect(Collectors.toList());

        return getPaginatedDto(pageable, collect);
    }

    public List<Long> getRecentlyPrescribedDiagnosticTypes(String auth) {
        List<Long> recentlyPrescribedDiagnosticTypes = new ArrayList<>();

        if(auth != null) {
            recentlyPrescribedDiagnosticTypes = diagnosticRepository.findByDoctorInClinicIn(Longs.toArray(tokenDecoder.getDoctorInClinic()), new java.sql.Date(Instant.now().minus(30, ChronoUnit.DAYS).toEpochMilli()), new Date(Instant.now().toEpochMilli()))
                    .stream()
                    .filter(diagnostic -> diagnostic.getDiagnosticType() != null)
                    .map(diagnostic -> diagnostic.getDiagnosticType().getId())
                    .collect(Collectors.toList());
        }

        return recentlyPrescribedDiagnosticTypes;
    }

    public List<Long> getDiagnosticTypeIdsFromElasticSearch(String value, List<Long> recentlyPrescribedDiagnosticTypes) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery().should(QueryBuilders.matchPhrasePrefixQuery("diagnosticType.name", value).boost(10f))
                .should(QueryBuilders.boolQuery().should(
                        QueryBuilders
                                .multiMatchQuery(value,
                                        "diagnosticType.name",
                                        "diagnosticType.abbreviation",
                                        "diagnosticType.alias").boost(5f)));

        List<FunctionScoreQueryBuilder.FilterFunctionBuilder> functionBuilderList = new ArrayList<>();
        functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(getStartsWithScript(value, "diagnosticType.name"))));
        if(!CollectionUtils.isEmpty(recentlyPrescribedDiagnosticTypes)){
            functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(getRecentlyPrescribedScript(recentlyPrescribedDiagnosticTypes))));
        }

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.functionScoreQuery(qb, functionBuilderList.toArray(new FunctionScoreQueryBuilder.FilterFunctionBuilder[0]))).build();
        if(elasticsearchTemplate.count(nativeSearchQuery, ESSearchDiagnosticType.class) > 1000){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too many records. Please be more specific in search");
        }

        List<Long> diagnosticTypeIds = new ArrayList<>();

        elasticsearchTemplate.stream(nativeSearchQuery, ESSearchDiagnosticType.class).forEachRemaining(esSearchDiagnosticType -> {
            if(diagnosticTypeIds.contains(esSearchDiagnosticType.getDiagnosticType().getId())){
                return;
            }
            diagnosticTypeIds.add(esSearchDiagnosticType.getId());
        });

        return diagnosticTypeIds;
    }

    public PaginatedDto searchDiagnosticTypes(String value, Pageable pageable) {
        List<Long> diagnosticTypeIds = getDiagnosticTypeIdsFromElasticSearch(value);

        List<DiagnosticTypeSearchDto> diagnosticTypeSearchDtoList = new ArrayList<>();
        for(Long i=0L; i<diagnosticTypeIds.size(); i++){
            diagnosticTypeSearchDtoList.add(DiagnosticTypeSearchDto.builder().index(i).diagnosticTypeId(diagnosticTypeIds.get(i.intValue())).build());
        }

        List<DiagnosticTypesForTranscription> initialCollect = diagnosticTypeRepository.findByIdInAndIsActiveTrue(diagnosticTypeIds);

        List<DiagnosticTypesForTranscription> collect = diagnosticTypeSearchDtoList.stream()
                .peek(diagnosticTypeSearchDto -> initialCollect.parallelStream()
                        .filter(diagnosticType -> diagnosticType.getId().equals(diagnosticTypeSearchDto.getDiagnosticTypeId()))
                        .findFirst().ifPresent(diagnosticTypeSearchDto::setDiagnosticTypeProjection))
                .filter(diagnosticTypeSearchDto -> diagnosticTypeSearchDto.getDiagnosticTypeProjection() != null)
                .sorted(Comparator.comparing(DiagnosticTypeSearchDto::getIndex))
                .map(DiagnosticTypeSearchDto::getDiagnosticTypeProjection)
                .collect(Collectors.toList());

        return getPaginatedDto(pageable, collect);
    }

    public List<Long> getDiagnosticTypeIdsFromElasticSearch(String value) {
        return getDiagnosticTypeIdsFromElasticSearch(value, new ArrayList<>());
    }

    public PaginatedDto searchProcedureType(String value, Pageable pageable, String auth){
        List<Long> procedureTypeIds = getProcedureTypeIdsFromElasticSearch(value, getRecentlyPrescribedProcedures(auth));

        List<ProcedureTypeSearchDto> procedureTypeSearchDtoList = new ArrayList<>();
        for(Long i=0L; i<procedureTypeIds.size(); i++){
            procedureTypeSearchDtoList.add(ProcedureTypeSearchDto.builder().index(i).procedureTypeId(procedureTypeIds.get(i.intValue())).build());
        }

        List<ProcedureType> procedureTypes = procedureTypeRepository.findAllByIdIn(procedureTypeIds);

        List<ProcedureTypes> collect = procedureTypeSearchDtoList.stream()
                .peek(procedureTypeSearchDto -> procedureTypes.parallelStream()
                        .filter(procedureType -> procedureType.getId().equals(procedureTypeSearchDto.getProcedureTypeId()))
                        .findFirst().ifPresent(procedureTypeSearchDto::setProcedureType))
                .filter(procedureTypeSearchDto -> procedureTypeSearchDto.getProcedureType() != null)
                .sorted(Comparator.comparing(ProcedureTypeSearchDto::getIndex))
                .map(procedureTypeSearchDto -> projectionFactory.createProjection(ProcedureTypes.class, procedureTypeSearchDto.getProcedureType()))
                .collect(Collectors.toList());
        return getPaginatedDto(pageable, collect);
    }

    private List<Long> getRecentlyPrescribedProcedures(String auth) {
        List<Long> recentlyPrescribedProcedures = new ArrayList<>();

        if(auth != null) {
            recentlyPrescribedProcedures = procedureRepository.findByDoctorInClinicIn(Longs.toArray(tokenDecoder.getDoctorInClinic()), new java.sql.Date(Instant.now().minus(30, ChronoUnit.DAYS).toEpochMilli()), new Date(Instant.now().toEpochMilli()))
                    .stream()
                    .filter(medicalProcedure -> medicalProcedure.getProcedureType() != null)
                    .map(medicalProcedure -> medicalProcedure.getProcedureType().getId())
                    .collect(Collectors.toList());
        }

        return recentlyPrescribedProcedures;
    }

    private List<Long> getProcedureTypeIdsFromElasticSearch(String value, List<Long> recentlyPrescribedProcedures) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery().should(QueryBuilders.prefixQuery("procedureType.name", value).boost(10f))
                .must(QueryBuilders.matchPhrasePrefixQuery("procedureType.name", value).slop(5).boost(1f));

        List<FunctionScoreQueryBuilder.FilterFunctionBuilder> functionBuilderList = new ArrayList<>();
        functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(getStartsWithScript(value, "procedureType.name"))));
        if(!CollectionUtils.isEmpty(recentlyPrescribedProcedures)){
            functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(getRecentlyPrescribedScript(recentlyPrescribedProcedures))));
        }

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.functionScoreQuery(qb, functionBuilderList.toArray(new FunctionScoreQueryBuilder.FilterFunctionBuilder[0]))).build();
        if(elasticsearchTemplate.count(nativeSearchQuery, ESSearchProcedureType.class) > 1000){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too many records. Please be more specific in search");
        }

        List<Long> procedureTypeIds = new ArrayList<>();

        elasticsearchTemplate.stream(nativeSearchQuery, ESSearchProcedureType.class).forEachRemaining(esSearchProcedureType -> {
            if(procedureTypeIds.contains(esSearchProcedureType.getProcedureType().getId())){
                return;
            }
            procedureTypeIds.add(esSearchProcedureType.getId());
        });

        return procedureTypeIds;
    }

    public PaginatedDto searchAdvice(String value, Pageable pageable, String auth){
        List<Long> ids = getAdviceIdsFromElasticSearch(value, getRecentlyPrescribedAdvices(auth));

        List<AdviceSearchDto> adviceSearchDtos = new ArrayList<>();
        for(Long i=0L; i<ids.size(); i++){
            adviceSearchDtos.add(AdviceSearchDto.builder().index(i).adviceId(ids.get(i.intValue())).build());
        }

        List<Advice> adviceList = adviceRepository.findAllByIdIn(ids);

        List<Advices> collect = adviceSearchDtos.stream()
                .peek(adviceSearchDto -> adviceList.parallelStream()
                        .filter(advice -> advice.getId().equals(adviceSearchDto.getAdviceId()))
                        .findFirst().ifPresent(adviceSearchDto::setAdvice))
                .filter(adviceSearchDto -> adviceSearchDto.getAdvice() != null)
                .sorted(Comparator.comparing(AdviceSearchDto::getIndex))
                .map(adviceSearchDto -> projectionFactory.createProjection(Advices.class, adviceSearchDto.getAdvice()))
                .collect(Collectors.toList());

        return getPaginatedDto(pageable, collect);
    }

    private List<Long> getRecentlyPrescribedAdvices(String auth) {
        List<Long> recentlyPrescribedAdvices = new ArrayList<>();

        if(auth != null) {
            recentlyPrescribedAdvices = adviceRepository.findByDoctorInClinicIn(Longs.toArray(tokenDecoder.getDoctorInClinic()), new java.sql.Date(Instant.now().minus(30, ChronoUnit.DAYS).toEpochMilli()), new Date(Instant.now().toEpochMilli()))
                    .stream()
                    .map(Advice::getId)
                    .collect(Collectors.toList());
        }

        return recentlyPrescribedAdvices;
    }

    private List<Long> getAdviceIdsFromElasticSearch(String value, List<Long> recentlyPrescribedAdvices) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery().should(QueryBuilders.prefixQuery("advice.name", value).boost(10f))
                .must(QueryBuilders.matchPhrasePrefixQuery("advice.name", value).slop(5).boost(1f));

        List<FunctionScoreQueryBuilder.FilterFunctionBuilder> functionBuilderList = new ArrayList<>();
        functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(getStartsWithScript(value, "advice.name"))));
        if(!CollectionUtils.isEmpty(recentlyPrescribedAdvices)){
            functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(getRecentlyPrescribedScript(recentlyPrescribedAdvices))));
        }

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.functionScoreQuery(qb, functionBuilderList.toArray(new FunctionScoreQueryBuilder.FilterFunctionBuilder[0]))).build();
        if(elasticsearchTemplate.count(nativeSearchQuery, ESSearchAdvice.class) > 1000){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too many records. Please be more specific in search");
        }

        List<Long> adviceIds = new ArrayList<>();

        elasticsearchTemplate.stream(nativeSearchQuery, ESSearchAdvice.class).forEachRemaining(esSearchAdvice -> {
            if(adviceIds.contains(esSearchAdvice.getAdvice().getId())){
                return;
            }
            adviceIds.add(esSearchAdvice.getId());
        });

        return adviceIds;
    }

    public PaginatedDto searchDiagnosisType(String value, Pageable pageable, String auth){
        List<Long> diagnosisTypeIds = getDiagnosisTypeIdsFromElasticSearch(value, getRecentlyPrescribedDiagnosisTypes(auth));

        List<DiagnosisTypeSearchDto> diagnosisTypeSearchDtoList = new ArrayList<>();
        for(Long i=0L; i<diagnosisTypeIds.size(); i++){
            diagnosisTypeSearchDtoList.add(DiagnosisTypeSearchDto.builder().index(i).diagnosisTypeId(diagnosisTypeIds.get(i.intValue())).build());
        }

        List<DiagnosisType> diagnosisTypes = diagnosisTypeRepository.findAllByIdIn(diagnosisTypeIds);

        List<DiagnosisTypes> collect = diagnosisTypeSearchDtoList.stream()
                .peek(diagnosisTypeSearchDto -> diagnosisTypes.parallelStream()
                        .filter(diagnosisType -> diagnosisType.getId().equals(diagnosisTypeSearchDto.getDiagnosisTypeId()))
                        .findFirst().ifPresent(diagnosisTypeSearchDto::setDiagnosisType))
                .filter(diagnosisTypeSearchDto -> diagnosisTypeSearchDto.getDiagnosisType() != null)
                .sorted(Comparator.comparing(DiagnosisTypeSearchDto::getIndex))
                .map(diagnosisTypeSearchDto -> projectionFactory.createProjection(DiagnosisTypes.class, diagnosisTypeSearchDto.getDiagnosisType()))
                .collect(Collectors.toList());

        return getPaginatedDto(pageable, collect);
    }

    private List<Long> getRecentlyPrescribedDiagnosisTypes(String auth) {
        List<Long> recentlyPrescribedDiagnsosis = new ArrayList<>();

        if(auth != null) {
            recentlyPrescribedDiagnsosis = diagnosisRepository.findByDoctorInClinicIn(Longs.toArray(tokenDecoder.getDoctorInClinic()), new java.sql.Date(Instant.now().minus(30, ChronoUnit.DAYS).toEpochMilli()), new Date(Instant.now().toEpochMilli()))
                    .stream()
                    .filter(diagnosis -> diagnosis.getDiagnosisType() != null)
                    .map(diagnosis -> diagnosis.getDiagnosisType().getId())
                    .collect(Collectors.toList());
        }

        return recentlyPrescribedDiagnsosis;
    }

    private List<Long> getDiagnosisTypeIdsFromElasticSearch(String value, List<Long> recentlyPrescribedDiagnosisTypes) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery().should(QueryBuilders.prefixQuery("diagnosisType.name", value).boost(10f))
                .must(QueryBuilders.matchPhrasePrefixQuery("diagnosisType.name", value).slop(5).boost(1f));

        List<FunctionScoreQueryBuilder.FilterFunctionBuilder> functionBuilderList = new ArrayList<>();
        functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(getStartsWithScript(value, "diagnosisType.name"))));
        if(!CollectionUtils.isEmpty(recentlyPrescribedDiagnosisTypes)){
            functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(getRecentlyPrescribedScript(recentlyPrescribedDiagnosisTypes))));
        }

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.functionScoreQuery(qb, functionBuilderList.toArray(new FunctionScoreQueryBuilder.FilterFunctionBuilder[0]))).build();
        if(elasticsearchTemplate.count(nativeSearchQuery, ESSearchDiagnosisType.class) > 1000){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too many records. Please be more specific in search");
        }

        List<Long> diagnosisTypeIds = new ArrayList<>();

        elasticsearchTemplate.stream(nativeSearchQuery, ESSearchDiagnosisType.class).forEachRemaining(esSearchDiagnosisType -> {
            if(diagnosisTypeIds.contains(esSearchDiagnosisType.getDiagnosisType().getId())){
                return;
            }
            diagnosisTypeIds.add(esSearchDiagnosisType.getId());
        });

        return diagnosisTypeIds;
    }

    public PaginatedDto searchClinicalFindingType(String value, Pageable pageable, String auth){
        List<Long> ids = getClinicalFindingTypeIdsFromElasticSearch(value, getRecentlyPrescribedClinicalFindingTypes(auth));

        List<ClinicalFindingTypeSearchDto> clinicalFindingTypeSearchDtos = new ArrayList<>();
        for(Long i=0L; i<ids.size(); i++){
            clinicalFindingTypeSearchDtos.add(ClinicalFindingTypeSearchDto.builder().index(i).clinicalFindingTypeId(ids.get(i.intValue())).build());
        }

        List<ClinicalFindingType> clinicalFindingTypes = clinicalFindingTypeRepository.findAllByIdIn(ids);

        List<ClinicalFindingTypes> clinicalFindingTypeList = clinicalFindingTypeSearchDtos.stream()
                .peek(clinicalFindingTypeSearchDto -> clinicalFindingTypes.parallelStream()
                        .filter(clinicalFindingType -> clinicalFindingType.getId().equals(clinicalFindingTypeSearchDto.getClinicalFindingTypeId()))
                        .findFirst().ifPresent(clinicalFindingTypeSearchDto::setClinicalFindingType))
                .filter(clinicalFindingTypeSearchDto -> clinicalFindingTypeSearchDto.getClinicalFindingType() != null)
                .sorted(Comparator.comparing(ClinicalFindingTypeSearchDto::getIndex))
                .map(clinicalFindingTypeSearchDto -> projectionFactory.createProjection(ClinicalFindingTypes.class, clinicalFindingTypeSearchDto.getClinicalFindingType()))
                .collect(Collectors.toList());

        return getPaginatedDto(pageable, clinicalFindingTypeList);
    }

    private List<Long> getRecentlyPrescribedClinicalFindingTypes(String auth) {
        List<Long> recentlyPrescribedClinicalFindings = new ArrayList<>();

        if(auth != null) {
            long[] dicIds = Longs.toArray(tokenDecoder.getDoctorInClinic());
            recentlyPrescribedClinicalFindings = clinicalFindingRepository.findByDoctorInClinicIn(dicIds, new Date(Instant.now().minus(30, ChronoUnit.DAYS).toEpochMilli()), new Date(Instant.now().toEpochMilli()))
                    .stream()
                    .filter(clinicalFinding -> clinicalFinding.getClinicalFindingType() != null)
                    .map(clinicalFinding -> clinicalFinding.getClinicalFindingType().getId())
                    .collect(Collectors.toList());
        }

        return recentlyPrescribedClinicalFindings;
    }
    private List<Long> getClinicalFindingTypeIdsFromElasticSearch(String value, List<Long> recentlyPrescribedClinicalFindingTypes) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery().should(QueryBuilders.prefixQuery("clinicalFindingType.name", value).boost(10f))
                .must(QueryBuilders.matchPhrasePrefixQuery("clinicalFindingType.name", value).slop(5).boost(1f));

        List<FunctionScoreQueryBuilder.FilterFunctionBuilder> functionBuilderList = new ArrayList<>();
        functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(getStartsWithScript(value, "clinicalFindingType.name"))));
        if(!CollectionUtils.isEmpty(recentlyPrescribedClinicalFindingTypes)){
            functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(getRecentlyPrescribedScript(recentlyPrescribedClinicalFindingTypes))));
        }

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.functionScoreQuery(qb, functionBuilderList.toArray(new FunctionScoreQueryBuilder.FilterFunctionBuilder[0]))).build();
        if(elasticsearchTemplate.count(nativeSearchQuery, ESSearchClinicalFindingType.class) > 1000){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too many records. Please be more specific in search");
        }

        List<Long> clinicalFindingTypeIds = new ArrayList<>();

        elasticsearchTemplate.stream(nativeSearchQuery, ESSearchClinicalFindingType.class).forEachRemaining(esSearchClinicalFindingType -> {
            if(!clinicalFindingTypeIds.contains(esSearchClinicalFindingType.getClinicalFindingType().getId())){
                clinicalFindingTypeIds.add(esSearchClinicalFindingType.getClinicalFindingType().getId());
//                return;
            }
//            clinicalFindingTypeIds.add(esSearchClinicalFindingType.getId());
        });

        return clinicalFindingTypeIds;
    }

    public PaginatedDto searchSymptomType(String value, Pageable pageable, String auth){
        List<Long> ids = getSymptomTypeIdsFromElasticSearch(value, getRecentlyPrescribedSymptomTypes(auth));

        List<SymptomTypeSearchDto> symptomTypeSearchDtos = new ArrayList<>();
        for(Long i=0L; i<ids.size(); i++){
            symptomTypeSearchDtos.add(SymptomTypeSearchDto.builder().index(i).symptomTypeId(ids.get(i.intValue())).build());
        }

        List<SymptomType> symptomTypes = symptomTypeRepository.findAllByIdIn(ids);

        List<SymptomTypes> symptomTypeList = symptomTypeSearchDtos.stream()
                .peek(symptomTypeSearchDto -> symptomTypes.parallelStream()
                        .filter(symptomType -> symptomType.getId().equals(symptomTypeSearchDto.getSymptomTypeId()))
                        .findFirst().ifPresent(symptomTypeSearchDto::setSymptomType))
                .filter(symptomTypeSearchDto -> symptomTypeSearchDto.getSymptomType() != null)
                .sorted(Comparator.comparing(SymptomTypeSearchDto::getIndex))
                .map(symptomTypeSearchDto -> projectionFactory.createProjection(SymptomTypes.class, symptomTypeSearchDto.getSymptomType()))
                .collect(Collectors.toList());

        return getPaginatedDto(pageable, symptomTypeList);
    }

    private List<Long> getRecentlyPrescribedSymptomTypes(String auth) {
        List<Long> recentlyPrescribedSymptoms = new ArrayList<>();

        if(auth != null) {
            long[] dicIds = Longs.toArray(tokenDecoder.getDoctorInClinic());
            recentlyPrescribedSymptoms = symptomRepository.findByDoctorInClinicIn(dicIds, new Date(Instant.now().minus(30, ChronoUnit.DAYS).toEpochMilli()), new Date(Instant.now().toEpochMilli()))
                    .stream()
                    .filter(symptom -> symptom.getSymptomType() != null)
                    .map(symptom -> symptom.getSymptomType().getId())
                    .collect(Collectors.toList());
        }

        return recentlyPrescribedSymptoms;
    }

    private List<Long> getSymptomTypeIdsFromElasticSearch(String value, List<Long> recentlyPrescribedSymptomTypes) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery().should(QueryBuilders.prefixQuery("symptomType.name", value).boost(10f))
                .must(QueryBuilders.matchPhrasePrefixQuery("symptomType.name", value).slop(5).boost(1f));

        List<FunctionScoreQueryBuilder.FilterFunctionBuilder> functionBuilderList = new ArrayList<>();
        functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(getStartsWithScript(value, "symptomType.name"))));
        if(!CollectionUtils.isEmpty(recentlyPrescribedSymptomTypes)){
            functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(getRecentlyPrescribedScript(recentlyPrescribedSymptomTypes))));
        }

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.functionScoreQuery(qb, functionBuilderList.toArray(new FunctionScoreQueryBuilder.FilterFunctionBuilder[0]))).build();
        if(elasticsearchTemplate.count(nativeSearchQuery, ESSearchSymptomType.class) > 1000){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too many records. Please be more specific in search");
        }

        List<Long> symptomTypeIds = new ArrayList<>();

        elasticsearchTemplate.stream(nativeSearchQuery, ESSearchSymptomType.class).forEachRemaining(esSearchSymptomType -> {
            if(!symptomTypeIds.contains(esSearchSymptomType.getSymptomType().getId())){
                symptomTypeIds.add(esSearchSymptomType.getSymptomType().getId());
//                return;
            }
//            symptomTypeIds.add(esSearchSymptomType.getId());
        });

        return symptomTypeIds;
    }

    public PaginatedDto searchSymptomType(String value, Pageable pageable){
        List<Long> ids = getSymptomTypeIdsFromElasticSearch(value);

        List<SymptomTypeSearchDto> symptomTypeSearchDtos = new ArrayList<>();
        for(Long i=0L; i<ids.size(); i++){
            symptomTypeSearchDtos.add(SymptomTypeSearchDto.builder().index(i).symptomTypeId(ids.get(i.intValue())).build());
        }

        List<SymptomTypes> symptomTypes = symptomTypeRepository.findAllByIdInAndIdIsNotNull(ids);

        List<SymptomTypes> symptomTypeList = symptomTypeSearchDtos.stream()
                .peek(symptomTypeSearchDto -> symptomTypes.parallelStream()
                        .filter(symptomType -> symptomType.getId().equals(symptomTypeSearchDto.getSymptomTypeId()))
                        .findFirst().ifPresent(symptomTypeSearchDto::setSymptomTypeProjection))
                .filter(symptomTypeSearchDto -> symptomTypeSearchDto.getSymptomTypeProjection() != null)
                .sorted(Comparator.comparing(SymptomTypeSearchDto::getIndex))
                .map(SymptomTypeSearchDto::getSymptomTypeProjection)
                .collect(Collectors.toList());

        return getPaginatedDto(pageable, symptomTypeList);
    }

    private List<Long> getSymptomTypeIdsFromElasticSearch(String value) {
        return getSymptomTypeIdsFromElasticSearch(value, null);
    }

    private PageDto getPageDto(Pageable pageable, int pageSize, int totalElements) {
        PageDto pageDto = new PageDto();
        pageDto.setTotalPages((int) Math.ceil((double) totalElements / pageSize));
        pageDto.setSize(pageSize);
        pageDto.setPage(pageable.getPageNumber());
        pageDto.setTotalElements((long) totalElements);
        return pageDto;
    }

    private PaginatedDto getPaginatedDto(Pageable pageable, List<?> collect) {
        PaginatedDto dto = new PaginatedDto();
        int totalElements = collect.size();
        int pageSize = pageable.getPageSize();
        dto.setContent(paginationService.getDefaultPage(collect,
                pageable.getPageNumber(), pageSize));
        PageDto pageDto = getPageDto(pageable, pageSize, totalElements);
        dto.setPage(pageDto);
        return dto;
    }

    private Map<String, Object> getScriptParamsForSearch(String value) {
        String trimmedValue = value.trim();
        Map<String, Object> map = new HashMap<>();
        map.put("searchQuery", trimmedValue);
        map.put("length", trimmedValue.length());
        return map;
    }

    public Script getStartsWithScript(String value, String field) {
        return new Script(ScriptType.INLINE, "painless", "def str = doc['" + field + ".keyword'].value.trim(); (str.length() >= params.length) " +
                "? str.substring(0, params.length).equalsIgnoreCase(params.searchQuery) ? str.substring(0, str.length()).equalsIgnoreCase(params.searchQuery) ? 15 : 10 : 1 : 1",
                getScriptParamsForSearch(value));
    }

    public Script getRecentlyPrescribedScript(List<Long> recentlyPrescribedTypes) {
        return new Script(
                ScriptType.INLINE,
                "painless",
                "for(entry in params.typeCount.entrySet()){ if(doc['id'].value.toString().equals(entry.getKey())) { return entry.getValue()+1; } } return 1;",
                Collections.singletonMap(
                        "typeCount",
                        recentlyPrescribedTypes.stream()
                                .collect(Collectors.groupingBy(Object::toString, Collectors.counting()))
                )
        );
    }

    public List<ESBarcodeDetailsType> searchBarcodeBatchDetailsByBarcode(String barcode) {
        List<ESBarcodeDetailsType> searchBarcodeBatchDetailsDtoList = new ArrayList<>();
        BoolQueryBuilder qb = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("bar_code", barcode));

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.functionScoreQuery(qb)).build();
        if (elasticsearchTemplate.count(nativeSearchQuery, ESBarcodeDetailsType.class) > 1000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too many records. Please be more specific in search");
        }
        elasticsearchTemplate.stream(nativeSearchQuery, ESBarcodeDetailsType.class).forEachRemaining(searchBarcodeBatchDetailsDtoList::add);
        return searchBarcodeBatchDetailsDtoList;
    }

    public PaginatedDto searchAllergyType(String value, Pageable pageable, String auth){
        List<Long> ids = getAllergyTypeIdsFromElasticSearch(value, getRecentlyPrescribedAllergyTypes(auth));

        List<AllergyTypeSearchDto> allergyTypeSearchDtos = new ArrayList<>();
        for(Long i=0L; i<ids.size(); i++){
            allergyTypeSearchDtos.add(AllergyTypeSearchDto.builder().index(i).allergyTypeId(ids.get(i.intValue())).build());
        }

        List<AllergyType> allergyTypes = allergyTypeRepository.findAllByIdIn(ids);

        List<AllergyTypes> allergyTypeList = allergyTypeSearchDtos.stream()
                .peek(allergyTypeSearchDto -> allergyTypes.parallelStream()
                        .filter(allergyType -> allergyType.getId().equals(allergyTypeSearchDto.getAllergyTypeId()))
                        .findFirst().ifPresent(allergyTypeSearchDto::setAllergyType))
                .filter(allergyTypeSearchDto -> allergyTypeSearchDto.getAllergyType() != null)
                .sorted(Comparator.comparing(AllergyTypeSearchDto::getIndex))
                .map(allergyTypeSearchDto -> projectionFactory.createProjection(AllergyTypes.class, allergyTypeSearchDto.getAllergyType()))
                .collect(Collectors.toList());

        return getPaginatedDto(pageable, allergyTypeList);
    }

    private List<Long> getRecentlyPrescribedAllergyTypes(String auth) {
        List<Long> recentlyPrescribedAllergy = new ArrayList<>();

        if(auth != null) {
            long[] dicIds = Longs.toArray(tokenDecoder.getDoctorInClinic());
            recentlyPrescribedAllergy = allergyRepository.findByDoctorInClinicIn(dicIds, new Date(Instant.now().minus(30, ChronoUnit.DAYS).toEpochMilli()), new Date(Instant.now().toEpochMilli()))
                    .stream()
                    .filter(allergy -> allergy.getAllergyType() !=null)
                    .map(allergy -> allergy.getAllergyType().getId())
                    .collect(Collectors.toList());
        }

        return recentlyPrescribedAllergy;
    }

    private List<Long> getAllergyTypeIdsFromElasticSearch(String value, List<Long> recentlyPrescribedAllergyTypes) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery().should(QueryBuilders.prefixQuery("allergyType.name", value).boost(10f))
                .must(QueryBuilders.matchPhrasePrefixQuery("allergyType.name", value).slop(5).boost(1f));

        List<FunctionScoreQueryBuilder.FilterFunctionBuilder> functionBuilderList = new ArrayList<>();
        functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(getStartsWithScript(value, "allergyType.name"))));
        if(!CollectionUtils.isEmpty(recentlyPrescribedAllergyTypes)){
            functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(getRecentlyPrescribedScript(recentlyPrescribedAllergyTypes))));
        }

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.functionScoreQuery(qb, functionBuilderList.toArray(new FunctionScoreQueryBuilder.FilterFunctionBuilder[0]))).build();
        if(elasticsearchTemplate.count(nativeSearchQuery, ESSearchAllergyType.class) > 1000){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too many records. Please be more specific in search");
        }

        List<Long> allergyTypeIds = new ArrayList<>();
        elasticsearchTemplate.stream(nativeSearchQuery, ESSearchAllergyType.class).forEachRemaining(esSearchAllergyType -> {
            if(!allergyTypeIds.contains(esSearchAllergyType.getAllergyType().getId())){
                allergyTypeIds.add(esSearchAllergyType.getAllergyType().getId());
//                return;
            }
//            allergyTypeIds.add(esSearchAllergyType.getId());
        });

        return allergyTypeIds;
    }
}
