package com.nrp;

import com.nrp.controller.CarController;
import com.nrp.domainobject.CarDO;
import com.nrp.domainvalue.EngineType;
import com.nrp.service.car.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;


@WebMvcTest(value = CarController.class)
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarService carService;

    private static final String LICENSE_PLATE = "546PW";

    private CarDO carDOResult = new CarDO(LICENSE_PLATE, 4, false, 10, EngineType.GAS, "MERCEDES");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup()
    {
        //Init MockMvc Object and build
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void createCar() throws Exception {

        Mockito.when(carService.create(Mockito.any(CarDO.class))).thenReturn(carDOResult);

        String exampleCar = "{\"licensePlate\":\"546PW\",\"seatCount\":\"4\",\"convertible\":\"false\",\"rating\":\"10\",\"engineType\":\"GAS\",\"manufacturer\":\"MERCEDES\"}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/v1/cars")
                .accept(MediaType.APPLICATION_JSON).content(exampleCar)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    public void getCar() throws Exception {

        Mockito.when(carService.find(LICENSE_PLATE)).thenReturn(carDOResult);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/v1/cars/"+LICENSE_PLATE).accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        System.out.println(result.getResponse());
        String expected = "{\"licensePlate\":\"546PW\",\"seatCount\":4,\"convertible\":false,\"rating\":10,\"engineType\":\"GAS\",\"manufacturer\":\"MERCEDES\"}";

        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
    }

}
