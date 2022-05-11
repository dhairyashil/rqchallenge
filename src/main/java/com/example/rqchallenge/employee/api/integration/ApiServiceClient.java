package com.example.rqchallenge.employee.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ApiServiceClient<T, U> {

    ObjectMapper mapper = new ObjectMapper();

    T execute(U request);
}
