package com.ayush.ravan.elsticsearch.search.query;

import co.arctern.api.emr.search.domain.PatientHistory;
import co.arctern.api.emr.utility.DateUtil;
import com.google.common.collect.Lists;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Component
public class SearchQueryBuilder {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * This method is used in Patient search in patient module(Doctor app)
     *
     * @param searchText
     * @param id
     * @param conditions
     * @param startDate
     * @param endDate
     * @param pageable
     * @return
     */
    //private Logger log = LoggerFactory.getLogger(SearchQueryBuilder.class);
  //  @PostAuthorize("@elasticApiAuthentication.userValidate(returnObject.content, authentication)")
    public Page<PatientHistory> basedOnPatientNameAndPhone(
            String searchText,
            Long[] id,
            String[] conditions,
            Date startDate,
            Date endDate,
            Pageable pageable) {

        if (searchText != null && !searchText.isEmpty()) searchText = searchText.trim();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (searchText != null) {
            if (searchText.length() > 0) {
                queryBuilder.should(QueryBuilders.prefixQuery("patient.phone", searchText))
                        .should(QueryBuilders.matchPhrasePrefixQuery("patient.name", searchText));
            } else {
                queryBuilder.should(QueryBuilders.matchAllQuery());
            }
        }

        BoolQueryBuilder queryBuilder1 = QueryBuilders.boolQuery();
        for (Long ids : id) {
            queryBuilder1.should(nestedQuery("consultation",
                    QueryBuilders.boolQuery()
                            .must(nestedQuery("consultation.doctorInClinic",
                                    QueryBuilders
                                            .boolQuery()
                                            .must(matchQuery("consultation.doctorInClinic.id", ids)),
                                    ScoreMode.Avg))
                    , ScoreMode.Avg));
        }

        BoolQueryBuilder queryBuilder2 = QueryBuilders.boolQuery();
        if (conditions != null) {
            for (String condition : conditions) {
                queryBuilder2
                        .should(QueryBuilders.multiMatchQuery(condition,
                                "consultation.symptoms.symptomType.name",
                                "consultation.existingConditions.existingConditionType.name"));
            }
        }

        QueryBuilder queryBuilder3 = QueryBuilders.boolQuery();
        if (startDate != null && endDate != null) {
            Long now = 0l;
            Long startDateDiff = DateUtil.diffBetween(new Date(), startDate);
            if (DateUtil.equilityOfTwoDate(endDate, new Date()) == 1 || DateUtil.equilityOfTwoDate(endDate, new Date()) == 0) {
                if (startDateDiff != 0) {
                    queryBuilder3 = QueryBuilders.rangeQuery("consultation.appointmentTime")
                            .gte("now" + startDateDiff + "d" + "/d");
//                        .lte("now");
                } else {
                    queryBuilder3 = QueryBuilders.rangeQuery("consultation.appointmentTime")
                            .gte("now" + "/d");
                }
            } else {
                queryBuilder3 = QueryBuilders.rangeQuery("consultation.appointmentTime")
                        .gte("now" + startDateDiff + "d" + "/d")
                        .lte("now" + DateUtil.diffBetween(new Date(), endDate) + "d" + "/d");
            }
        }


        SortBuilder sortBuilder = SortBuilders
                .fieldSort("consultation.appointmentTime")
                .order(SortOrder.DESC)
                .setNestedPath("consultation");

        BoolQueryBuilder must = boolQuery()
                .must(queryBuilder1)
                .must(queryBuilder)
                .must(queryBuilder2)
                .must(queryBuilder3);

        SearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withFilter(must)
                .withSort(sortBuilder)
                .withPageable(pageable)
                .build();

        return elasticsearchTemplate.queryForPage(build, PatientHistory.class);
    }

    /**
     * Method for Recent care provider using Elastic search query----------
     *
     * @param id
     * @return
     */
    public List<PatientHistory> forRecentCareProvider(
            Long[] id
    ) {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (id.length > 0) {
            for (Long ids : id) {
                queryBuilder.should(QueryBuilders.matchQuery("patient.id", ids));
            }
        }

        SearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withFilter(boolQuery()
                        .must(queryBuilder)
                        .must(QueryBuilders
                                .rangeQuery("consultation.appointmentTime")
                                .gte("now-365d/d")))
                .withSort(SortBuilders
                        .fieldSort("consultation.appointmentTime")
                        .order(SortOrder.DESC))
                .build();

        return Lists.newArrayList(elasticsearchTemplate.stream(build, PatientHistory.class));
    }
}
