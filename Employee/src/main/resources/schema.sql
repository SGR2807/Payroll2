CREATE TABLE IF NOT EXISTS employee (
     employee_id SERIAL PRIMARY KEY,
     first_name varchar(30) NOT NULL,
     last_name varchar(30) NOT NULL,
     mobile_number varchar(20) NOT NULL,
     dob date NOT NULL,
     manager_id int NOT NULL,
     role_id varchar(100),
     date_of_joining date NOT NULL,
     salary_id int NOT NULL,
     created_at date NOT NULL,
     created_by varchar(30) NOT NULL,
     updated_at date DEFAULT NULL,
     updated_by varchar(30) DEFAULT NULL
);
