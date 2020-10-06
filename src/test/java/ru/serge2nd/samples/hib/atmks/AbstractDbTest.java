package ru.serge2nd.samples.hib.atmks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateOptimisticLockingFailureException;
import ru.serge2nd.samples.hib.atmks.config.TxSessionTemplate;
import ru.serge2nd.samples.hib.atmks.data.Route;
import ru.serge2nd.samples.hib.atmks.data.RouteStop;
import ru.serge2nd.samples.hib.atmks.data.Station;
import ru.serge2nd.samples.hib.atmks.data.StayInterval;

import javax.persistence.LockModeType;
import java.time.LocalTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

abstract class AbstractDbTest {
    @Autowired TxSessionTemplate t;

    @Test void contextLoads() {}

    @Test
    void testSimpleRead() {
        // WHEN
        Route route = t.fromTxSession((session, tx) -> session.createQuery(
                "select r from Route r " +
                "left join fetch r.routeStops " +
                "where r.id = :id", Route.class)
                .setParameter("id", 200L)
                .setReadOnly(true)
                .getSingleResult());

        /* THEN */ assertAll(() ->
        assertEquals(2, route.routeStops().count()), () ->
        assertAll(route.routeStops().flatMap(rs -> Stream.of(() ->
        assertSame(route, rs.getRoute()), () -> {
        assertNotNull(rs.getStation());
        assertNotNull(rs.getStation().getName());
        }))));
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
                                .stayInterval(StayInterval.builder()
                                        .arrivalOn(LocalTime.of(8, 27))
                                        .departureOn(LocalTime.of(8, 28))
                                        .build())
                                .build())
                        .build()));

        // WHEN
        Route route = t.fromTxSession((session, tx) -> session.createQuery(
                "select r from Route r " +
                "left join fetch r.routeStops " +
                "where r.id = :id", Route.class)
                .setParameter("id", id)
                .setReadOnly(true)
                .getSingleResult());

        /* THEN */ assertAll(() ->
        assertEquals(1, route.routeStops().count()), () ->
        // AND
        route.routeStops().findFirst().ifPresent(rs -> assertAll(() ->
        assertEquals(111, rs.getIndexNumber()), () -> {
        // AND
        assertNotNull(rs.getStation()); assertAll(() ->
        assertEquals(90L, rs.getStation().getId()), () ->
        assertEquals("abc", rs.getStation().getName())); }, () -> {
        // AND
        assertNotNull(rs.getStayInterval()); assertAll(() ->
        assertEquals(LocalTime.of(8, 27), rs.getStayInterval().getArrivalOn()), () ->
        assertEquals(LocalTime.of(8, 28), rs.getStayInterval().getDepartureOn())); })));
    }

    @Test
    void testSimpleOptimisticLock() {
        // GIVEN
        Route route = Route.builder().build();
        t.withTxSession((session, tx) -> session.save(route));
        t.withTxSession((session, tx) -> session.find(Route.class, route.getId(), LockModeType.OPTIMISTIC_FORCE_INCREMENT));

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
        assertThrows(HibernateOptimisticLockingFailureException.class, () -> t.withTxSession(((session, tx) -> session.merge(route))));
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
        assertNull(t.fromTxSession((session, tx) -> session.find(Station.class, id)));
    }

    static void sleep(long ms) {try {Thread.sleep(ms);} catch(InterruptedException e) {fail("unexpected interruption", e);}}
}
