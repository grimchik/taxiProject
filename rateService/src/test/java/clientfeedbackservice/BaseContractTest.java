package clientfeedbackservice;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import rateservice.RateServiceApplication;
import rateservice.controller.ClientFeedbackController;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {RateServiceApplication.class, ClientFeedbackController.class})
@DirtiesContext
@AutoConfigureMessageVerifier
public abstract class BaseContractTest {
    private static final Logger log = LoggerFactory.getLogger(BaseContractTest.class);
    @Autowired
    private ClientFeedbackController clientFeedbackController;

    @BeforeEach
    public void setup() {
        log.info("Запуск setup() в BaseContractTest");
        StandaloneMockMvcBuilder standaloneMockMvcBuilder =
                MockMvcBuilders.standaloneSetup(clientFeedbackController);
        RestAssuredMockMvc.mockMvc(standaloneMockMvcBuilder.build());
    }
}