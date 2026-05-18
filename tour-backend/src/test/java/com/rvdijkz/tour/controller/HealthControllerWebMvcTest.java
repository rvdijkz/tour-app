package com.rvdijkz.tour.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rvdijkz.tour.application.port.in.GetHealthUseCase;
import com.rvdijkz.tour.application.port.in.GetSystemInfoUseCase;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration"
})
@AutoConfigureMockMvc
class HealthControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetHealthUseCase getHealthUseCase;

    @MockBean
    private GetSystemInfoUseCase getSystemInfoUseCase;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    @WithMockUser(authorities = "SCOPE_tour.read")
    void getHealthReturnsOkForAuthorizedScope() throws Exception {
        given(getHealthUseCase.getHealth())
                .willReturn(new GetHealthUseCase.HealthState("UP", OffsetDateTime.parse("2026-05-18T10:15:30Z")));

        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getHealthReturnsUnauthorizedWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_tour.read")
    void getInfoReturnsOkForAuthorizedScope() throws Exception {
        given(getSystemInfoUseCase.getSystemInfo())
                .willReturn(new GetSystemInfoUseCase.SystemInfo("tour-backend", "0.0.1-SNAPSHOT"));

        mockMvc.perform(get("/api/v1/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceName").value("tour-backend"))
                .andExpect(jsonPath("$.version").value("0.0.1-SNAPSHOT"));
    }
}




