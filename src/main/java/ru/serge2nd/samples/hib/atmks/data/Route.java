package ru.serge2nd.samples.hib.atmks.data;

import lombok.*;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Stream;

import static lombok.AccessLevel.PROTECTED;
import static ru.serge2nd.samples.hib.atmks.data.Helpers.has;
import static ru.serge2nd.samples.hib.atmks.data.RouteStop.sameIndexes;

@Entity
@Table(name = Route.TABLE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = PROTECTED)
public class Route implements Identifiable<Long> {
    public static final String TABLE = "ROUTES";
    public static final String ROUTE_STOP_TABLE = "ROUTE_STOPS";

    //region ID & version

    @Id @GeneratedValue(generator = Route.TABLE)
    @EqualsAndHashCode.Exclude
    protected Long id;

    @Version
    @Column(name = "ROUTE_VERSION")
    @EqualsAndHashCode.Exclude
    protected Long version;
    //endregion

    //region Route stops

    @ElementCollection @Getter(PROTECTED) @Setter(PROTECTED)
    @CollectionTable(
            name = ROUTE_STOP_TABLE,
            joinColumns = @JoinColumn(name = "ROUTE_ID"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"ROUTE_ID", "INDEX_NUMBER"}))
    @CollectionId(
            columns = @Column(name = "ROUTE_STOP_ID"),
            type = @Type(type = "long"), generator = ROUTE_STOP_TABLE)
    @OrderBy("INDEX_NUMBER asc")
    protected List<@Valid RouteStop> routeStops = new ArrayList<>();

    public Stream<RouteStop> routeStops()                           { return getRouteStops().stream(); }
    public RouteStop         addRouteStop(@NonNull RouteStop rs)    { return doAddRouteStop(this, rs); }
    public RouteStop         removeRouteStop(@NonNull RouteStop rs) { return doRemoveRouteStop(this, rs); }
    public boolean           contains(@NonNull RouteStop rs)        { return has(e -> sameIndexes(rs, e), getRouteStops()::iterator); }
    //endregion

    //region Private helpers

    private static RouteStop doAddRouteStop(Route r, RouteStop rs) {
        if (r.contains(rs)) throw errRouteStopExists(rs);
        r.getRouteStops().add(rs); rs.setRoute(r); return rs;
    }
    private static RouteStop doRemoveRouteStop(Route r, RouteStop rs) {
        for (var it = r.getRouteStops().iterator(); it.hasNext();)
            if (sameIndexes(rs, it.next())) {
                it.remove(); rs.setRoute(null); return rs; }
        throw errRouteStopNotFound(rs);
    }
    //endregion

    /**
     * Extending the Lombok-generated builder to inject bidirectional associations properly
     */
    public static class RouteBuilder {
        // lombok-generated code...

        private final List<RouteStop> routeStops = new ArrayList<>();

        public RouteBuilder routeStop(RouteStop routeStop) {
            routeStops.add(routeStop);
            return this;
        }
        public RouteBuilder routeStops(List<RouteStop> routeStops) {
            this.routeStops.clear();
            this.routeStops.addAll(routeStops);
            return this;
        }

        public Route build() {
            var route = new Route(id, version, new ArrayList<>());
            for (RouteStop routeStop : routeStops) route.addRouteStop(routeStop);
            return route;
        }
    }

    public static IllegalArgumentException errRouteStopExists(RouteStop rs) { return new IllegalArgumentException("route stop " + rs.getIndexNumber() + " exists"); }
    public static IllegalArgumentException errRouteStopNotFound(RouteStop rs) { return new IllegalArgumentException("route stop " + rs.getIndexNumber() + " not found"); }
}
