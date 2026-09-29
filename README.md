# Pure Java Bus Reservation System

A complete web-based Bus Reservation System built strictly with **Java 17+**. 
No external frameworks (Spring, Node, Maven, Gradle) were used. The `HttpServer` from `com.sun.net.httpserver` is used to serve pages rendered dynamically using Java Text Blocks.

## Features
- Bus Management (AC, Non-AC, Sleeper)
- Passenger Management
- Seat Booking with visual grid (Prevents double booking)
- Dynamic Fare Calculation (Base fare * Bus Type Multiplier * Age Discount)
- CSV file based data storage

## File Structure & Modules
1. **Bus Management**: `Bus.java`, `ACBus.java`, `NonACBus.java`, `SleeperBus.java`, `BusManager.java`
2. **Passenger Management**: `Person.java`, `Passenger.java`, `PassengerManager.java`
3. **Seat Reservation**: `Seat.java`, `Booking.java`, `BookingManager.java`
4. **Fare Calculation**: `FareCalculator.java`, `Payment.java`
5. **Data Storage & Exception Handling**: `FileManager.java`, `Validation.java`, `Report.java`, `InvalidInputException.java`, `BusNotFoundException.java`, `SeatAlreadyBookedException.java`
6. **System Integration**: `Manageable.java`, `WebServer.java`, `Main.java`

## How to Run

1. Open your terminal in the root project folder (where `src` is).
2. Compile the Java files:
   ```bash
   javac -d out src/*.java
   ```
3. Run the application:
   ```bash
   java -cp out Main
   ```
4. Open your browser and navigate to:
   [http://localhost:8080](http://localhost:8080)
   
Sample data is automatically injected if the database files (`data/*.csv`) are empty.
