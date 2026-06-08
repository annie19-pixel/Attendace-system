DROP DATABASE IF EXISTS attendance_db;

CREATE DATABASE IF NOT EXISTS attendance_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE attendance_db;

-- =========================
-- USERS TABLE
-- =========================
CREATE TABLE users (
                       user_id INT PRIMARY KEY AUTO_INCREMENT,
                       full_name VARCHAR(200) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role ENUM('admin', 'student', 'lecturer') NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) AUTO_INCREMENT = 1000;


-- =========================
-- STUDENTS TABLE
-- =========================
CREATE TABLE students (
                          reg_no VARCHAR(50) PRIMARY KEY,                 -- e.g., '24/06623'
                          user_id INT UNIQUE NOT NULL,
                          student_name VARCHAR(100) NOT NULL,
                          year_of_study VARCHAR(20),
                          stream VARCHAR(20),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                          FOREIGN KEY (user_id)
                              REFERENCES users(user_id)
                              ON DELETE CASCADE
);


-- =========================
-- LECTURERS TABLE
-- =========================
CREATE TABLE lecturers (
                           staff_no VARCHAR(50) PRIMARY KEY,               -- e.g., 'LCT/24/001'
                           user_id INT UNIQUE NOT NULL,
                           lecturer_name VARCHAR(100) NOT NULL,
                           department VARCHAR(100),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                           FOREIGN KEY (user_id)
                               REFERENCES users(user_id)
                               ON DELETE CASCADE
);


-- =========================
-- UNITS TABLE
-- =========================
CREATE TABLE units (
                       unit_id INT PRIMARY KEY AUTO_INCREMENT,
                       unit_code VARCHAR(20) UNIQUE NOT NULL,          -- e.g., 'STU2022'
                       unit_name VARCHAR(100) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) AUTO_INCREMENT = 1000;


-- =========================
-- ENROLLMENT TABLE
-- =========================
CREATE TABLE enrollment (
                            enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
                            reg_no VARCHAR(50) NOT NULL,                    -- student via reg_no
                            unit_id INT NOT NULL,
                            staff_no VARCHAR(50) NOT NULL,                  -- lecturer via staff_no
                            semester VARCHAR(20) NOT NULL,
                            academic_year VARCHAR(20) NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                            FOREIGN KEY (reg_no)
                                REFERENCES students(reg_no)
                                ON DELETE CASCADE,

                            FOREIGN KEY (unit_id)
                                REFERENCES units(unit_id)
                                ON DELETE CASCADE,

                            FOREIGN KEY (staff_no)
                                REFERENCES lecturers(staff_no)
                                ON DELETE CASCADE
) AUTO_INCREMENT = 1000;


-- =========================
-- ATTENDANCE TABLE
-- =========================
CREATE TABLE attendance (
                            attendance_id INT PRIMARY KEY AUTO_INCREMENT,
                            enrollment_id INT NOT NULL,
                            attendance_date DATE NOT NULL,
                            status ENUM('Present', 'Absent', 'Late') NOT NULL,
                            time_marked TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                            FOREIGN KEY (enrollment_id)
                                REFERENCES enrollment(enrollment_id)
                                ON DELETE CASCADE
) AUTO_INCREMENT = 1000;

