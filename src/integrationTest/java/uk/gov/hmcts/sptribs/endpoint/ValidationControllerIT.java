package uk.gov.hmcts.sptribs.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import uk.gov.hmcts.sptribs.common.config.WebMvcConfig;
import uk.gov.hmcts.sptribs.common.config.interceptors.RequestInterceptor;
import uk.gov.hmcts.sptribs.endpoint.data.OcrDataValidationRequest;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.AUTH_HEADER_VALUE;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.SERVICE_AUTHORIZATION;
import static uk.gov.hmcts.sptribs.testutil.TestDataHelper.populateKeyValue;
import static uk.gov.hmcts.sptribs.testutil.TestResourceUtil.expectedResponse;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ValidationControllerIT {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestInterceptor requestInterceptor;

    @MockBean
    private WebMvcConfig webMvcConfig;

    @BeforeAll
    static void setUp() {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    @Test
    public void givenInvalidFormTypeThenValidationErrors() throws Exception {
        mockMvc.perform(post("/forms/invalid/validate-ocr")
            .contentType(APPLICATION_JSON)
            .header(SERVICE_AUTHORIZATION, AUTH_HEADER_VALUE)
            .content(OBJECT_MAPPER.writeValueAsString(
                OcrDataValidationRequest.builder()
                    .ocrDataFields(
                        List.of(
                            populateKeyValue("soleApplicantOrApplicant1FirstName", "app1Fname"),
                            populateKeyValue("aSoleApplication", "true")
                        )
                    )
                    .build()))
            .accept(APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResponse("classpath:bulk-scan-d8-validation-error-response.json")));
    }
}
