# Java Swing Bus Reservation System (JDBC & MySQL)

This is a desktop-based Bus Reservation System demonstrating key principles of Object-Oriented Programming (OOP) in Java, along with Database Connectivity using JDBC and MySQL. 

## Architecture 
**Java Swing → OOP/Java Classes → JDBC → MySQL Database**

- **Frontend**: Desktop GUI built using `javax.swing` components (`JFrame`, `JTable`, etc.).
- **Application Logic**: Core domain objects (`Bus`, `ACBus`, `NonACBus`, `SleeperBus`) utilizing inheritance and polymorphism.
- **Connectivity**: `DatabaseConnection` using the MySQL Connector/J driver.
- **Backend**: MySQL Database storing permanent records of buses, passengers, and bookings.

## Features
- **Bus Management**: View available buses pulled dynamically from the MySQL database.
- **Polymorphism in Action**: Dynamic fare calculation based on specific bus types (AC, Non-AC, Sleeper).
- **GUI Booking**: Simple user interface to book tickets by entering passenger details.
- **Transaction Safety**: Uses JDBC transactions (`setAutoCommit(false)`) to ensure database consistency between `passengers` and `bookings`.

## Prerequisites
- Java Development Kit (JDK 17+)
- MySQL Server 8.0+ (or use Docker Desktop)
- `mysql-connector-j-8.4.0.jar` (Already provided in the `lib/` directory)

## Getting Started

### 1. Database Setup
You can set up the MySQL database using one of two methods:

**Method A: Using Docker Compose (Recommended)**
If you have Docker Desktop installed, simply run:
```bash
docker-compose up -d
```
*Note: This will automatically spin up a MySQL container on port 3306 and execute `database_setup.sql` to populate the schema and sample data.*

**Method B: Manual MySQL Setup**
1. Start your local MySQL server.
2. Execute the `database_setup.sql` script in your MySQL environment to create the `bus_booking` database and its tables.

### 2. Compilation
Compile the Java files from the root of the project directory. Make sure to include the JDBC driver in the classpath:

```powershell
# Windows
javac -cp "lib/mysql-connector-j-8.4.0.jar" -d out src/*.java
```

### 3. Running the Application
Once compiled, run the `BusBookingGUI` class:

```powershell
# Windows
java -cp "out;lib/mysql-connector-j-8.4.0.jar" BusBookingGUI
```

The GUI window will open, displaying the available buses and allowing you to book tickets directly into the database!
