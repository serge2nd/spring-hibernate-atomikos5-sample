package ru.serge2nd.samples.hib.atmks.data;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static lombok.AccessLevel.PROTECTED;
import static ru.serge2nd.samples.hib.atmks.data.Helpers.STRMAXLEN;

@Entity
@Table(name = Station.TABLE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = PROTECTED)
public class Station implements Identifiable<Long> {
    public static final String TABLE = "STATIONS";

    /** Station ID */
    @Id @GeneratedValue(generator = Station.TABLE)
    @EqualsAndHashCode.Exclude
    protected Long id;

    /** Station name */
    @NotBlank @Size(max = STRMAXLEN)
    @Column(name = "STATION_NAME", length = STRMAXLEN, nullable = false)
    protected String name;
}
