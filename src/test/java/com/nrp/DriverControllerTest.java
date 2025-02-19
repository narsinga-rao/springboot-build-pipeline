package com.nrp;

import com.nrp.controller.DriverController;
import com.nrp.domainobject.CarDO;
import com.nrp.domainobject.DriverDO;
import com.nrp.domainvalue.EngineType;
import com.nrp.domainvalue.OnlineStatus;
import com.nrp.exception.CarAlreadyInUseException;
import com.nrp.service.car.CarService;
import com.nrp.service.driver.DriverService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(value = DriverController.class)
public class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DriverService driverService;

    @MockitoBean
    private CarService carService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup()
    {
        //Init MockMvc Object and build
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    DriverDO mockDriver = new DriverDO("user01", "password");

    DriverDO mockDriver2 = new DriverDO("user02", "password");

    DriverDO mockDriver3 = new DriverDO("user03", "password");

    CarDO mockCarResult = new CarDO("5432PW", 4, false, 10, EngineType.GAS, "MERCEDES");

    @Test
    public void findDriver() throws Exception {

        Mockito.when(driverService.find(Mockito.anyLong())).thenReturn(mockDriver);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/v1/drivers/1").accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        System.out.println(result.getResponse());
        String expected = "{\"username\":\"user01\",\"password\":\"password\"}";

        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
    }

    @Test
    public void findDriversFilteringByUsername() throws Exception {
        List<DriverDO> drivers = new ArrayList<>();
        drivers.add(mockDriver);
        drivers.add(mockDriver2);
        Mockito.when(driverService.findAll()).thenReturn(drivers);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/v1/drivers?username=user02").accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        System.out.println(result.getResponse());
        String expected = "[{\"username\":\"user02\",\"password\":\"password\"}]";

        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
    }

    @Test
    public void findDriversFilteringByOnlineStatusOnline() throws Exception {
        List<DriverDO> drivers = new ArrayList<>();
        drivers.add(mockDriver);
        drivers.add(mockDriver2);
        drivers.add(mockDriver3);
        mockDriver3.setOnlineStatus(OnlineStatus.ONLINE);

        Mockito.when(driverService.findAll()).thenReturn(drivers);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/v1/drivers?onlineStatus=ONLINE").accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        System.out.println(result.getResponse());
        String expected = "[{\"username\":\"user03\",\"password\":\"password\"}]";

        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
    }

    @Test
    public void findDriversFilteringByOnlineStatusOffline() throws Exception {
        List<DriverDO> drivers = new ArrayList<>();
        drivers.add(mockDriver);
        drivers.add(mockDriver2);
        drivers.add(mockDriver3);
        mockDriver3.setOnlineStatus(OnlineStatus.ONLINE);

        Mockito.when(driverService.findAll()).thenReturn(drivers);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/v1/drivers?onlineStatus=OFFLINE").accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        System.out.println(result.getResponse());
        String expected = "[{\"username\":\"user01\",\"password\":\"password\"},{\"username\":\"user02\",\"password\":\"password\"}]";

        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
    }

    @Test
    public void createDriver() throws Exception {

        DriverDO mockDriverResult = new DriverDO("user","password");
        mockDriverResult.setId(9L);

        Mockito.when(driverService.create(Mockito.any(DriverDO.class))).thenReturn(mockDriverResult);

        String exampleDriver = "{\"username\":\"user\",\"password\":\"password\"}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/v1/drivers")
                .accept(MediaType.APPLICATION_JSON).content(exampleDriver)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    public void selectCar() throws Exception {

        DriverDO mockDriverResult = new DriverDO("user","password");

        Mockito.when(driverService.find(1L)).thenReturn(mockDriverResult);
        Mockito.when(carService.find("5432PW")).thenReturn(mockCarResult);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/v1/drivers/1/car/5432PW")
                //.header("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void selectAnAlreadySelectedCar() throws Exception {

        Mockito.doThrow(CarAlreadyInUseException.class).when(carService).addDriver(1L, "5432PW");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/v1/drivers/1/car/5432PW")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("This car is already in use by another driver.", response.getErrorMessage());
    }

    @Test
    public void deselectCar() throws Exception {

        DriverDO mockDriverResult = new DriverDO("user","password");
        CarDO mockCarResult = new CarDO("5432PW", 4, false, 10, EngineType.GAS, "MERCEDES");

        Mockito.when(driverService.find(1L)).thenReturn(mockDriverResult);
        Mockito.when(carService.find("5432PW")).thenReturn(mockCarResult);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/v1/drivers/1/car/5432PW")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

}
