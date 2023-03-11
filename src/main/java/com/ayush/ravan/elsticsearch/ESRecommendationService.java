package com.ayush.ravan.elsticsearch;

package co.arctern.api.emr.service.middlelayer;

import co.arctern.api.emr.domain.Medicine;
import co.arctern.api.emr.domain.dto.DataForRecommendationEngine;
import co.arctern.api.emr.domain.dto.RecommendationDto;
import co.arctern.api.emr.domain.dto.RecommendationEngineResponseBody;
import co.arctern.api.emr.domain.projection.MedicineRecommendation;
import co.arctern.api.emr.search.domain.*;
import co.arctern.api.emr.security.TokenDecoder;
import co.arctern.api.emr.service.api.ConsultationRepository;
import co.arctern.api.emr.service.api.DoctorInClinicRepository;
import co.arctern.api.emr.service.api.MedicineRepository;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

@Service
@Transactional
public class ESRecommendationService {

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    ConsultationRepository consultationRepository;

    @Autowired
    ProjectionFactory projectionFactory;

    @Autowired
    TokenDecoder tokenDecoder;

    @Autowired
    DoctorInClinicRepository doctorInClinicRepository;

    @Autowired
    MedicineRepository medicineRepository;

    public AggregatedPage<ESDiagnosticType> fetchDiagnosticTypes(RecommendationDto recommendationDto, Boolean medicineFlag, Pageable pageable) {
        return elasticsearchTemplate.queryForPage(generateSearchQuery(recommendationDto, medicineFlag, pageable), ESDiagnosticType.class);

    }

    public AggregatedPage<ESDiagnosisType> fetchDiagnosisTypes(RecommendationDto recommendationDto, Boolean medicineFlag, Pageable pageable) {
        return elasticsearchTemplate.queryForPage(generateSearchQuery(recommendationDto, medicineFlag, pageable), ESDiagnosisType.class);
    }

    public AggregatedPage<ESSymptomType> fetchSymptomTypes(RecommendationDto recommendationDto, Boolean medicineFlag, Pageable pageable) {
        return elasticsearchTemplate.queryForPage(generateSearchQuery(recommendationDto, medicineFlag, pageable), ESSymptomType.class);
    }

    public AggregatedPage<ESAllergyType> fetchAllergyTypes(RecommendationDto recommendationDto, Boolean medicineFlag, Pageable pageable) {
        return elasticsearchTemplate.queryForPage(generateSearchQuery(recommendationDto, medicineFlag, pageable), ESAllergyType.class);
    }

    public AggregatedPage<ESClinicalFindingType> fetchClinicalFindingTypes(RecommendationDto recommendationDto, Boolean medicineFlag, Pageable pageable) {
        return elasticsearchTemplate.queryForPage(generateSearchQuery(recommendationDto, medicineFlag, pageable), ESClinicalFindingType.class);
    }
    public AggregatedPage<ESProcedureType> fetchProcedureTypes(RecommendationDto recommendationDto, Boolean medicineFlag, Pageable pageable) {
        return elasticsearchTemplate.queryForPage(generateSearchQuery(recommendationDto, medicineFlag, pageable), ESProcedureType.class);
    }

    public AggregatedPage<ESMedicine> fetchMedicines(RecommendationDto recommendationDto, Boolean medicineFlag, Pageable pageable) {
        return elasticsearchTemplate.queryForPage(generateSearchQuery(recommendationDto, medicineFlag, pageable), ESMedicine.class);
    }

    public AggregatedPage<ESAdvice> fetchAdvices(RecommendationDto recommendationDto, Boolean medicineFlag, Pageable pageable) {
        return elasticsearchTemplate.queryForPage(generateSearchQuery(recommendationDto, medicineFlag, pageable), ESAdvice.class);
    }

    public DataForRecommendationEngine fetchDataFromConsultation(RecommendationDto recommendationDto, List<Long> dicIds, List<String> specialities) {
        DataForRecommendationEngine dataForRecommendationEngine = new DataForRecommendationEngine();
        List<String> symptomTypeNames = recommendationDto.getSymptomTypeNames();
        dataForRecommendationEngine.setDoctorInClinicIds(dicIds);
        if (!(symptomTypeNames == null || symptomTypeNames.isEmpty())) {
            dataForRecommendationEngine.setSymptomTypeNames(symptomTypeNames);
        }
        dataForRecommendationEngine.setSpecialityNames(specialities);
        return dataForRecommendationEngine;
    }

    public SearchQuery generateSearchQuery(RecommendationDto recommendationDto, Boolean medicineFlag, Pageable pageable) {
        List<Long> dicIds = fetchDicFromToken();
        List<String> specialities = doctorInClinicRepository
                .findById(Long.parseLong(String.valueOf(dicIds.get(0))))
                .get().getDoctor().getSpeciality()
                .stream().map(a -> a.getName())
                .collect(Collectors.toList());

        DataForRecommendationEngine dataForRecommendationEngine = fetchDataFromConsultation(recommendationDto, dicIds, specialities);
        SearchQuery searchQuery;
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withFilter(applyFilter(dataForRecommendationEngine, medicineFlag))
                .withSort(applySort())
                .withPageable(pageable)
                .build();
        return searchQuery;

    }

    public BoolQueryBuilder applyFilter(DataForRecommendationEngine dataForRecommendationEngine, Boolean medicineFlag) {
        BoolQueryBuilder qbForSymptom = QueryBuilders.boolQuery();
        BoolQueryBuilder qbForSpeciality = QueryBuilders.boolQuery();
        BoolQueryBuilder qbForDoctorInClinic = QueryBuilders.boolQuery();
        BoolQueryBuilder qbForMedicineInClinic = QueryBuilders.boolQuery();
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        List<String> symptomTypeNames = dataForRecommendationEngine.getSymptomTypeNames();
        List<Long> dicIds = dataForRecommendationEngine.getDoctorInClinicIds();
        List<String> specialityNames = dataForRecommendationEngine.getSpecialityNames();
        for (String specialityName : specialityNames) {
            qbForSpeciality.should(QueryBuilders.matchQuery("specialities.name", specialityName));
        }
        for (Long dicId : dicIds) {
            qbForDoctorInClinic.should(QueryBuilders.matchQuery("doctorInClinicId", dicId)).boost(10f);
        }
        if (symptomTypeNames == null || symptomTypeNames.isEmpty()) {
            return qb.should(qbForDoctorInClinic).should(qbForSpeciality);
        }
        for (String symptomTypeName : symptomTypeNames) {
            qbForSymptom.should(QueryBuilders.matchQuery("symptomTypes.name", symptomTypeName));
        }
        if (medicineFlag) {
            qb.must(qbForMedicineInClinic.must(QueryBuilders.existsQuery("medicineInClinicId")));
        }
        return qb.should(qbForSymptom).boost(5f).should(qbForDoctorInClinic).should(qbForSpeciality);
    }

    public SortBuilder applySort() {
        return SortBuilders
                .fieldSort("creationTime")
                .order(SortOrder.DESC);
    }

    public RecommendationEngineResponseBody fetchRecommendationDetails(RecommendationDto recommendationDto,
                                                                       Pageable pageable) {
        RecommendationEngineResponseBody recommendationEngineResponseBody = new RecommendationEngineResponseBody();
        recommendationEngineResponseBody.setSymptoms(this.fetchSymptomTypes(recommendationDto, false, pageable).getContent().stream().map(a -> a.getSymptomType()).collect(Collectors.toSet()));
        recommendationEngineResponseBody.setAllergies(this.fetchAllergyTypes(recommendationDto, false, pageable).getContent().stream().map(a -> a.getAllergyType()).collect(Collectors.toSet()));
        recommendationEngineResponseBody.setClinicalFindings(this.fetchClinicalFindingTypes(recommendationDto, false, pageable).getContent().stream().map(a -> a.getClinicalFindingType()).collect(Collectors.toSet()));
        recommendationEngineResponseBody.setDiagnoses(this.fetchDiagnosisTypes(recommendationDto, false, pageable).getContent().stream().map(a -> a.getDiagnosisType()).collect(Collectors.toSet()));
        recommendationEngineResponseBody.setMedicines(findActiveMedicine(this.fetchMedicines(recommendationDto, false, pageable).getContent().stream().map(a -> a.getMedicine()).collect(Collectors.toSet())));
        recommendationEngineResponseBody.setProcedures(this.fetchProcedureTypes(recommendationDto, false, pageable).getContent().stream().map(a -> a.getProcedureType()).collect(Collectors.toSet()));
        recommendationEngineResponseBody.setAdvices(this.fetchAdvices(recommendationDto, false, pageable).getContent().stream().map(a -> a.getAdvice()).collect(Collectors.toSet()));
        recommendationEngineResponseBody.setDiagnostics(this.fetchDiagnosticTypes(recommendationDto, false, pageable).getContent().stream().map(a -> a.getDiagnosticType()).collect(Collectors.toSet()));
        return recommendationEngineResponseBody;
    }

    private Set<MedicineForRecommendation> findActiveMedicine(Set<MedicineForRecommendation> collect) {
        if (CollectionUtils.isEmpty(collect)) return collect;
        List<Long> activeMedicineIds = collect.stream().map(MedicineForRecommendation::getId).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(activeMedicineIds)) return new HashSet<>();
        Set<Long> activeMedicineByIds = medicineRepository.findActiveMedicineByIds(activeMedicineIds).stream().map(MedicineRecommendation::getId).collect(Collectors.toSet());
        if(CollectionUtils.isEmpty(activeMedicineByIds)) return new HashSet<>();
        return collect.stream().filter(a->a.getId() != null && activeMedicineByIds.contains(a.getId())).collect(Collectors.toSet());
    }

    public List<Long> fetchDicFromToken() {
        List<Long> dicIds = tokenDecoder.getDoctorInClinic();
        List<Long> dicIdsNew = new ArrayList<Long>();
        Integer size = dicIds.size();
        for (int i = 0; i < size; i++) {
            dicIdsNew.add(Long.parseLong(String.valueOf(dicIds.get(i))));
        }
        return dicIdsNew;
    }
}




