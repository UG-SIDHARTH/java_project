import java.io.File;

// Module 6: System Integration & OOP
public class Main {
    public static void main(String[] args) {
        System.out.println("Initializing Bus Reservation System...");
        
        try {
            BusManager bm = new BusManager();
            PassengerManager pm = new PassengerManager();
            BookingManager bkm = new BookingManager(bm, pm);

            // Pre-load sample data if empty
            if (bm.getAll().isEmpty()) {
                System.out.println("Pre-loading sample buses...");
                bm.add(new ACBus("BUS-101", "New York", "Boston", 10, 50.0));
                bm.add(new SleeperBus("BUS-202", "Los Angeles", "San Francisco", 8, 100.0));
                bm.add(new NonACBus("BUS-303", "Chicago", "Detroit", 15, 30.0));
            }
            
            if (pm.getAll().isEmpty()) {
                System.out.println("Pre-loading sample passengers...");
                pm.add(new Passenger("P1001", "John Doe", 35, "Male", "1234567890"));
                pm.add(new Passenger("P1002", "Alice Smith", 65, "Female", "0987654321")); // Senior
                pm.add(new Passenger("P1003", "Bob Jr.", 10, "Male", "1122334455")); // Child
            }

            WebServer server = new WebServer(6969, bm, pm, bkm);
            server.start();

        } catch (Exception e) {
            System.err.println("Failed to start the system: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
