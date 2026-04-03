CREATE TABLE IF NOT EXISTS leave_details (
    leave_detail_id SERIAL PRIMARY KEY,
    employee_id int NOT NULL,
    remaining_sick_leaves INT NOT NULL DEFAULT 7,
    remaining_casual_leaves INT NOT NULL DEFAULT 12,
    remaining_earned_leaves INT NOT NULL DEFAULT 21,
    leave_year INT NOT NULL,
    paid_leaves INT NOT NULL DEFAULT 0,
    total_paid_leaves INT NOT NULL DEFAULT 0,
    created_at date NOT NULL,
    created_by varchar(30) NOT NULL,
    updated_at date DEFAULT NULL,
    updated_by varchar(30) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS "leave" (
    leave_id SERIAL PRIMARY KEY,
    employee_id int NOT NULL,
    leave_type varchar(20) NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    applied_date date NOT NULL,
    status varchar(30) NOT NULL,
    comments varchar(200) NOT NULL,
    manager_id int NOT NULL,
    created_at date NOT NULL,
    created_by varchar(30) NOT NULL,
    updated_at date DEFAULT NULL,
    updated_by varchar(30) DEFAULT NULL
);
