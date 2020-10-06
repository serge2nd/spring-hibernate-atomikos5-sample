package ru.serge2nd.samples.hib.atmks.data;

import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = PROTECTED)
public class RouteStop {
    /** Route */
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude @ToString.Exclude
    protected Route route;

    /** Station */
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "STATION_ID")
    protected Station station;

    /** Index number */
    @NotNull @Min(0)
    @Column(name = "INDEX_NUMBER", nullable = false)
    protected Integer indexNumber;

    /** Stay interval */
    @NotNull @Valid
    @AttributeOverride(
            name = "arrivalOn",
            column = @Column(name = "ARRIVAL_ON", nullable = false))
    @AttributeOverride(
            name = "departureOn",
            column = @Column(name = "DEPARTURE_ON", nullable = false))
    @Convert(attributeName = "arrivalOn", converter = TimeConverter.class)
    @Convert(attributeName = "departureOn", converter = TimeConverter.class)
    protected StayInterval stayInterval;

    public static boolean sameIndexes(RouteStop rs1, RouteStop rs2) {
        return rs1 != null && rs2 != null &&
                rs1.getIndexNumber() != null &&
                rs2.getIndexNumber() != null &&
                rs1.getIndexNumber().equals(rs2.getIndexNumber());
    }
}
