import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Modify these credentials as per your MySQL configuration
    private static final String URL = "jdbc:mysql://localhost:3306/bus_booking?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // change password if needed

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found. Ensure the MySQL connector JAR is in your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed. Please check if MySQL is running and credentials are correct.");
            e.printStackTrace();
        }
        return conn;
    }
}
