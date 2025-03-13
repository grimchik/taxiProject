package rateservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientFeedbackWithIdDTO {
    private Long id;
    private Long rate;
    private String comment;
    private Boolean cleanInterior;
    private Boolean safeDriving;
    private Boolean niceMusic;
    private Long userId;
    private Long rideId;
}
