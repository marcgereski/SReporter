CREATE DATABASE LINK ts CONNECT TO arcts_strade IDENTIFIED BY strade USING 'world';



INSERT INTO transit_stat(id, username, account, instrid, serial,
                 orderserial, direct, price, quantity, 
                 remainder, status, created, expired, refnum)
  SELECT
    *
  FROM (
    SELECT
      id,
      (SELECT
      nick
       FROM ts.useritrader@ts u
       WHERE u.id = iduser)           username,
      useracc                         account,
      idinstr                         instrid,
      serial                          serial,
      ordserial                       orderserial,
      ascii(direct)                   direct,
      price                           price,
      volume                          quantity,
      volrest                         remainder,
      ascii(status)                   status,
      ts.int_to_date@ts(date0, time)  created,
      ts.int_to_date@ts(expird, null) expired,
      sign_hex4                       refnum
    FROM arcts.deltransit@ts
    WHERE sign_hex1 IS null
                       AND
                       ts.int_to_date@ts(date0, time) > to_date('18-FEB-2013', 'DD-MON-YYYY')
                       AND
                       ts.int_to_date@ts(date0, time) < to_date('11-JUN-2013', 'DD-MON-YYYY')
    ORDER BY date0
  )
  WHERE rownum < 10000;




INSERT INTO account (id, name)
  SELECT
    a.id,
    a.code
  FROM ts.tradeaccount@ts a
  WHERE a.id IN (SELECT
                   DISTINCT account
                 FROM transit_stat); 




DROP DATABASE LINK ts;