//package com.ayush.ravan.es;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
////@PropertySource(value = "classpath:applications.properties")
//@EnableElasticsearchRepositories(basePackages = "com.ayush.ravan.es.repository")
//@ComponentScan(basePackages = { "com.ayush.ravan.es.service" })
//public class ElasticSearchConfiguration {
//
//    @Value("${elasticsearch.host}")
//    private String EsHost;
//
//    @Value("${elasticsearch.port}")
//    private int EsPort;
//
//    @Value("${es.index-name}")
//    private String indexName;
//
//    @Value("${es.index-type}")
//    private String indexType;
//
//    public String getIndexName() {
//        return indexName;
//    }
//
//    public void setIndexName(String indexName) {
//        this.indexName = indexName;
//    }
//
//    public String getIndexType() {  return indexType; }
//
//    public void setIndexType(String indexType) { this.indexType = indexType; }
//
//
//    @Bean
//    public RestHighLevelClient client() {
//        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
//                .connectedTo("localhost:9200")
//                .build();
//
//        return RestClients.create(clientConfiguration)
//                .rest();
//    }
//
//    @Bean
//    public ElasticsearchOperations elasticsearchTemplate() {
//        return new ElasticsearchRestTemplate(client());
//    }
//
//
////    @Bean
////    public RestHighLevelClient client() {
////        return new RestHighLevelClient(RestClient.builder(HttpHost.create(elasticHost)));
////    }
//
//
//
////    private static final int ONE_MINUTE = 60 * 1000;
////    private static final int ONE_SECOND = 1000;
////    @Bean
////    public RestHighLevelClient elasticsearchClient() {
////        HttpHost host = new HttpHost("localhost", 9200);
////        RestClientBuilder.RequestConfigCallback requestConfigCallback = requestConfigBuilder -> requestConfigBuilder
////                .setConnectionRequestTimeout(0)
////                .setSocketTimeout(ONE_MINUTE)
////                .setConnectTimeout(ONE_SECOND * 5);
////
////        RestClientBuilder builder = RestClient.builder(host)
////                .setRequestConfigCallback(requestConfigCallback);
////
////        return new RestHighLevelClient(builder);
////    }
//}
