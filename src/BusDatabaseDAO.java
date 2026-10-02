import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BusDatabaseDAO {

    // Fetch all buses from the database
    public List<Bus> getAllBuses() {
        List<Bus> busList = new ArrayList<>();
        String query = "SELECT * FROM buses";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String busNumber = rs.getString("bus_number");
                String busType = rs.getString("bus_type");
                String fromRoute = rs.getString("from_route");
                String toRoute = rs.getString("to_route");
                int totalSeats = rs.getInt("total_seats");
                double baseFare = rs.getDouble("base_fare");

                Bus bus = null;
                if (busType.equalsIgnoreCase("AC")) {
                    bus = new ACBus(busNumber, fromRoute, toRoute, totalSeats, baseFare);
                } else if (busType.equalsIgnoreCase("Sleeper")) {
                    bus = new SleeperBus(busNumber, fromRoute, toRoute, totalSeats, baseFare);
                } else {
                    bus = new NonACBus(busNumber, fromRoute, toRoute, totalSeats, baseFare);
                }

                busList.add(bus);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return busList;
    }

    // Add a new booking
    public boolean bookTicket(String busNumber, String passengerName, String phone, int seatNumber, double fare) {
        String passengerId = "P" + System.currentTimeMillis(); // Generate a random ID for simplicity

        String insertPassenger = "INSERT INTO passengers (passenger_id, name, age, gender, phone) VALUES (?, ?, 25, 'Unknown', ?)";
        String insertBooking = "INSERT INTO bookings (bus_number, passenger_id, seat_number, fare_paid) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Transaction processing - OOP/Data consistency

            try (PreparedStatement stmtPass = conn.prepareStatement(insertPassenger);
                 PreparedStatement stmtBook = conn.prepareStatement(insertBooking)) {

                // Insert passenger
                stmtPass.setString(1, passengerId);
                stmtPass.setString(2, passengerName);
                stmtPass.setString(3, phone);
                stmtPass.executeUpdate();

                // Insert booking
                stmtBook.setString(1, busNumber);
                stmtBook.setString(2, passengerId);
                stmtBook.setInt(3, seatNumber);
                stmtBook.setDouble(4, fare);
                stmtBook.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
