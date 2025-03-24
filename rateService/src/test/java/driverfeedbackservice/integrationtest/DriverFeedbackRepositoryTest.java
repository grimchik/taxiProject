package driverfeedbackservice.integrationtest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import rateservice.RateServiceApplication;
import rateservice.entity.DriverFeedback;
import rateservice.repository.DriverFeedbackRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = RateServiceApplication.class)
@ActiveProfiles("test")
public class DriverFeedbackRepositoryTest {

    @Autowired
    private DriverFeedbackRepository driverFeedbackRepository;

    @Test
    void shouldFindClientFeedbacksByUserId() {
        DriverFeedback feedback1 = new DriverFeedback();
        feedback1.setRate(5L);
        feedback1.setComment("EXCELLENT");
        feedback1.setPunctuality(true);
        feedback1.setPolitePassenger(true);
        feedback1.setCleanPassenger(true);
        feedback1.setDriverId(1L);
        feedback1.setRideId(100L);
        driverFeedbackRepository.save(feedback1);

        DriverFeedback feedback2 = new DriverFeedback();
        feedback2.setRate(4L);
        feedback2.setComment("GOOD");
        feedback2.setPunctuality(false);
        feedback2.setPolitePassenger(true);
        feedback2.setCleanPassenger(true);
        feedback2.setDriverId(1L);
        feedback2.setRideId(100L);
        driverFeedbackRepository.save(feedback2);

        Page<DriverFeedback> result = driverFeedbackRepository.findAllByDriverId(1L, PageRequest.of(0, 10));

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
        DriverFeedback feedback1 = new DriverFeedback();
        feedback1.setRate(3L);
        feedback1.setComment("MIDDLE");
        feedback1.setPunctuality(true);
        feedback1.setPolitePassenger(true);
        feedback1.setCleanPassenger(false);
        feedback1.setDriverId(2L);
        feedback1.setRideId(200L);
        driverFeedbackRepository.save(feedback1);

        DriverFeedback feedback2 = new DriverFeedback();
        feedback2.setRate(3L);
        feedback2.setComment("BAD");
        feedback2.setPunctuality(false);
        feedback2.setPolitePassenger(false);
        feedback2.setCleanPassenger(false);
        feedback2.setDriverId(3L);
        feedback2.setRideId(201L);
        driverFeedbackRepository.save(feedback2);

        List<DriverFeedback> resultList = driverFeedbackRepository.findByRideIdIn(List.of(200L, 201L));

        assertThat(resultList)
                .isNotNull()
                .hasSize(2);
        assertThat(resultList)
                .extracting("rideId")
                .containsExactlyInAnyOrder(200L, 201L);
    }

    @Test
    void shouldFindClientFeedbackByRideIdAndUserId() {
        DriverFeedback feedback = new DriverFeedback();
        feedback.setRate(4L);
        feedback.setComment("GOOD");
        feedback.setPunctuality(true);
        feedback.setPolitePassenger(true);
        feedback.setCleanPassenger(false);
        feedback.setDriverId(5L);
        feedback.setRideId(300L);
        driverFeedbackRepository.save(feedback);

        Optional<DriverFeedback> foundFeedback = driverFeedbackRepository.findByRideIdAndDriverId(300L, 5L);
        assertThat(foundFeedback)
                .isPresent();
        assertThat(foundFeedback.get().getComment())
                .isEqualTo("GOOD");

        Optional<DriverFeedback> notFoundFeedback = driverFeedbackRepository.findByRideIdAndDriverId(300L, 99L);
        assertThat(notFoundFeedback)
                .isNotPresent();
    }
}
