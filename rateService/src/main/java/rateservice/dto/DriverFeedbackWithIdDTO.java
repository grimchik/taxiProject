package rateservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverFeedbackWithIdDTO {
    private Long id;
    private Long rate;
    private String comment;
    private Boolean politePassenger;
    private Boolean cleanPassenger;
    private Boolean punctuality;
}
