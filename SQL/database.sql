-- This database file, when run in a MySQL database will result in:
-- 1. Creation of database and tables
-- 2. Inserting seed data for testing
-- 3. Stored procedures which can be called for testing the functionalities

CREATE DATABASE IF NOT EXISTS HospitalDB;
USE HospitalDB;

-- Creating the tables
CREATE TABLE Patient (
    patient_id CHAR(36) NOT NULL, -- UUID as CHAR(36)
    p_name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender ENUM("male", "female", "other") NOT NULL,
    PRIMARY KEY (patient_id)
);

CREATE TABLE Doctor (
	doctor_id CHAR(36) NOT NULL,
    d_name VARCHAR(100) NOT NULL,
    specialization varchar(25) NOT NULL,
    PRIMARY KEY (doctor_id)
);

CREATE TABLE Appointment (
    appointment_id CHAR(36) NOT NULL,
    a_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    a_status ENUM("BOOKED", "CONFIRMED", "CANCELLED", "ATTENDED", "MISSED") NOT NULL DEFAULT "BOOKED",
    patient_id CHAR(36) NOT NULL,
    doctor_id CHAR(36) NOT NULL,

    PRIMARY KEY (appointment_id),

    CONSTRAINT fk_patient_appointment
    FOREIGN KEY (patient_id)
    REFERENCES Patient(patient_id),

    CONSTRAINT fk_doctor_appointment
    FOREIGN KEY (doctor_id)
    REFERENCES Doctor(doctor_id)
);

CREATE TABLE Treatment (
    treatment_id CHAR(36) NOT NULL,
    appointment_id CHAR(36) NOT NULL,
    cost INT NOT NULL,
    diagnosis VARCHAR(255) NOT NULL,

    PRIMARY KEY (treatment_id),

    CONSTRAINT fk_appointment_treatment
    FOREIGN KEY (appointment_id)
    REFERENCES Appointment(appointment_id)
);

-- Seeding the database with sample data
INSERT INTO Patient
VALUES
    (UUID(), 'Ram', 30, 'male'),
    (UUID(), 'Krutika', 25, 'female'),
    (UUID(), 'Rahul', 40, 'male'),
    (UUID(), 'Priya', 35, 'female'),
    (UUID(), 'Alex', 28, 'other');

INSERT INTO Doctor
VALUES
    (UUID(), 'Dr. Amit', 'Cardiology'),
    (UUID(), 'Dr. Sarah', 'Dermatology'),
    (UUID(), 'Dr. Khan', 'Orthopedics'),
    (UUID(), 'Dr. Vikram', 'Pediatrics'),
    (UUID(), 'Dr. Rao', 'Neurology');

INSERT INTO Appointment 
VALUES
(
    UUID(),
    CURRENT_DATE,
    'BOOKED',
    (SELECT patient_id FROM Patient WHERE p_name = 'Ram' LIMIT 1),
    (SELECT doctor_id FROM Doctor WHERE d_name = 'Dr. Amit' LIMIT 1)
),
(
    UUID(),
    CURRENT_DATE,
    'CONFIRMED',
    (SELECT patient_id FROM Patient WHERE p_name = 'Krutika' LIMIT 1),
    (SELECT doctor_id FROM Doctor WHERE d_name = 'Dr. Sarah' LIMIT 1)
),
(
    UUID(),
    CURRENT_DATE - INTERVAL 1 DAY, -- Small date changes
    'ATTENDED',
    (SELECT patient_id FROM Patient WHERE p_name = 'Rahul' LIMIT 1),
    (SELECT doctor_id FROM Doctor WHERE d_name = 'Dr. Khan' LIMIT 1)
),
(
    UUID(),
    CURRENT_DATE,
    'CANCELLED',
    (SELECT patient_id FROM Patient WHERE p_name = 'Priya' LIMIT 1), -- Subquery to fetch patient_id
    (SELECT doctor_id FROM Doctor WHERE d_name = 'Dr. Vikram' LIMIT 1) -- Subquery to fetch doctor_id
),
(
    UUID(),
    CURRENT_DATE + INTERVAL 1 DAY,
    'BOOKED',
    (SELECT patient_id FROM Patient WHERE p_name = 'Alex' LIMIT 1),
    (SELECT doctor_id FROM Doctor WHERE d_name = 'Dr. Rao' LIMIT 1)
);

INSERT INTO Treatment
VALUES
(
    UUID(),
    (  -- Subquery to get appointment_id based on patient and doctor name
        SELECT a.appointment_id
        FROM Appointment a
        JOIN Patient p ON a.patient_id = p.patient_id
        JOIN Doctor d ON a.doctor_id = d.doctor_id
        WHERE p.p_name = 'Ram' AND d.d_name = 'Dr. Amit'
        LIMIT 1
    ),
    500,
    'General Checkup'
),
(
    UUID(),
    (
        SELECT a.appointment_id
        FROM Appointment a
        JOIN Patient p ON a.patient_id = p.patient_id
        JOIN Doctor d ON a.doctor_id = d.doctor_id
        WHERE p.p_name = 'Krutika' AND d.d_name = 'Dr. Sarah'
        LIMIT 1
    ),
    1200,
    'Skin Allergy Treatment'
),
(
    UUID(),
    (
        SELECT a.appointment_id
        FROM Appointment a
        JOIN Patient p ON a.patient_id = p.patient_id
        JOIN Doctor d ON a.doctor_id = d.doctor_id
        WHERE p.p_name = 'Rahul' AND d.d_name = 'Dr. Khan'
        LIMIT 1
    ),
    3000,
    'Fracture Treatment'
),
(
    UUID(),
    (
        SELECT a.appointment_id
        FROM Appointment a
        JOIN Patient p ON a.patient_id = p.patient_id
        JOIN Doctor d ON a.doctor_id = d.doctor_id
        WHERE p.p_name = 'Priya' AND d.d_name = 'Dr. Vikram'
        LIMIT 1
    ),
    800,
    'Routine Child Checkup'
);

-- Stored Procedures for functionality
-- Most consulted doctors 
DELIMITER $$
CREATE PROCEDURE MostConsultedDoctors(IN doctorCount INT)
BEGIN
    SELECT
        d.doctor_id,
        d.d_name,
        d.specialization
        COUNT(a.appointment_id) AS total_appointments
    FROM Doctor d JOIN Appointment a
    ON d.doctor_id = a.doctor_id
    GROUP BY d.doctor_id
    ORDER BY total_appointments DESC
    LIMIT doctorCount
END$$
DELIMITER;

-- Total revenue per month
DELIMITER $$
CREATE PROCEDURE TotalRevenuePerMonth()
BEGIN
    SELECT
        YEAR(a.a_date) AS year,
        MONTH(a.a_date) AS month,
        SUM(t.cost) AS total_revenue
    FROM Treatment t
    JOIN Appointment a 
        ON t.appointment_id = a.appointment_id
    GROUP BY YEAR(a.a_date), MONTH(a.a_date)
    ORDER BY year, month;
END$$
DELIMITER ;

-- Most common diseases
DELIMITER $$
CREATE PROCEDURE MostCommonDiseases(IN frequencyLimit INT)
BEGIN
    SELECT
        diagnosis,
        COUNT(diagnosis) AS frequency
    FROM Treatment t
    GROUP BY diagnosis
    ORDER BY frequency
    LIMIT frequencyLimit;
END$$
DELIMITER ;

-- Patient visit frequency
DELIMITER $$
CREATE PROCEDURE MostFrequentPatients(IN frequencyLimit INT)
BEGIN
    SELECT
        p.p_name as patient_name,
        p.age as patient_age,
        p.gender as patient_gender,
        COUNT(a.appointment_id) as appointments
    FROM Patient p JOIN Appointment a
    ON p.patient_id = a.patient_id
    GROUP BY a.patient_id
    ORDER BY appointments
    LIMIT frequencyLimit;
END$$
DELIMITER ;

-- Analysis of doctor performance (total appointments + total revenue)
DELIMITER $$

CREATE PROCEDURE AnalyzeDoctorPerformance()
BEGIN
    SELECT
        d.doctor_id,
        d.d_name,
        d.specialization,
        COUNT(a.appointment_id) AS total_appointments,
        COALESCE(SUM(t.cost), 0) AS total_revenue
    FROM Doctor d
    LEFT JOIN Appointment a
        ON d.doctor_id = a.doctor_id
    LEFT JOIN Treatment t
        ON a.appointment_id = t.appointment_id
    GROUP BY d.doctor_id, d.d_name, d.specialization
    ORDER BY total_revenue DESC, total_appointments DESC;
END$$

DELIMITER ;