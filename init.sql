-- create schema
CREATE SCHEMA IF NOT EXISTS dbpersonel;

-- create sequences
CREATE SEQUENCE IF NOT EXISTS dbpersonel.personelTypeSeq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS dbpersonel.buildingSeq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS dbpersonel.floorSeq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS dbpersonel.unitSeq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS dbpersonel.personelSeq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS dbpersonel.gateSeq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS dbpersonel.turnstileSeq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS dbpersonel.turnstileRegistrationLogSeq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS dbpersonel.workingHoursSeq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS dbpersonel.salarySeq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS dbpersonel.userSeq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- create personel type table
CREATE TABLE IF NOT EXISTS dbpersonel.personel_type (
    personel_type_id BIGINT DEFAULT nextval('dbpersonel.personelTypeSeq'::regclass) NOT NULL PRIMARY KEY,
    personel_type_name VARCHAR(255),
    base_salary DOUBLE PRECISION
);

-- create building table
CREATE TABLE IF NOT EXISTS dbpersonel.building (
    building_id BIGINT DEFAULT nextval('dbpersonel.buildingSeq'::regclass) NOT NULL PRIMARY KEY,
    building_name VARCHAR(255)
);

-- create floor table
CREATE TABLE IF NOT EXISTS dbpersonel.floor (
    floor_id BIGINT DEFAULT nextval('dbpersonel.floorSeq'::regclass) NOT NULL PRIMARY KEY,
    floor_name VARCHAR(255),
    fk_building_id BIGINT,
    CONSTRAINT fk_floor_building FOREIGN KEY (fk_building_id) REFERENCES dbpersonel.building(building_id)
);

-- create personel table (döngüsel bağımlılığı önlemek için önce unit'siz oluştur)
CREATE TABLE IF NOT EXISTS dbpersonel.personel (
    personel_id BIGINT DEFAULT nextval('dbpersonel.personelSeq'::regclass) NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) NOT NULL,
    fk_personel_type_id BIGINT,
    CONSTRAINT fk_personel_personel_type FOREIGN KEY (fk_personel_type_id) REFERENCES dbpersonel.personel_type(personel_type_id)
);

-- create unit table
CREATE TABLE IF NOT EXISTS dbpersonel.unit (
    unit_id BIGINT DEFAULT nextval('dbpersonel.unitSeq'::regclass) NOT NULL PRIMARY KEY,
    unit_name VARCHAR(255),
    fk_floor_id BIGINT,
    fk_administrator_personel_id BIGINT NULL,
    CONSTRAINT fk_unit_floor FOREIGN KEY (fk_floor_id) REFERENCES dbpersonel.floor(floor_id),
    CONSTRAINT fk_unit_administrator FOREIGN KEY (fk_administrator_personel_id) REFERENCES dbpersonel.personel(personel_id)
);

-- create personel_unit table (many-to-many relationship between personel and unit)
CREATE TABLE IF NOT EXISTS dbpersonel.personel_unit (
    personel_id BIGINT NOT NULL,
    unit_id BIGINT NOT NULL,
    PRIMARY KEY (personel_id, unit_id),
    CONSTRAINT fk_personel_unit_personel FOREIGN KEY (personel_id) REFERENCES dbpersonel.personel(personel_id),
    CONSTRAINT fk_personel_unit_unit FOREIGN KEY (unit_id) REFERENCES dbpersonel.unit(unit_id)
);

-- create gate table
CREATE TABLE IF NOT EXISTS dbpersonel.gate (
    gate_id BIGINT DEFAULT nextval('dbpersonel.gateSeq'::regclass) NOT NULL PRIMARY KEY,
    gate_name VARCHAR(255),
    main_entrance BOOLEAN,
    fk_unit_id BIGINT,
    CONSTRAINT fk_gate_unit FOREIGN KEY (fk_unit_id) REFERENCES dbpersonel.unit(unit_id)
);

-- create turnstile table
CREATE TABLE IF NOT EXISTS dbpersonel.turnstile (
    turnstile_id BIGINT DEFAULT nextval('dbpersonel.turnstileSeq'::regclass) NOT NULL PRIMARY KEY,
    turnstile_name VARCHAR(255),
    fk_gate_id BIGINT,
    CONSTRAINT fk_turnstile_gate FOREIGN KEY (fk_gate_id) REFERENCES dbpersonel.gate(gate_id)
);

-- create turnstile registration log table
CREATE TABLE IF NOT EXISTS dbpersonel.turnstile_registration_log (
    turnstile_registration_log_id BIGINT DEFAULT nextval('dbpersonel.turnstileRegistrationLogSeq'::regclass) NOT NULL PRIMARY KEY,
    fk_personel_id BIGINT,
    fk_turnstile_id BIGINT,
    operation_time TIMESTAMP,
    operation_type VARCHAR(10),
    CONSTRAINT fk_turnstile_reg_log_personel FOREIGN KEY (fk_personel_id) REFERENCES dbpersonel.personel(personel_id),
    CONSTRAINT fk_turnstile_reg_log_turnstile FOREIGN KEY (fk_turnstile_id) REFERENCES dbpersonel.turnstile(turnstile_id)
);

-- create working hours table
CREATE TABLE IF NOT EXISTS dbpersonel.working_hours (
    working_hours_id BIGINT DEFAULT nextval('dbpersonel.workingHoursSeq'::regclass) NOT NULL PRIMARY KEY,
    check_in_time TIME,
    check_out_time TIME,
    fk_personel_type_id BIGINT,
    CONSTRAINT fk_working_hours_personel_type FOREIGN KEY (fk_personel_type_id) REFERENCES dbpersonel.personel_type(personel_type_id)
);

-- create salary table
CREATE TABLE IF NOT EXISTS dbpersonel.salary (
    salary_id BIGINT DEFAULT nextval('dbpersonel.salarySeq'::regclass) NOT NULL PRIMARY KEY,
    fk_personel_id BIGINT,
    salary_month VARCHAR(7), -- YYYY-MM formatında
    base_amount DOUBLE PRECISION,
    late_days INTEGER,
    late_penalty_amount DOUBLE PRECISION,
    final_amount DOUBLE PRECISION,
    calculation_date DATE,
    is_paid BOOLEAN,
    CONSTRAINT fk_salary_personel FOREIGN KEY (fk_personel_id) REFERENCES dbpersonel.personel(personel_id)
);

-- create user table
CREATE TABLE IF NOT EXISTS dbpersonel.user (
    id BIGINT DEFAULT nextval('dbpersonel.userSeq'::regclass) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    enabled BOOLEAN,
    role VARCHAR(50),
    CONSTRAINT username_unique UNIQUE (username),
    CONSTRAINT email_unique UNIQUE (email)
);

-- insert example records - PersonelType
INSERT INTO dbpersonel.personel_type (personel_type_name, base_salary) VALUES 
('Staff', 50000),
('Manager', 70000),
('Security', 30000)
ON CONFLICT DO NOTHING;

-- insert example records - Building
INSERT INTO dbpersonel.building (building_name) VALUES 
('Main Building'),
('Additional Service Building 1'),
('Additional Service Building 2')
ON CONFLICT DO NOTHING;

-- insert example records - Floor
INSERT INTO dbpersonel.floor (floor_name, fk_building_id) VALUES 
('Ground Floor', 1),
('1st Floor', 1),
('2nd Floor', 1),
('3rd Floor', 1),
('4th Floor', 1),
('5th Floor', 1),
('6th Floor', 1),
('7th Floor', 1),
('8th Floor', 1),
('9th Floor', 1),
('10th Floor', 1),
('11th Floor', 1),
('12th Floor', 1),
('13th Floor', 1),
('14th Floor', 1),
('15th Floor', 1),
('16th Floor', 1),
('17th Floor', 1),
('18th Floor', 1),
('19th Floor', 1),
('20th Floor', 1),
('21st Floor', 1),
('Ground Floor', 2),
('1st Floor', 2),
('2nd Floor', 2),
('3rd Floor', 2),
('4th Floor', 2),
('5th Floor', 2),
('6th Floor', 2),
('7th Floor', 2),
('8th Floor', 2),
('9th Floor', 2),
('Ground Floor', 3),
('1st Floor', 3),
('2nd Floor', 3),
('3rd Floor', 3),
('4th Floor', 3),
('5th Floor', 3),
('6th Floor', 3),
('7th Floor', 3),
('8th Floor', 3),
('9th Floor', 3),
('10th Floor', 3),
('11th Floor', 3),
('12th Floor', 3),
('13th Floor', 3)
ON CONFLICT DO NOTHING;

-- insert example records - Personel
INSERT INTO dbpersonel.personel (name, email, fk_personel_type_id) VALUES 
('Arif Özcan', 'zcanarif@gmail(.com', 1),
('Ahmet Arslan', 'ahmetarslan764@gmail.com', 1),
('Ahmet Yılmaz', 'ahmet.yilmaz@company.com', 1),
('Mehmet Demir', 'mehmet.demir@company.com', 1),
('Ayşe Kaya', 'ayse.kaya@company.com', 2),
('Ali Can', 'ali.can@company.com', 3)
ON CONFLICT DO NOTHING;

-- insert example records - Unit
INSERT INTO dbpersonel.unit (unit_name, fk_floor_id, fk_administrator_personel_id) VALUES 
('Accounting', 1, 1),
('Human Resources', 1, 2),
('Software Development', 3, 3),
('Security', 5, 4)
ON CONFLICT DO NOTHING;

-- insert example records - personel_unit (many-to-many)
INSERT INTO dbpersonel.personel_unit (personel_id, unit_id) VALUES 
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(1, 2)
ON CONFLICT DO NOTHING;

-- insert example records - Gate
INSERT INTO dbpersonel.gate (gate_name, main_entrance, fk_unit_id) VALUES 
('Gate Main Entrance', true, 4),
('Gate 1', false, 4),
('Gate 2', false, 3),
('Gate 3', false, 2),
('Gate 4', false, 1),
('Gate 5', false, 1),
('Gate 6', false, 3),
('Gate 7', false, 1),
('Gate 8', false, 2),
('Gate 9', false, 1),
('Gate 10', false, 4),
('Gate 11', false, 1)
ON CONFLICT DO NOTHING;

-- insert example records - Turnstile
INSERT INTO dbpersonel.turnstile (turnstile_name, fk_gate_id) VALUES 
('Turnstile 1', 1),
('Turnstile 2', 2),
('Turnstile 3', 3),
('Turnstile 4', 4),
('Turnstile 5', 5),
('Turnstile 6', 6),
('Turnstile 7', 7),
('Turnstile 8', 8),
('Turnstile 9', 9),
('Turnstile 10', 10)
ON CONFLICT DO NOTHING;

-- insert example records - Working Hours
INSERT INTO dbpersonel.working_hours (check_in_time, check_out_time, fk_personel_type_id) VALUES 
('09:00:00', '18:00:00', 1),
('09:00:00', '18:00:00', 2),
('09:00:00', '18:00:00', 3)
ON CONFLICT DO NOTHING;
