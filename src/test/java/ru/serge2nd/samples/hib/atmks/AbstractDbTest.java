package ru.serge2nd.samples.hib.atmks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateOptimisticLockingFailureException;
import ru.serge2nd.samples.hib.atmks.config.TxSessionTemplate;
import ru.serge2nd.samples.hib.atmks.data.Route;
import ru.serge2nd.samples.hib.atmks.data.RouteStop;
import ru.serge2nd.samples.hib.atmks.data.Station;
import ru.serge2nd.samples.hib.atmks.data.StayInterval;

import java.time.LocalTime;

import static java.util.Comparator.comparingInt;
import static javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.jupiter.api.Assertions.fail;
import static ru.serge2nd.test.matcher.ArrayMatch.isArray;
import static ru.serge2nd.test.matcher.AssertThat.assertThat;
import static ru.serge2nd.test.matcher.CommonMatch.equalTo;
import static ru.serge2nd.test.matcher.CommonMatch.fails;

abstract class AbstractDbTest {
    @Autowired TxSessionTemplate t;

    @Test void contextLoads() {}

    @Test
    void testSimpleRead() {
        // WHEN
        Route route = t.fromTxSession((session, tx) -> session.createQuery("""
                select r from Route r
                left join fetch r.routeStops
                where r.id = :id""", Route.class)
                .setParameter("id", 200L)
                .setReadOnly(true)
                .getSingleResult());
        Object[] routeStops = route.routeStops().sorted(comparingInt(RouteStop::getIndexNumber)).toArray();

        /* THEN */ assertThat(
        routeStops, isArray(
                RouteStop.builder()
                        .indexNumber(50)
                        .station(Station.builder().name("ЛЕСНАЯ").build())
                        .stayInterval(new StayInterval(LocalTime.of(6, 30), LocalTime.of(6, 32)))
                        .build(),
                RouteStop.builder()
                        .indexNumber(100)
                        .station(Station.builder().name("МОСКВА КИЕВСКАЯ").build())
                        .stayInterval(new StayInterval(LocalTime.of(7, 45), LocalTime.of(7, 45)))
                        .build()));
    }

    @Test
    void testSimpleWrite() {
        // GIVEN
        Long id = 300L;
        t.withTxSession((session, tx) ->
                session.merge(Route.builder().id(id)
                        .routeStop(RouteStop.builder()
                                .indexNumber(111)
                                .station((Station)session.merge(Station.builder().id(90L).name("abc").build()))
                                .stayInterval(new StayInterval(LocalTime.of(8, 27), LocalTime.of(8, 28)))
                                .build())
                        .build()));

        // WHEN
        Route route = t.fromTxSession((session, tx) -> session.createQuery("""
                select r from Route r
                left join fetch r.routeStops
                where r.id = :id""", Route.class)
                .setParameter("id", id)
                .setReadOnly(true)
                .getSingleResult());
        RouteStop[] routeStops = route.routeStops().toArray(RouteStop[]::new);

        /* THEN */ assertThat(
        routeStops                        , isArray(RouteStop.builder()
                                                .indexNumber(111)
                                                .station(Station.builder().name("abc").build())
                                                .stayInterval(new StayInterval(LocalTime.of(8, 27), LocalTime.of(8, 28)))
                                                .build()),
        routeStops[0].getStation().getId(), equalTo(90L));
    }

    @Test
    void testSimpleOptimisticLock() {
        // GIVEN
        Route route = Route.builder().build();
        t.withTxSession((session, tx) -> session.save(route));
        t.withTxSession((session, tx) -> session.find(Route.class, route.getId(), OPTIMISTIC_FORCE_INCREMENT));

        // WHEN
        route.addRouteStop(RouteStop.builder()
                .indexNumber(0)
                .station(t.fromTxSession((session, tx) -> session.find(Station.class, 99L)))
                .stayInterval(StayInterval.builder()
                        .arrivalOn(LocalTime.now())
                        .departureOn(LocalTime.now())
                        .build())
                .build());

        // THEN
        assertThat(()->t.withTxSession(((session, tx)->session.merge(route))), fails(HibernateOptimisticLockingFailureException.class));
    }

    @Test
    void testSimpleRollback() {
        // WHEN
        Long id = 19L;
        try { t.withTxSession((session, tx) -> {
            session.merge(Station.builder().id(id).name("not to save").build());
            sleep(2200);
        }); } catch(Exception e) { e.printStackTrace(); }

        // THEN
        assertThat(t.fromTxSession((session, tx)->session.find(Station.class, id)), nullValue());
    }

    static void sleep(long ms) {try {Thread.sleep(ms);} catch(InterruptedException e) {fail("unexpected interruption", e);}}
}
