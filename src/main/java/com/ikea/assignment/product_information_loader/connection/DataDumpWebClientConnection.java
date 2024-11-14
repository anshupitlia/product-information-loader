package com.ikea.assignment.product_information_loader.connection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;


public class DataDumpWebClientConnection {
    @Value("products.datadump.location.url")
    private String dataDumpUrl;
    @Value("products.datadump.records")
    private String numberofRecords;

    @Bean
    public WebClient localApiClient() {
        return WebClient.create(dataDumpUrl + "?limit=" + numberofRecords);
    }
}
