package com.example.rqchallenge.employee.util;

import com.example.rqchallenge.employee.dto.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class AppTestHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Employee dhairya() {
        Employee dhairya = new Employee();
        dhairya.setId(1L);
        dhairya.setEmployeeName("Dhairya Bankar");
        dhairya.setEmployeeAge(32);
        dhairya.setEmployeeSalary(30000);
        return dhairya;
    }

    public static Employee rajesh() {
        Employee rajesh = new Employee();
        rajesh.setId(2L);
        rajesh.setEmployeeName("Rajesh Patil");
        rajesh.setEmployeeAge(28);
        rajesh.setEmployeeSalary(190000);
        return rajesh;
    }

    public static Employee ajay() {
        Employee ajay = new Employee();
        ajay.setId(3L);
        ajay.setEmployeeName("Ajay Thorat");
        ajay.setEmployeeAge(55);
        ajay.setEmployeeSalary(130000);
        return ajay;
    }

    public static Employee john() {
        Employee john = new Employee();
        john.setId(4L);
        john.setEmployeeName("John Garlo");
        john.setEmployeeAge(40);
        john.setEmployeeSalary(200000);
        return john;
    }

    public static Employee dhiraj() {
        Employee dhiraj = new Employee();
        dhiraj.setId(5L);
        dhiraj.setEmployeeName("Dhiraj Patil");
        dhiraj.setEmployeeAge(39);
        dhiraj.setEmployeeSalary(330000);
        return dhiraj;
    }

    public static Employee tejal() {
        Employee tejal = new Employee();
        tejal.setId(6L);
        tejal.setEmployeeName("Tejal Bankar");
        tejal.setEmployeeAge(28);
        tejal.setEmployeeSalary(90000);
        return tejal;
    }

    public static List<String> topTenEmployeeNames() {
        List<String> topTenEmployees = List.of(
                "Paul Byrd",
                "Yuri Berry",
                "Cedric Kelly",
                "Charde Marshall",
                "Tatyana Fitzpatrick",
                "Quinn Flynn",
                "Jenette Caldwell",
                "Brielle Williamson",
                "Rhona Davidson",
                "Tiger Nixon"
        );

        return topTenEmployees;
    }

    public static List<Employee> getAllEmployees() {
        final String fileContents = readFile("data/allEmployees.json");
        List<Employee> employees = null;
        try {
            employees = objectMapper
                                .readerForListOf(Employee.class)
                                .readValue(fileContents);
        } catch (JsonProcessingException e) {
            log.error("Getting Exception while parsing file contents.");
        }
        return employees;
    }

    private static String readFile(String fileName) {
        String jsonString = "";
        try {
            File file = new ClassPathResource(fileName).getFile();
            jsonString = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException ioException) {
            log.error("Getting Exception while getting and reading file.");
        }
        return jsonString;
    }

    public static String getAllEmployeesResponseFromApi() {
        return readFile("data/allEmployeesResponseFromApi.json");
    }

    public static String getAllEmployeeByIdFromApi() {
        return readFile("data/getEmployeeByIdApiResponse.json");
    }

    public static String deleteEmployeeByIdResponseFromApi() {
        return readFile("data/deleteEmployeeByIdApiResponse.json");
    }

    public static String createEmployeeResponseFromApi() {
        return readFile("data/createEmployeeApiResponse.json");
    }

    public static String createEmployeeApiRequest() throws JsonProcessingException {
        Map<String ,Object> requestMap = new HashMap<>();
        requestMap.put("name", "Dhairya Bankar");
        requestMap.put("salary", 20000);
        requestMap.put("age", 21);
        return objectMapper.writeValueAsString(requestMap);
    }
}
