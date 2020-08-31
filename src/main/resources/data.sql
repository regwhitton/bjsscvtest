-- Populate in-memory H2 database with some data for demonstration.

INSERT INTO CV (ID,VERSION,FIRST_NAME,PREFERRED_FIRST_NAME,MIDDLE_NAMES,SURNAME,
DATE_OF_BIRTH,EMAIL,TELEPHONE,ADDRESS_LINE1,ADDRESS_LINE2,CITY,COUNTY,POSTAL_CODE) VALUES
(100, 3, 'James', 'Jim', 'Tiberius', 'Kirk', DATE '1985-08-27', 'jim.kirk@example.com', '01999 999999',
'33 Hard Street', 'Tourbon', 'Cheam', 'North Ruddles', 'TE99 3JJ'),
(101, 1, 'Fredrick', 'Fred', 'James John', 'Douglas', DATE '1992-07-21', 'fred.douglas@example.com', '01888 888888',
'3 Shortbread Street', 'Chomsy', 'Flint', 'West Leamshire', 'FT88 1ER');

INSERT INTO SKILL (ID,CV_ID,SKILL)
VALUES
(200, 100, 'Navigation'),
(201, 100, 'Starship Command'),
(202, 101, '5 Star Food Safety Certificate'),
(203, 101, 'Speciality Baker');

INSERT INTO EMPLOYMENT (ID,VERSION,CV_ID,FROM_DATE,UNTIL_DATE,COMPANY,POSITION,SUMMARY)
VALUES
(300, 1, 100, DATE '2018-10-01', NULL, 'Starfleet', 'Captain of USS Enterprise',
  'Responsible for Starfleet Operations, the safety of the ship and crew and representing the Fedration in the distant galaxy.'),
(301, 2, 100, DATE '1998-09-20', DATE '2018-09-30', 'Starfleet', 'Captain of USS Farragut', 'Responsible for Planetary Surveys.');

ALTER SEQUENCE "HIBERNATE_SEQUENCE" RESTART WITH 1000;