import java.util.*;

// Module 5: Exception Handling & Reporting
public class Report {
    public static Map<String, Double> generateRevenuePerBus(BookingManager bm) {
        Map<String, Double> rev = new HashMap<>();
        for (Booking b : bm.getAll()) {
            rev.put(b.getBusNumber(), rev.getOrDefault(b.getBusNumber(), 0.0) + b.getTotalFare());
        }
        return rev;
    }
    
    public static int getTotalBookings(BookingManager bm) {
        return bm.getAll().size();
    }
}
