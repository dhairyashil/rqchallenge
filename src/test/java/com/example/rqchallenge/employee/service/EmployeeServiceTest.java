package com.example.rqchallenge.employee.service;

import com.example.rqchallenge.employee.api.integration.ApiServiceClient;
import com.example.rqchallenge.employee.dto.Employee;
import com.example.rqchallenge.employee.exception.*;
import com.example.rqchallenge.employee.service.impl.EmployeeService;
import com.example.rqchallenge.employee.util.AppTestConstants;
import com.example.rqchallenge.employee.util.AppTestHelper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static com.example.rqchallenge.employee.util.AppTestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmployeeServiceTest {

    @MockBean
    private ApiServiceClient<Employee, Map<String, Object>> createEmployeeApiService;

    @MockBean
    private ApiServiceClient<List<Employee>, Void> getAllEmployeesService;

    @MockBean
    private ApiServiceClient<Optional<Employee>, String> getEmployeeByIdService;

    @MockBean
    private ApiServiceClient<String, String> deleteEmployeeService;

    @Autowired
    private EmployeeService employeeService;

    @Test
    void givenEmployeeList_whenGetAllEmployees_thenListOfEmployeeShouldBeReturned() {

        Employee dhairya = dhairya();
        Employee dhiraj = dhiraj();

        List<Employee> allEmployees = List.of(dhairya, dhiraj, tejal(), ajay(), john());

        given(getAllEmployeesService.execute(null))
                .willReturn(allEmployees);

        List<Employee> employees = employeeService.getAllEmployees();

        assertThat(employees).isNotNull();
        assertThat(employees).hasSize(5);
        assertThat(employees).containsAll(allEmployees);
        assertThat(employees).extracting("id").contains(dhairya.getId());
        assertThat(employees).extracting("employeeName").contains(dhairya.getEmployeeName());
    }

    @Test
    void givenEmptyEmployeeList_whenGetAllEmployees_thenReturnEmptyEmployeeList() {

        given(getAllEmployeesService.execute(null)).willReturn(Collections.emptyList());

        List<Employee> employees = employeeService.getAllEmployees();

        assertThat(employees).isEmpty();
        assertThat(employees).hasSize(0);
    }

    @Test
    void whenGetAllEmployees_thenThrowsTooManyRequests() {
        given(getAllEmployeesService.execute(null)).willThrow(new TooManyRequestException());
        assertThrows(TooManyRequestException.class, () -> employeeService.getAllEmployees());
    }

    @Test
    void whenGetAllEmployees_thenThrowsApiResponseJsonParseException() {
        given(getAllEmployeesService.execute(null)).willThrow(new ApiResponseJsonParseException());
        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.getAllEmployees());
    }

    @Test
    void whenGetEmployeeById_thenReturnEmployee() {
        String empId = "1";
        final Employee dhairya = dhairya();
        given(getEmployeeByIdService.execute(empId)).willReturn(Optional.of(dhairya));

        Employee employeeById = employeeService.getEmployeeById(empId);
        assertThat(employeeById).isNotNull();
        assertThat(employeeById).isEqualTo(dhairya);

        assertThat(employeeById).extracting("id").isEqualTo(dhairya.getId());
        assertThat(employeeById).extracting("employeeName").isEqualTo(dhairya.getEmployeeName());
        assertThat(employeeById).extracting("employeeAge").isEqualTo(dhairya.getEmployeeAge());
        assertThat(employeeById).extracting("employeeSalary").isEqualTo(dhairya.getEmployeeSalary());
    }

    @Test
    void whenGetEmployeeById_thenThrowsEmployeeNotFoundException() {
        String empId = "1090";
        given(getEmployeeByIdService.execute(empId)).willThrow(new EmployeeNotFoundException(empId));

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(empId));
    }

    @Test
    void whenGetEmployeeById_thenThrowsTooManyRequestsException() {
        String empId = "1090";
        given(getEmployeeByIdService.execute(empId)).willThrow(new TooManyRequestException());

        assertThrows(TooManyRequestException.class, () -> employeeService.getEmployeeById(empId));
    }

    @Test
    void whenGetEmployeeById_thenThrowsApiResponseJsonParseException() {
        String empId = "1090";
        given(getEmployeeByIdService.execute(empId)).willThrow(new ApiResponseJsonParseException());

        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.getEmployeeById(empId));
    }

    @Test
    void whenGetEmployeesByNameSearch_returnMatchingEmployeeList() {

        Employee dhairya = dhairya();
        Employee dhiraj = dhiraj();

        given(getAllEmployeesService.execute(null)).willReturn(List.of(dhairya, dhiraj, rajesh(), tejal(), ajay(), john()));

        List<Employee> filteredEmployees = employeeService.getEmployeesByNameSearch("dh");

        assertThat(filteredEmployees).hasSize(2);
        assertThat(filteredEmployees).contains(dhairya, dhiraj);
        assertThat(filteredEmployees).extracting("id").contains(dhairya.getId(), dhiraj.getId());
        assertThat(filteredEmployees).extracting("employeeName").contains(dhairya.getEmployeeName(), dhiraj.getEmployeeName());
        assertThat(filteredEmployees).extracting("employeeAge").contains(dhairya.getEmployeeAge(), dhiraj.getEmployeeAge());
        assertThat(filteredEmployees).extracting("employeeSalary").contains(dhairya.getEmployeeSalary(), dhiraj.getEmployeeSalary());
    }

    @Test
    void whenGetEmployeesByNameSearch_IfTooManyRequests_throwsTooManyRequestException() {
        given(getAllEmployeesService.execute(null)).willThrow(new TooManyRequestException());

        final String searchString = "dh";

        assertThrows(TooManyRequestException.class, () -> employeeService.getEmployeesByNameSearch(searchString));
    }

    @Test
    void whenGetEmployeesByNameSearch_IfApiResponseParsingFails_throwsApiResponseJsonParseException() {
        given(getAllEmployeesService.execute(null)).willThrow(new ApiResponseJsonParseException());
        final String searchString = "dh";
        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.getEmployeesByNameSearch(searchString));
    }

    @Test
    void whenGetHighestSalaryOfEmployee_IfSuccess_thenReturnHighestSalary() {
        Employee dhairya = dhairya();
        Employee dhiraj = dhiraj();

        given(getAllEmployeesService.execute(null)).willReturn(List.of(dhairya, dhiraj, rajesh(), tejal(), ajay(), john()));

        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();

        assertThat(highestSalary).isEqualTo(dhiraj.getEmployeeSalary());
    }

    @Test
    void whenGetHighestSalaryOfEmployee_IfTooManyRequests_thenThrowsTooManyRequestException() {
        given(getAllEmployeesService.execute(null)).willThrow(new TooManyRequestException());
        assertThrows(TooManyRequestException.class, () -> employeeService.getHighestSalaryOfEmployees());
    }

    @Test
    void whenGetHighestSalaryOfEmployee_IfApiResponseParsingFails_thenThrowsApiResponseJsonParseException() {
        given(getAllEmployeesService.execute(null)).willThrow(new ApiResponseJsonParseException());
        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.getHighestSalaryOfEmployees());
    }

    @Test
    void whenGetTopTenHighestEarningEmployeeNames_IfSuccess_thenReturnEmployeeNamesList() {
        List<Employee> allEmployees = AppTestHelper.getAllEmployees();
        given(getAllEmployeesService.execute(null)).willReturn(allEmployees);

        List<String> employeeNames = employeeService.getTopTenHighestEarningEmployeeNames();

        assertThat(employeeNames).hasSize(10);
        assertThat(employeeNames).containsAnyOf("Paul Byrd", "Tiger Nixon");
    }

    @Test
    void whenGetTopTenHighestEarningEmployeeNames_IfEmptyEmployeesList_thenReturnEmptyEmployeeNamesList() {
        given(getAllEmployeesService.execute(null)).willReturn(new ArrayList<>());

        List<String> employeeNames = employeeService.getTopTenHighestEarningEmployeeNames();

        assertThat(employeeNames).hasSize(0);
    }

    @Test
    void whenGetTopTenHighestEarningEmployeeNames_IfTooManyRequests_thenThrowsTooManyRequestException() {
        given(getAllEmployeesService.execute(null)).willThrow(new TooManyRequestException());
        assertThrows(TooManyRequestException.class, () -> employeeService.getTopTenHighestEarningEmployeeNames());
    }

    @Test
    void whenGetTopTenHighestEarningEmployeeNames_IfApiResponseParsingFails_thenThrowsApiResponseJsonParseException() {
        given(getAllEmployeesService.execute(null)).willThrow(new ApiResponseJsonParseException());
        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.getTopTenHighestEarningEmployeeNames());
    }

    @Test
    void whenCreateEmployee_IfSuccess_thenReturnCreatedEmployee() {
        Map<String, Object> input = new HashMap<>();
        input.put("name", "Dhairya Bankar");
        input.put("age", 32);
        input.put("salary", 30000);

        Employee dhairya = dhairya();
        given(createEmployeeApiService.execute(input)).willReturn(dhairya);

        Employee employee = employeeService.createEmployee(input);

        assertThat(employee).isEqualTo(dhairya);
        assertThat(employee).extracting("id").isEqualTo(dhairya.getId());
        assertThat(employee).extracting("employeeName").isEqualTo(dhairya.getEmployeeName());
        assertThat(employee).extracting("employeeAge").isEqualTo(dhairya.getEmployeeAge());
        assertThat(employee).extracting("employeeSalary").isEqualTo(dhairya.getEmployeeSalary());
    }

    @Test
    void whenCreateEmployee_IfInputIsInvalid_thenThrowBadRequestException() {
        Map<String, Object> input = new HashMap<>();
        input.put("name", "Dhairya Bankar");
        input.put("age", 32);

        lenient().when(createEmployeeApiService.execute(input))
                .thenThrow(new BadRequestException(AppTestConstants.BAD_INPUT_EXCEPTION_MESSAGE));

        assertThrows(BadRequestException.class, () -> employeeService.createEmployee(input));
    }

    @Test
    void whenCreateEmployee_IfTooManyRequests_thenThrowsTooManyRequestException() {
        Map<String, Object> input = new HashMap<>();
        input.put("name", "Dhairya Bankar");
        input.put("age", 32);
        input.put("salary", 30000);

        when(createEmployeeApiService.execute(input))
                .thenThrow(new TooManyRequestException());

        assertThrows(TooManyRequestException.class, () -> employeeService.createEmployee(input));
    }

    @Test
    void whenCreateEmployee_IfApiResponseParsingFails_thenThrowsApiResponseJsonParseException() {
        Map<String, Object> input = new HashMap<>();
        input.put("name", "Dhairya Bankar");
        input.put("age", 32);
        input.put("salary", 30000);

        given(createEmployeeApiService.execute(input))
                .willThrow(new ApiResponseJsonParseException());

        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.createEmployee(input));
    }
    
    @Test
    void whenDeleteEmployee_IfSuccess_thenGetDeletedEmployeeName() {
        Employee dhairya = dhairya();
        Optional<Employee> optionalEmployee = Optional.of(dhairya);
        final String empId = dhairya.getId().toString();
        given(getEmployeeByIdService.execute(empId)).willReturn(optionalEmployee);
        given(deleteEmployeeService.execute(empId)).willReturn(dhairya.getEmployeeName());

        assertThat(employeeService.deleteEmployee(empId)).isEqualTo(dhairya.getEmployeeName());
    }

    @Test
    void whenDeleteEmployee_IfServerReturnError_thenThrowsInternalServerError() {
        Employee dhairya = dhairya();
        Optional<Employee> optionalEmployee = Optional.of(dhairya);
        final String empId = dhairya.getId().toString();
        given(getEmployeeByIdService.execute(empId)).willReturn(optionalEmployee);
        given(deleteEmployeeService.execute(empId)).willThrow(new InternalServerError(500));

        assertThrows(InternalServerError.class, () -> employeeService.deleteEmployee(empId));
    }

    @Test
    void whenDeleteEmployee_IfTooManyRequests_thenThrowsTooManyRequestException() {
        Employee dhairya = dhairya();
        Optional<Employee> optionalEmployee = Optional.of(dhairya);
        final String empId = dhairya.getId().toString();
        given(getEmployeeByIdService.execute(empId)).willReturn(optionalEmployee);
        given(deleteEmployeeService.execute(empId)).willThrow(new TooManyRequestException());

        assertThrows(TooManyRequestException.class, () -> employeeService.deleteEmployee(empId));
    }

    @Test
    void whenDeleteEmployee_IfApiResponseParsingFails_thenThrowsApiResponseJsonParseException() {
        Employee dhairya = dhairya();
        Optional<Employee> optionalEmployee = Optional.of(dhairya);
        final String empId = dhairya.getId().toString();
        given(getEmployeeByIdService.execute(empId)).willReturn(optionalEmployee);
        given(deleteEmployeeService.execute(empId)).willThrow(new ApiResponseJsonParseException());

        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.deleteEmployee(empId));
    }
}