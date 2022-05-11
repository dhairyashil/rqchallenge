package com.example.rqchallenge.employee.controller;

import com.example.rqchallenge.employee.dto.Employee;
import com.example.rqchallenge.employee.exception.*;
import com.example.rqchallenge.employee.exception.handler.ApiExceptionHandler;
import com.example.rqchallenge.employee.service.IEmployeeService;
import com.example.rqchallenge.employee.util.AppTestConstants;
import com.example.rqchallenge.employee.util.AppTestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.rqchallenge.employee.util.AppTestHelper.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = IEmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private IEmployeeService employeeService;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(IEmployeeController.class)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void whenGetAllEmployees_thenReturnJsonArray() throws Exception {
        Employee dhairya = dhairya();
        Employee dhiraj = dhiraj();
        Employee rajesh = rajesh();
        Employee ajay = ajay();
        Employee john = john();
        Employee tejal = tejal();

        List<Employee> allEmployees = List.of(dhairya, rajesh, ajay, john, dhiraj, tejal);

        given(employeeService.getAllEmployees()).willReturn(allEmployees);

        mockMvc.perform(get("/employee")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].employee_name", is(dhairya.getEmployeeName())))
                .andExpect(jsonPath("$[0].employee_salary", is(dhairya.getEmployeeSalary())))
                .andExpect(jsonPath("$[0].employee_age", is(dhairya.getEmployeeAge())));;
    }

    @Test
    void whenGetAllEmployees_throwsTooManyRequestException() throws Exception {
        given(employeeService.getAllEmployees()).willThrow(new TooManyRequestException());

        mockMvc.perform(get("/employee")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.status", is(429)));
    }

    @Test
    void whenGetAllEmployees_throwsApiResponseJsonParseException() throws Exception {
        given(employeeService.getAllEmployees()).willThrow(new ApiResponseJsonParseException());

        mockMvc.perform(get("/employee")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.status", is(500)));
    }

    @Test
    void whenGetEmployeeById_thenReturnEmployee() throws Exception {
        Employee dhairya = dhairya();

        given(employeeService.getEmployeeById("1")).willReturn(dhairya);

        mockMvc.perform(get("/employee/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name", is(dhairya.getEmployeeName())))
                .andExpect(jsonPath("$.employee_salary", is(dhairya.getEmployeeSalary())))
                .andExpect(jsonPath("$.employee_age", is(dhairya.getEmployeeAge())));;
    }

    @Test
    void whenGetEmployeeById_throwsTooManyRequestException() throws Exception {
        given(employeeService.getEmployeeById("1")).willThrow(new TooManyRequestException());

        mockMvc.perform(get("/employee/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.status", is(429)));
    }

    @Test
    void whenGetEmployeeById_throwsInternalServerError() throws Exception {
        given(employeeService.getEmployeeById("1")).willThrow(new InternalServerError(500));

        mockMvc.perform(get("/employee/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.status", is(500)));
    }

    @Test
    void whenGetEmployeeById_throwsApiResponseJsonParseException() throws Exception {
        given(employeeService.getEmployeeById("1")).willThrow(new ApiResponseJsonParseException());

        mockMvc.perform(get("/employee/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.status", is(500)));
    }

    @Test
    void whenGetEmployeeById_IfNotPresent_throwsEmployeeNotFoundException() throws Exception {
        given(employeeService.getEmployeeById("1")).willThrow(new EmployeeNotFoundException("1"));

        mockMvc.perform(get("/employee/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Employee Not found with id 1" )));
    }

    @Test
    void whenGetEmployeeByNameSearch_thenReturnEmployeeList() throws Exception {

        String searchString = "dh";

        given(employeeService.getEmployeesByNameSearch(searchString))
                .willReturn(List.of(dhairya(), dhiraj()));

        Employee dhairya = dhairya();
        mockMvc.perform(get("/employee/search/{searchString}",searchString)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].employee_name", is(dhairya.getEmployeeName())))
                .andExpect(jsonPath("$[0].employee_salary", is(dhairya.getEmployeeSalary())))
                .andExpect(jsonPath("$[0].employee_age", is(dhairya.getEmployeeAge())));
    }

    @Test
    void whenGetEmployeeByNameSearch_IfNotPresent_thenReturnEmptyEmployeeList() throws Exception {

        String searchString = "dh";

        given(employeeService.getEmployeesByNameSearch(searchString))
                .willReturn(new ArrayList<>());

        mockMvc.perform(get("/employee/search/{searchString}",searchString)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void whenGetEmployeeByNameSearch_IfTooManyRequests_throwsTooManyRequestException() throws Exception {
        String searchString = "ab";

        given(employeeService.getEmployeesByNameSearch(searchString)).willThrow(new TooManyRequestException());

        mockMvc.perform(get("/employee/search/{searchString}",searchString)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.status", is(429)));
    }

    @Test
    void whenGetEmployeeByNameSearch_IfApiResponseParseFails_throwsApiResponseJsonParseException() throws Exception {
        String searchString = "ab";

        given(employeeService.getEmployeesByNameSearch(searchString)).willThrow(new ApiResponseJsonParseException());

        mockMvc.perform(get("/employee/search/{searchString}",searchString)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.status", is(500)));
    }

    @Test
    void whenGetHighestSalaryOfEmployee_thenReturnHighestSalary() throws Exception {

        given(employeeService.getHighestSalaryOfEmployees()).willReturn(dhiraj().getEmployeeSalary());

        mockMvc.perform(get("/employee/highestSalary"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("330000"));
    }

    @Test
    void whenGetHighestSalaryOfEmployee_IfTooManyRequest_throwsTooManyRequestsException() throws Exception {

        given(employeeService.getHighestSalaryOfEmployees()).willThrow(new TooManyRequestException());

        mockMvc.perform(get("/employee/highestSalary"))
                .andDo(print())
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.status", is(429)));
    }

    @Test
    void whenGetHighestSalaryOfEmployee_IfApiResponseParseFails_throwsApiResponseParseException() throws Exception {

        given(employeeService.getHighestSalaryOfEmployees()).willThrow(new ApiResponseJsonParseException());

        mockMvc.perform(get("/employee/highestSalary"))
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.status", is(500)));
    }

    @Test
    void whenGetTopTenHighestEarningEmployeeNames_thenReturnListOfEmployeeName() throws Exception {

        given(employeeService.getTopTenHighestEarningEmployeeNames()).willReturn(AppTestHelper.topTenEmployeeNames());

        mockMvc.perform(get("/employee/topTenHighestEarningEmployeeNames")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$.[0]", is("Paul Byrd")))
                .andExpect(jsonPath("$.[9]", is("Tiger Nixon")));
    }

    @Test
    void whenGetTopTenHighestEarningEmployeeNames_IfTooManyRequests_throwTooManyRequestsException() throws Exception {

        given(employeeService.getTopTenHighestEarningEmployeeNames())
                .willThrow(new TooManyRequestException());

        mockMvc.perform(get("/employee/topTenHighestEarningEmployeeNames"))
                .andDo(print())
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.status", is(429)));
    }

    @Test
    void whenCreateEmployee_thenReturnCreatedEmployee() throws Exception {
        Map<String, Object> input = new HashMap<>();
        input.put("name", "Dhairya Bankar");
        input.put("age", 32);
        input.put("salary", 30000);

        given(employeeService.createEmployee(input)).willReturn(dhairya());

        mockMvc.perform(post("/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.employee_name", is(dhairya().getEmployeeName())))
                        .andExpect(jsonPath("$.employee_salary", is(dhairya().getEmployeeSalary())));
    }

    @Test
    void whenCreateEmployee_IfInputIsInvalid_thenThrowBadRequestException() throws Exception {
        Map<String, Object> input = new HashMap<>();
        input.put("name", "Dhairya Bankar");
        input.put("salary", 30000);

        given(employeeService.createEmployee(input)).willThrow(new BadRequestException(AppTestConstants.BAD_INPUT_EXCEPTION_MESSAGE));

        mockMvc.perform(post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is(AppTestConstants.BAD_INPUT_EXCEPTION_MESSAGE)));
    }

    @Test
    void whenDeleteEmployee_thenReturnSuccess() throws Exception {
        String id = "2";
        given(employeeService.deleteEmployee(id)).willReturn(AppTestConstants.SUCCESS);

        mockMvc.perform(delete("/employee/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(AppTestConstants.SUCCESS));
    }

    @Test
    void whenDeleteEmployee_IfTooManyRequests_thenThrowsTooManyRequests() throws Exception {
        String id = "2";
        given(employeeService.deleteEmployee(id)).willThrow(new TooManyRequestException());

        mockMvc.perform(delete("/employee/{id}", id))
                .andDo(print())
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.status", is(429)));
    }

    @Test
    void whenDeleteEmployee_IfServerReturnError_thenThrowsInternalServerError() throws Exception {
        String id = "2";
        given(employeeService.deleteEmployee(id)).willThrow(new InternalServerError(500));

        mockMvc.perform(delete("/employee/{id}", id))
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.status", is(500)));
    }

    @Test
    void whenDeleteEmployee_IfResponseJsonParsingFails_thenThrowsApiResponseJsonParseException() throws Exception {
        String id = "2";
        given(employeeService.deleteEmployee(id)).willThrow(new ApiResponseJsonParseException());

        mockMvc.perform(delete("/employee/{id}", id))
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.status", is(500)));
    }
}
