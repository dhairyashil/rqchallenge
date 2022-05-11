package com.example.rqchallenge.employee.api.integration;

import com.example.rqchallenge.employee.dto.Employee;
import com.example.rqchallenge.employee.exception.ApiResponseJsonParseException;
import com.example.rqchallenge.employee.exception.TooManyRequestException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component("getAllEmployeesClient")
public class GetAllEmployees implements ApiServiceClient<List<Employee>, Void> {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final String getAllEmployeesApiPath = "/api/v1/employees";

    @Override
    public List<Employee> execute(Void request) {
        List<Employee> employees =
                webClientBuilder.build().get()
                    .uri(uriBuilder -> uriBuilder.path(getAllEmployeesApiPath).build())
                    .retrieve()
                    .onStatus(
                            httpStatus -> httpStatus.value() == 429,
                            clientResponse -> Mono.just(new TooManyRequestException())
                    )
                    .toEntity(String.class)
                    .map(apiResponse -> {
                        List<Employee> employeeList;
                        try {
                            JsonNode apiResponseNode = mapper.readTree(apiResponse.getBody());
                            JsonNode dataNode = apiResponseNode.at("/data");
                            employeeList = mapper.readerForListOf(Employee.class).readValue(dataNode);

                        } catch (Exception ioException) {
                            throw new ApiResponseJsonParseException();
                        }

                        return employeeList;
                    })
                    .block();
        return employees;
    }
}