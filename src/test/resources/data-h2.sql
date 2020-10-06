INSERT INTO
    STATIONS ( ID,     STATION_NAME  )
    VALUES   ( 98, 'МОСКВА КИЕВСКАЯ' ),
             ( 99,          'ЛЕСНАЯ' );

INSERT INTO
    ROUTES (  ID, ROUTE_VERSION )
    VALUES ( 200,             1 );

INSERT INTO
    ROUTE_STOPS ( ROUTE_STOP_ID, ROUTE_ID, INDEX_NUMBER, STATION_ID, ARRIVAL_ON, DEPARTURE_ON )
    VALUES      (             1,      200,           50,         99, 60 * 06+30,   60 * 06+32 ),
                (             2,      200,          100,         98, 60 * 07+45,   60 * 07+45 );
