package clientfeedbackservice.integrationtest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import rateservice.RateServiceApplication;
import rateservice.entity.ClientFeedback;
import rateservice.repository.ClientFeedbackRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = RateServiceApplication.class)
@ActiveProfiles("test")
public class ClientFeedbackRepositoryTest {

    @Autowired
    private ClientFeedbackRepository clientFeedbackRepository;

    @Test
    void shouldFindClientFeedbacksByUserId() {
        ClientFeedback feedback1 = new ClientFeedback();
        feedback1.setRate(5L);
        feedback1.setComment("EXCELLENT");
        feedback1.setCleanInterior(true);
        feedback1.setSafeDriving(true);
        feedback1.setNiceMusic(true);
        feedback1.setUserId(1L);
        feedback1.setRideId(100L);
        clientFeedbackRepository.save(feedback1);

        ClientFeedback feedback2 = new ClientFeedback();
        feedback2.setRate(4L);
        feedback2.setComment("GOOD");
        feedback2.setCleanInterior(false);
        feedback2.setSafeDriving(true);
        feedback2.setNiceMusic(true);
        feedback2.setUserId(1L);
        feedback2.setRideId(101L);
        clientFeedbackRepository.save(feedback2);

        Page<ClientFeedback> result = clientFeedbackRepository.findAllByUserId(1L, PageRequest.of(0, 10));

        assertThat(result)
                .isNotNull();
        assertThat(result.getTotalElements())
                .isEqualTo(2);
        assertThat(result.getContent())
                .extracting("comment")
                .contains("EXCELLENT", "GOOD");
    }

    @Test
    void shouldFindClientFeedbacksByRideIds() {
        ClientFeedback feedback1 = new ClientFeedback();
        feedback1.setRate(3L);
        feedback1.setComment("MIDDLE");
        feedback1.setCleanInterior(true);
        feedback1.setSafeDriving(true);
        feedback1.setNiceMusic(false);
        feedback1.setUserId(2L);
        feedback1.setRideId(200L);
        clientFeedbackRepository.save(feedback1);

        ClientFeedback feedback2 = new ClientFeedback();
        feedback2.setRate(2L);
        feedback2.setComment("BAD");
        feedback2.setCleanInterior(false);
        feedback2.setSafeDriving(false);
        feedback2.setNiceMusic(false);
        feedback2.setUserId(3L);
        feedback2.setRideId(201L);
        clientFeedbackRepository.save(feedback2);

        List<ClientFeedback> resultList = clientFeedbackRepository.findByRideIdIn(List.of(200L, 201L));

        assertThat(resultList)
                .isNotNull()
                .hasSize(2);
        assertThat(resultList)
                .extracting("rideId")
                .containsExactlyInAnyOrder(200L, 201L);
    }

    @Test
    void shouldFindClientFeedbackByRideIdAndUserId() {
        ClientFeedback feedback = new ClientFeedback();
        feedback.setRate(4L);
        feedback.setComment("GOOD");
        feedback.setCleanInterior(true);
        feedback.setSafeDriving(true);
        feedback.setNiceMusic(true);
        feedback.setUserId(5L);
        feedback.setRideId(300L);
        clientFeedbackRepository.save(feedback);

        Optional<ClientFeedback> foundFeedback = clientFeedbackRepository.findByRideIdAndUserId(300L, 5L);
        assertThat(foundFeedback)
                .isPresent();
        assertThat(foundFeedback.get().getComment())
                .isEqualTo("GOOD");

        Optional<ClientFeedback> notFoundFeedback = clientFeedbackRepository.findByRideIdAndUserId(300L, 99L);
        assertThat(notFoundFeedback)
                .isNotPresent();
    }
}
