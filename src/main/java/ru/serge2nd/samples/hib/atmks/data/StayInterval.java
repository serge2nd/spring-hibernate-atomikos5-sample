package ru.serge2nd.samples.hib.atmks.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StayInterval {
    @NotNull
    protected LocalTime arrivalOn;
    @NotNull
    protected LocalTime departureOn;
}
