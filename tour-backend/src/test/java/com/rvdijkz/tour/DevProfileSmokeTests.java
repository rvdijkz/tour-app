package com.rvdijkz.tour;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class DevProfileSmokeTests {

    private static final String TEST_DATASOURCE_URL = "jdbc:h2:mem:tourdb-dev-smoke;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    private static final String TEST_DATASOURCE_DRIVER = "org.h2.Driver";
    private static final String TEST_DATASOURCE_USERNAME = "sa";
    private static final String TEST_DATASOURCE_PASSWORD = "";

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void registerDevProfileTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> TEST_DATASOURCE_URL);
        registry.add("spring.datasource.driver-class-name", () -> TEST_DATASOURCE_DRIVER);
        registry.add("spring.datasource.username", () -> TEST_DATASOURCE_USERNAME);
        registry.add("spring.datasource.password", () -> TEST_DATASOURCE_PASSWORD);
        registry.add("spring.liquibase.url", () -> TEST_DATASOURCE_URL);
        registry.add("spring.liquibase.user", () -> TEST_DATASOURCE_USERNAME);
        registry.add("spring.liquibase.password", () -> TEST_DATASOURCE_PASSWORD);
    }

    @Test
    void devProfileStartsAndServesHealthEndpoint() throws Exception {
        mockMvc.perform(get("/health"))
            .andExpect(status().isOk());
    }

    @Test
    void devProfileDoesNotChallengePlayerEndpointWhenNoTokenIsProvided() throws Exception {
        int status = mockMvc.perform(get("/editions/{editionId}/entries/current", 1L))
            .andReturn()
            .getResponse()
            .getStatus();

        assertThat(status).isNotIn(401, 403);
    }
}

