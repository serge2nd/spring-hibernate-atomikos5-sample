INSERT INTO
    STATIONS ( ID,     STATION_NAME  ) WITH R AS (
    SELECT     98, 'МОСКВА КИЕВСКАЯ'   FROM DUAL UNION ALL
    SELECT     99,          'ЛЕСНАЯ'   FROM DUAL
) SELECT * FROM R;

INSERT INTO
    ROUTES (  ID, ROUTE_VERSION ) WITH R AS (
    SELECT   200,             1   FROM DUAL
) SELECT * FROM R;

INSERT INTO
    ROUTE_STOPS ( ROUTE_STOP_ID, ROUTE_ID, INDEX_NUMBER, STATION_ID, ARRIVAL_ON, DEPARTURE_ON ) WITH R AS (
    SELECT                    1,      200,           50,         99, 60 * 06+30,   60 * 06+32   FROM DUAL UNION ALL
    SELECT                    2,      200,          100,         98, 60 * 07+45,   60 * 07+45   FROM DUAL
) SELECT * FROM R;
