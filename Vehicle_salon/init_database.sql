DROP TABLE IF EXISTS rental;
DROP TABLE IF EXISTS vehicle;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id TEXT PRIMARY KEY,
    login TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role TEXT NOT NULL
);


CREATE TABLE vehicle (
    id TEXT PRIMARY KEY,
    category TEXT,
    brand TEXT,
    model TEXT,
    year INT,
    plate TEXT,
    price NUMERIC,
    attributes JSONB
);


CREATE TABLE rental (
    id TEXT PRIMARY KEY,
    vehicle_id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    rent_date TEXT NOT NULL,
    return_date TEXT,
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);


INSERT INTO users (id, login, password, role) VALUES
('fc6e600b-e89e-42e2-b2b7-ca18edb68924', 'lukasz', '$2a$10$7MUYZR8FP8ebLzfqwMK85ejBke/TNp/Yme9NrC0XxRy.NAFde2vEm', 'ADMIN'),
('f4b5b79b-df2c-4e0a-89fc-464908d2e6dc', 'kamil', '$2a$10$BwY3UtVLQTSUjX3zXjCeJ.h4Gwq4MiLhhTaUC.Yesce7rBDRkdU/i', 'USER');


INSERT INTO vehicle (id, category, brand, model, year, plate, price, attributes) VALUES
('1', 'Bus', 'Volkswagen', 'T2', 1985, 'LU123', 100.0, '{"seats": 20}'),
('2', 'Motorcycle', 'Honda', 'CBR600', 2016, 'LU456', 200.0, '{"licence_category": "A", "drive": "chain"}'),
('3', 'Car', 'Toyota', 'Corolla', 2024, 'LU789', 300.0, '{}');


INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date) VALUES
('884646be-d46a-4cfb-bda2-12c5ffff87f0', '2', 'f4b5b79b-df2c-4e0a-89fc-464908d2e6dc', '2025-03-25T14:25:49.982037500', '2025-03-26T13:26:07.052319200'),
('3500e6c9-9878-41ac-9b65-ee5af8a5fc41', '1', 'fc6e600b-e89e-42e2-b2b7-ca18edb68924', '2025-03-25T08:59:12.121505', '2025-03-26T07:59:20.468257700');
