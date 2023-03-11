package com.ayush.ravan.elsticsearch;

package co.arctern.api.emr.service.middlelayer;


import co.arctern.api.emr.domain.*;
import co.arctern.api.emr.domain.dto.MedicineSearchDto;
import co.arctern.api.emr.domain.dto.SearchContentDto;
import co.arctern.api.emr.domain.dto.SearchContentDtoWithPagination;
import co.arctern.api.emr.domain.projection.*;
import co.arctern.api.emr.search.domain.DiagnosticTypeForRecommendation;
import co.arctern.api.emr.search.domain.ESSearchDiagnosticType;
import co.arctern.api.emr.service.api.*;
import com.azure.storage.queue.models.QueueStorageException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class SearchService {
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final MedicineRepository medicineRepository;
    private final SearchV2Service searchV2Service;
    private final PaginationService paginationService;

    private final ClinicalFindingTypeRepository clinicalFindingTypeRepository;
    private final AllergyTypeRepository allergyTypeRepository;
    private final SymptomTypeRepository symptomTypeRepository;
    private final DiagnosisTypeRepository diagnosisTypeRepository;
    private final DiagnosticTypeRepository diagnosticTypeRepository;
    private final ProcedureTypeRepository procedureTypeRepository;
    private final  AdviceRepository adviceRepository;

    @Autowired
    public SearchService(ElasticsearchTemplate elasticsearchTemplate,
                         MedicineRepository medicineRepository,
                         SearchV2Service searchV2Service, PaginationService paginationService,
                         ClinicalFindingTypeRepository clinicalFindingTypeRepository,
                         AllergyTypeRepository allergyTypeRepository,
                         SymptomTypeRepository symptomTypeRepository,
                         DiagnosisTypeRepository diagnosisTypeRepository,
                         DiagnosticTypeRepository diagnosticTypeRepository,
                         ProcedureTypeRepository procedureTypeRepository,
                         AdviceRepository adviceRepository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.medicineRepository = medicineRepository;
        this.searchV2Service = searchV2Service;
        this.paginationService = paginationService;
        this.clinicalFindingTypeRepository = clinicalFindingTypeRepository;
        this.allergyTypeRepository = allergyTypeRepository;
        this.symptomTypeRepository = symptomTypeRepository;
        this.diagnosisTypeRepository = diagnosisTypeRepository;
        this.diagnosticTypeRepository = diagnosticTypeRepository;
        this.procedureTypeRepository =  procedureTypeRepository;
        this.adviceRepository = adviceRepository;
    }

    @Transactional
    public List<SearchContentDto> searchByType(String type, String value, Long clinicId, Pageable pageable, String auth) {
        List<SearchContentDto> dtos = new ArrayList<>();
        switch (type) {
            case "diagnostic": {
                try{
                    BoolQueryBuilder qb = QueryBuilders.boolQuery().should(QueryBuilders.matchPhrasePrefixQuery("diagnosticType.name", value).boost(10f))
                            .should(QueryBuilders.boolQuery().should(QueryBuilders
                                    .multiMatchQuery(value
                                            , "diagnosticType.name"
                                            , "diagnosticType.abbreviation"
                                            , "diagnosticType.alias")
                                    .boost(5f)));

                    List<Long> recentlyPrescribedDiagnosticTypes = searchV2Service.getRecentlyPrescribedDiagnosticTypes(auth);

                    List<FunctionScoreQueryBuilder.FilterFunctionBuilder> functionBuilderList = new ArrayList<>();
                    functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(searchV2Service.getStartsWithScript(value, "diagnosticType.name"))));
                    if(!CollectionUtils.isEmpty(recentlyPrescribedDiagnosticTypes)){
                        functionBuilderList.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.scriptFunction(searchV2Service.getRecentlyPrescribedScript(recentlyPrescribedDiagnosticTypes))));
                    }

                    List<Long> esSearchDiagnosticTypesIds = new ArrayList<>();

                    elasticsearchTemplate.stream(new NativeSearchQueryBuilder()
                            .withQuery(QueryBuilders.functionScoreQuery(qb, functionBuilderList.toArray(new FunctionScoreQueryBuilder.FilterFunctionBuilder[0])))
                            .build(), ESSearchDiagnosticType.class).forEachRemaining(esSearchDiagnosticType -> {
                        if(esSearchDiagnosticTypesIds.contains(esSearchDiagnosticType.getId())){
                            return;
                        }
                        SearchContentDto dto = new SearchContentDto();
                        DiagnosticTypeForRecommendation diagnosticType = esSearchDiagnosticType.getDiagnosticType();
                        String name = diagnosticType.getName();
                        dto.setName(name);
                        dto.setState(diagnosticType.getDiagnosticState());
                        dto.setId(diagnosticType.getId());
                        dto.setLength(name.length());
                        esSearchDiagnosticTypesIds.add(esSearchDiagnosticType.getId());
                        dtos.add(dto);
                    });
                    if (CollectionUtils.isEmpty(dtos)) {
                        log.info("diagnostic database");
                        dtos.add(findDiagnosticTypeByName(pageable));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    dtos.add(findDiagnosticTypeByName(pageable));
                }
                break;
            }
            case "medicine": {
                try{
                    List<Long> medicinesIdsFromElasticSearch = searchV2Service.getMedicinesIdsFromElasticSearch(value, searchV2Service.getRecentlyPrescribedMedicines(auth));

                    List<MedicineSearchDto> medicineSearchDtoList = new ArrayList<>();
                    for(Long i=0L; i<medicinesIdsFromElasticSearch.size(); i++) {
                        medicineSearchDtoList.add(MedicineSearchDto.builder().index(i).medicineId(medicinesIdsFromElasticSearch.get(i.intValue())).build());
                    }

                    List<Medicine> medicines = medicineRepository.findByIdInAndIsActiveTrue(medicinesIdsFromElasticSearch);

                    medicineSearchDtoList.stream()
                            .peek(medicineSearchDto -> medicines.parallelStream()
                                    .filter(medicine -> medicine.getId().equals(medicineSearchDto.getMedicineId()))
                                    .findFirst().ifPresent(medicineSearchDto::setMedicine))
                            .filter(medicineSearchDto -> medicineSearchDto.getMedicine() != null)
                            .sorted(Comparator.comparing(MedicineSearchDto::getIndex))
                            .filter(DiagnosticRevampService.distinctByKey(medicineSearchDto -> medicineSearchDto.getMedicine().getId()))
                            .peek(a ->
                                    {
                                        SearchContentDto dto = new SearchContentDto();
                                        Medicine medicine = a.getMedicine();
                                        String name = medicine.getName();
                                        dto.setName(name);
                                        dto.setId(medicine.getId());
                                        dto.setType(medicine.getDosageForm());
                                        dto.setLength(name.length());
                                        dtos.add(dto);
                                    }
                            ).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(dtos)) {
                        log.info("medicine database");
                        dtos.add(findMedicineByName(pageable));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    dtos.add(findMedicineByName(pageable));
                }
                break;
            }
            case "procedure": {
                try{
                    searchV2Service.searchProcedureType(value, pageable, auth).getContent().stream().forEach(o -> {
                        SearchContentDto searchContentDto = new SearchContentDto();
                        String name = ((ProcedureTypes) o).getName();
                        searchContentDto.setName(name);
                        searchContentDto.setId(((ProcedureTypes) o).getId());
                        searchContentDto.setLength(name.length());
                        dtos.add(searchContentDto);
                    });
                    if (CollectionUtils.isEmpty(dtos)) {
                        log.info("procedure database");
                        dtos.add(findProcedureTypeByName(pageable));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    dtos.add(findProcedureTypeByName(pageable));
                }
                break;
            }
            case "advice": {
                try{
                    searchV2Service.searchAdvice(value, pageable, auth).getContent().stream().forEach(o -> {
                        SearchContentDto searchContentDto = new SearchContentDto();
                        String name = ((Advices) o).getText();
                        searchContentDto.setName(name);
                        searchContentDto.setId(((Advices) o).getId());
                        searchContentDto.setLength(name.length());
                        dtos.add(searchContentDto);
                    });
                    if (CollectionUtils.isEmpty(dtos)) {
                        log.info("advice database");
                        dtos.add(findAdviceByName(pageable));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    dtos.add(findAdviceByName(pageable));
                }
                break;
            }
            case "diagnosis": {
                try{
                    searchV2Service.searchDiagnosisType(value, pageable, auth).getContent().stream().forEach(o -> {
                        SearchContentDto searchContentDto = new SearchContentDto();
                        String name = ((DiagnosisTypes) o).getName();
                        searchContentDto.setName(name);
                        searchContentDto.setId(((DiagnosisTypes) o).getId());
                        searchContentDto.setLength(name.length());
                        dtos.add(searchContentDto);
                    });
                    if (CollectionUtils.isEmpty(dtos)) {
                        log.info("diagnosis database");
                        dtos.add(findDiagnosisTypeByName(pageable));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    dtos.add(findDiagnosisTypeByName(pageable));
                }
                break;
            }
            case "symptom": {
                try{
                    searchV2Service.searchSymptomType(value, pageable, auth).getContent().stream().forEach(o -> {
                        SearchContentDto searchContentDto = new SearchContentDto();
                        String name = ((SymptomTypes) o).getName();
                        searchContentDto.setName(name);
                        searchContentDto.setId(((SymptomTypes) o).getId());
                        searchContentDto.setLength(name.length());
                        dtos.add(searchContentDto);
                    });
                    if (CollectionUtils.isEmpty(dtos)) {
                        log.info("symptom database");
                        dtos.add(findSymptomTypeByNameSearch(pageable));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    dtos.add(findSymptomTypeByNameSearch(pageable));
                }
                break;
            }
            case "allergy": {
                try{
                    searchV2Service.searchAllergyType(value, pageable, auth).getContent().stream().forEach(o -> {
                        SearchContentDto searchContentDto = new SearchContentDto();
                        String name = ((AllergyTypes) o).getName();
                        searchContentDto.setName(name);
                        searchContentDto.setId(((AllergyTypes) o).getId());
                        searchContentDto.setLength(name.length());
                        dtos.add(searchContentDto);
                    });
                    if (CollectionUtils.isEmpty(dtos)) {
                        log.info("allergy database");
                        dtos.add(findAllergyTypeByName(pageable));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    dtos.add(findAllergyTypeByName(pageable));
                }
                break;
            }
            case "clinicalFinding": {
                try{
                    searchV2Service.searchClinicalFindingType(value, pageable, auth).getContent().stream().forEach(o -> {
                        SearchContentDto searchContentDto = new SearchContentDto();
                        String name = ((ClinicalFindingTypes) o).getName();
                        searchContentDto.setName(name);
                        searchContentDto.setId(((ClinicalFindingTypes) o).getId());
                        searchContentDto.setLength(name.length());
                        dtos.add(searchContentDto);
                    });
                    if (CollectionUtils.isEmpty(dtos)) {
                        log.info("clinicalFinding database");
                        dtos.add(findClinicalFindingTypeByNameSearch(pageable));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    dtos.add(findClinicalFindingTypeByNameSearch(pageable));
                }
                break;
            }
        }
        return dtos;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public SearchContentDtoWithPagination searchByTypeForTranscription(String type, String value, Long clinicId, Integer pageNumber, Integer size, String auth, Pageable pageable) {
        List<SearchContentDto> searchContentDtos = searchByType(type, value, clinicId, pageable, auth);
        SearchContentDtoWithPagination searchContentDtoWithPagination = new SearchContentDtoWithPagination(pageNumber, size);
        searchContentDtoWithPagination.setTotalElements(searchContentDtos.size());
        searchContentDtoWithPagination.setTotalPage((int) Math.ceil((double) (searchContentDtos.size()) / size));
        searchContentDtoWithPagination.setSearchContentDtos(paginationService.getPage(searchContentDtos, pageNumber, size));
        return searchContentDtoWithPagination;

    }

    private SearchContentDto findClinicalFindingTypeByNameSearch(Pageable pageable){
        Optional<ClinicalFindingType> data = clinicalFindingTypeRepository.findAll(pageable).stream().findFirst();
        SearchContentDto searchContentDto = null;
        if(data.isPresent()){
            searchContentDto = new SearchContentDto();
            ClinicalFindingType clinicalFindingType = data.get();
            searchContentDto.setName(clinicalFindingType.getName());
            searchContentDto.setId(clinicalFindingType.getId());
            searchContentDto.setLength(clinicalFindingType.getName().length());
            return searchContentDto;
        }
        return this.noDataFound();
    }

    private SearchContentDto findAllergyTypeByName(Pageable pageable){
        Optional<AllergyType> data = allergyTypeRepository.findAll(pageable).stream().findFirst();
        SearchContentDto searchContentDto = null;
        if(data.isPresent()){
            searchContentDto = new SearchContentDto();
            AllergyType allergyType = data.get();
            searchContentDto.setName(allergyType.getName());
            searchContentDto.setId(allergyType.getId());
            searchContentDto.setLength(allergyType.getName().length());
            return searchContentDto;
        }
        return this.noDataFound();
    }

    private SearchContentDto findSymptomTypeByNameSearch(Pageable pageable){
        Optional<SymptomType> data = symptomTypeRepository.findAll(pageable).stream().findFirst();
        SearchContentDto searchContentDto = null;
        if(data.isPresent()){
            searchContentDto = new SearchContentDto();
            SymptomType symptomType = data.get();
            searchContentDto.setName(symptomType.getName());
            searchContentDto.setId(symptomType.getId());
            searchContentDto.setLength(symptomType.getName().length());
            return searchContentDto;
        }
        return this.noDataFound();
    }

    private SearchContentDto findDiagnosisTypeByName(Pageable pageable){
        Optional<DiagnosisType> data = diagnosisTypeRepository.findAll(pageable).stream().findFirst();
        SearchContentDto searchContentDto = null;
        if(data.isPresent()){
            searchContentDto = new SearchContentDto();
            DiagnosisType diagnosisType = data.get();
            searchContentDto.setName(diagnosisType.getName());
            searchContentDto.setId(diagnosisType.getId());
            searchContentDto.setLength(diagnosisType.getName().length());
            return searchContentDto;
        }
        return this.noDataFound();
    }

    private SearchContentDto findAdviceByName(Pageable pageable){
        Optional<Advice> data = adviceRepository.findAll(pageable).stream().findFirst();
        SearchContentDto searchContentDto = null;
        if(data.isPresent()){
            searchContentDto = new SearchContentDto();
            Advice advice = data.get();
            searchContentDto.setName(advice.getText());
            searchContentDto.setId(advice.getId());
            searchContentDto.setLength(advice.getText().length());
            return searchContentDto;
        }
        return this.noDataFound();
    }

    private SearchContentDto findProcedureTypeByName(Pageable pageable){
        Optional<ProcedureType> data = procedureTypeRepository.findAll(pageable).stream().findFirst();
        SearchContentDto searchContentDto = null;
        if(data.isPresent()){
            searchContentDto = new SearchContentDto();
            ProcedureType procedureType = data.get();
            searchContentDto.setName(procedureType.getName());
            searchContentDto.setId(procedureType.getId());
            searchContentDto.setLength(procedureType.getName().length());
            return searchContentDto;
        }
        return this.noDataFound();
    }

    private SearchContentDto findMedicineByName(Pageable pageable){
        Optional<Medicine> data = medicineRepository.findAll(pageable).stream().findFirst();
        SearchContentDto searchContentDto = null;
        if(data.isPresent()){
            searchContentDto = new SearchContentDto();
            Medicine medicine = data.get();
            searchContentDto.setName(medicine.getName());
            searchContentDto.setId(medicine.getId());
            searchContentDto.setLength(medicine.getName().length());
            return searchContentDto;
        }
        return this.noDataFound();
    }

    private SearchContentDto findDiagnosticTypeByName(Pageable pageable){
        Optional<DiagnosticType> data =diagnosticTypeRepository.findAll(pageable).stream().findFirst();
        SearchContentDto searchContentDto = null;
        if(data.isPresent()){
            searchContentDto = new SearchContentDto();
            DiagnosticType diagnosticType = data.get();
            searchContentDto.setName(diagnosticType.getName());
            searchContentDto.setId(diagnosticType.getId());
            searchContentDto.setLength(diagnosticType.getName().length());
            return searchContentDto;
        }
        return this.noDataFound();
    }

    private SearchContentDto noDataFound(){
        SearchContentDto searchContentDto = new SearchContentDto();
        searchContentDto.setName("No Data");
        searchContentDto.setId(0l);
        searchContentDto.setLength("No Data".length());
        return searchContentDto;
    }
}

