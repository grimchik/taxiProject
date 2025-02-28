package rideservice.dto;

import lombok.Getter;
import lombok.Setter;
import rideservice.enums.Status;
import rideservice.enumvalidation.ValueOfEnum;

@Getter
@Setter
public class StatusDTO {
    @ValueOfEnum(enumClass = Status.class)
    private String status;
}
