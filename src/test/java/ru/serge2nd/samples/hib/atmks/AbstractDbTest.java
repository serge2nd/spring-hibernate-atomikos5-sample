package ru.serge2nd.samples.hib.atmks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateOptimisticLockingFailureException;
import org.springframework.transaction.UnexpectedRollbackException;
import ru.serge2nd.samples.hib.atmks.config.TxSessionTemplate;
import ru.serge2nd.samples.hib.atmks.data.Route;
import ru.serge2nd.samples.hib.atmks.data.RouteStop;
import ru.serge2nd.samples.hib.atmks.data.Station;
import ru.serge2nd.samples.hib.atmks.data.StayInterval;

import java.time.LocalTime;

import static java.util.Comparator.comparingInt;
import static javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT;
import static ru.serge2nd.ObjectAssist.throwSneaky;
import static ru.serge2nd.test.match.ArrayMatch.items;
import static ru.serge2nd.test.match.AssertThat.assertThat;
import static ru.serge2nd.test.match.CommonMatch.equalTo;
import static ru.serge2nd.test.match.CommonMatch.fails;
import static ru.serge2nd.test.match.CommonMatch.nullValue;

abstract class AbstractDbTest {
    @Autowired TxSessionTemplate $;

    @Test void contextLoads() {}

    @Test
    void testSimpleRead() {
        // WHEN
        Route route = $.from((session, tx) -> session.createQuery("""
                select r from Route r
                left join fetch r.routeStops
                where r.id = :id""", Route.class)
                .setParameter("id", 200L)
                .setReadOnly(true)
                .getSingleResult());
        Object[] routeStops = route.routeStops().sorted(comparingInt(RouteStop::getIndexNumber)).toArray();

        /* THEN */ assertThat(
        routeStops, items(
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
        $.with((session, tx) ->
            session.merge(Route.builder().id(id)
                .routeStop(RouteStop.builder()
                    .indexNumber(111)
                    .station((Station)session.merge(Station.builder().id(90L).name("abc").build()))
                    .stayInterval(new StayInterval(LocalTime.of(8, 27), LocalTime.of(8, 28)))
                    .build())
                .build()));

        // WHEN
        Route route = $.from((session, tx) -> session.createQuery("""
                select r from Route r
                left join fetch r.routeStops
                where r.id = :id""", Route.class)
                .setParameter("id", id)
                .setReadOnly(true)
                .getSingleResult());
        RouteStop[] routeStops = route.routeStops().toArray(RouteStop[]::new);

        /* THEN */ assertThat(
        routeStops, items(RouteStop.builder()
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
        $.with((session, tx) -> session.save(route));
        $.with((session, tx) -> session.find(Route.class, route.getId(), OPTIMISTIC_FORCE_INCREMENT));

        // WHEN
        route.addRouteStop(RouteStop.builder()
            .indexNumber(0)
            .station($.from((session, tx) -> session.find(Station.class, 99L)))
            .stayInterval(StayInterval.builder()
                .arrivalOn(LocalTime.now())
                .departureOn(LocalTime.now())
                .build())
            .build());

        // THEN
        assertThat(()->$.with(((session, tx)->session.merge(route))), fails(HibernateOptimisticLockingFailureException.class));
    }

    @Test
    void testSimpleRollback() {
        Long id = 19L;
        assertThat(
        ()->$.with((session, tx) -> {
            session.merge(Station.builder().id(id).name("not to save").build());
            sleep(2200);
        }), fails(UnexpectedRollbackException.class),
        $.<Station>from((session, tx)->session.find(Station.class, id)), nullValue("no added row"));
    }

    static void sleep(long ms) { try {Thread.sleep(ms);} catch(Throwable t) {throwSneaky(t);} }
}
