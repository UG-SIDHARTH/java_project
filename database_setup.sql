CREATE DATABASE IF NOT EXISTS bus_booking;
USE bus_booking;

CREATE TABLE IF NOT EXISTS buses (
    bus_number VARCHAR(20) PRIMARY KEY,
    bus_type VARCHAR(20) NOT NULL,
    from_route VARCHAR(50) NOT NULL,
    to_route VARCHAR(50) NOT NULL,
    total_seats INT NOT NULL,
    base_fare DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS passengers (
    passenger_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(10) NOT NULL,
    phone VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    bus_number VARCHAR(20),
    passenger_id VARCHAR(20),
    seat_number INT NOT NULL,
    fare_paid DECIMAL(10, 2) NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bus_number) REFERENCES buses(bus_number),
    FOREIGN KEY (passenger_id) REFERENCES passengers(passenger_id)
);

-- Insert sample data
INSERT INTO buses (bus_number, bus_type, from_route, to_route, total_seats, base_fare) VALUES
('BUS-101', 'AC', 'New York', 'Boston', 10, 50.00),
('BUS-202', 'Sleeper', 'Los Angeles', 'San Francisco', 8, 100.00),
('BUS-303', 'Non-AC', 'Chicago', 'Detroit', 15, 30.00)
ON DUPLICATE KEY UPDATE from_route=from_route;
