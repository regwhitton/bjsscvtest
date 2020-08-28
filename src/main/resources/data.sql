-- Populate in-memory H2 database with some data for demonstration.

INSERT INTO CV (ID,VERSION,FIRST_NAME,PREFERRED_FIRST_NAME,MIDDLE_NAMES,SURNAME,
DATE_OF_BIRTH,EMAIL,TELEPHONE,ADDRESS_LINE1,ADDRESS_LINE2,CITY,COUNTY,POSTAL_CODE) VALUES
(100, 3, 'James', 'Jim', 'T', 'Kirk', DATE '1985-08-27', 'jim.kirk@example.com', '01999 999999',
'33 Hard Street', 'Tourbon', 'Cheam', 'North Ruddles', 'TE99 3JJ'),
(101, 1, 'Fredrick', 'Fred', 'James John', 'Douglas', DATE '1992-07-21', 'fred.douglas@example.com', '01888 888888',
'3 Shortbread Street', 'Chomsy', 'Flint', 'West Leamshire', 'FT88 1ER');

INSERT INTO SKILL (ID,CV_ID,SKILL)
VALUES
(102, 100, 'Navigation Certificate'),
(103, 100, 'Social Engineer'),
(104, 101, '5 Star Food Safety Certificate'),
(105, 101, 'Speciality Baker');

ALTER SEQUENCE "HIBERNATE_SEQUENCE" RESTART WITH 200;