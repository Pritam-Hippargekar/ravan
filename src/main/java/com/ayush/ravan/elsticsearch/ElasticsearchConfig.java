package com.ayush.ravan.elsticsearch;

@Configuration
@EnableElasticsearchRepositories(basePackages = "co.arctern.api.emr.search.service")
@ComponentScan(basePackages = "co.arctern.api.emr.search")
public class ElasticsearchConfig {
//    elasticsearch:
//      doctor:
//        cluster:
//          index: "doctor-detail-stagings"
//          type: "doctor-detail-staging"

    @Value("${elasticsearch.doctor.cluster.index:doctor-detail-stagings}")
    private String esDoctorSearchIndex;

    @Value("${elasticsearch.doctor.cluster.type:doctor-detail-staging}")
    private String esDoctorSearchType;

    @Bean
    public String esDoctorSearchTypeName() {
        return esDoctorSearchType;
    }

    @Bean
    public String esDoctorSearchIndexName() {
        return esDoctorSearchIndex;
    }

    //getter and setter
}
