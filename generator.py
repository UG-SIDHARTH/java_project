import os

src_dir = r"e:\d drive\antigravity projects\java_project\src"
data_dir = r"e:\d drive\antigravity projects\java_project\data"
os.makedirs(src_dir, exist_ok=True)
os.makedirs(data_dir, exist_ok=True)

classes = {}

classes["Manageable.java"] = """
import java.util.List;

// Module 6: System Integration & OOP
public interface Manageable<T> {
    void add(T item) throws Exception;
    void update(T item) throws Exception;
    void delete(String id) throws Exception;
    T get(String id) throws Exception;
    List<T> getAll();
}
"""

classes["InvalidInputException.java"] = """
// Module 5: Exception Handling
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}
"""

classes["BusNotFoundException.java"] = """
// Module 5: Exception Handling
public class BusNotFoundException extends Exception {
    public BusNotFoundException(String message) {
        super(message);
    }
}
"""

classes["SeatAlreadyBookedException.java"] = """
// Module 5: Exception Handling
public class SeatAlreadyBookedException extends Exception {
    public SeatAlreadyBookedException(String message) {
        super(message);
    }
}
"""

classes["Person.java"] = """
// Module 6: System Integration & OOP (Inheritance/Abstraction)
public abstract class Person {
    protected String id;
    protected String name;
    protected int age;
    protected String gender;
    protected String phone;

    public Person(String id, String name, int age, String gender, String phone) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getPhone() { return phone; }
    
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }
    public void setPhone(String phone) { this.phone = phone; }
}
"""

classes["Passenger.java"] = """
// Module 2: Passenger Management
public class Passenger extends Person {
    public Passenger(String id, String name, int age, String gender, String phone) {
        super(id, name, age, gender, phone);
    }
    
    public String toCsv() {
        return id + "," + name + "," + age + "," + gender + "," + phone;
    }
    
    public static Passenger fromCsv(String csv) {
        String[] parts = csv.split(",");
        if(parts.length == 5) {
            return new Passenger(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], parts[4]);
        }
        return null;
    }
}
"""

classes["Bus.java"] = """
// Module 1: Bus Management
// Module 6: OOP (Abstraction)
public abstract class Bus {
    protected String busNumber;
    protected String fromRoute;
    protected String toRoute;
    protected int totalSeats;
    protected double baseFare;

    public Bus(String busNumber, String fromRoute, String toRoute, int totalSeats, double baseFare) {
        this.busNumber = busNumber;
        this.fromRoute = fromRoute;
        this.toRoute = toRoute;
        this.totalSeats = totalSeats;
        this.baseFare = baseFare;
    }

    public abstract String getType();
    public abstract double calculateFare();

    public String getBusNumber() { return busNumber; }
    public String getFromRoute() { return fromRoute; }
    public String getToRoute() { return toRoute; }
    public int getTotalSeats() { return totalSeats; }
    public double getBaseFare() { return baseFare; }

    public String toCsv() {
        return busNumber + "," + getType() + "," + fromRoute + "," + toRoute + "," + totalSeats + "," + baseFare;
    }
}
"""

classes["ACBus.java"] = """
// Module 1: Bus Management
public class ACBus extends Bus {
    public ACBus(String busNumber, String fromRoute, String toRoute, int totalSeats, double baseFare) {
        super(busNumber, fromRoute, toRoute, totalSeats, baseFare);
    }
    @Override
    public String getType() { return "AC"; }
    @Override
    public double calculateFare() { return baseFare * 1.5; }
}
"""

classes["NonACBus.java"] = """
// Module 1: Bus Management
public class NonACBus extends Bus {
    public NonACBus(String busNumber, String fromRoute, String toRoute, int totalSeats, double baseFare) {
        super(busNumber, fromRoute, toRoute, totalSeats, baseFare);
    }
    @Override
    public String getType() { return "Non-AC"; }
    @Override
    public double calculateFare() { return baseFare * 1.0; }
}
"""

classes["SleeperBus.java"] = """
// Module 1: Bus Management
public class SleeperBus extends Bus {
    public SleeperBus(String busNumber, String fromRoute, String toRoute, int totalSeats, double baseFare) {
        super(busNumber, fromRoute, toRoute, totalSeats, baseFare);
    }
    @Override
    public String getType() { return "Sleeper"; }
    @Override
    public double calculateFare() { return baseFare * 2.0; }
}
"""

classes["FileManager.java"] = """
import java.io.*;
import java.util.*;

// Module 5: Data Storage
public class FileManager {
    private static final String DATA_DIR = "data/";

    static {
        new File(DATA_DIR).mkdirs();
    }

    public static void saveLines(String filename, List<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_DIR + filename))) {
            for (String line : lines) {
                pw.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }

    public static List<String> loadLines(String filename) {
        List<String> lines = new ArrayList<>();
        File file = new File(DATA_DIR + filename);
        if (!file.exists()) return lines;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return lines;
    }
}
"""

classes["BusManager.java"] = """
import java.util.*;

// Module 1: Bus Management
public class BusManager implements Manageable<Bus> {
    private List<Bus> buses = new ArrayList<>();
    private static final String FILE_NAME = "buses.csv";

    public BusManager() {
        loadData();
    }

    @Override
    public void add(Bus item) throws Exception {
        if (get(item.getBusNumber()) != null) {
            throw new InvalidInputException("Bus with number " + item.getBusNumber() + " already exists!");
        }
        buses.add(item);
        saveData();
    }

    @Override
    public void update(Bus item) throws Exception {
        Bus existing = get(item.getBusNumber());
        if (existing == null) throw new BusNotFoundException("Bus not found");
        buses.remove(existing);
        buses.add(item);
        saveData();
    }

    @Override
    public void delete(String id) throws Exception {
        Bus existing = get(id);
        if (existing == null) throw new BusNotFoundException("Bus not found");
        buses.remove(existing);
        saveData();
    }

    @Override
    public Bus get(String id) {
        return buses.stream().filter(b -> b.getBusNumber().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Bus> getAll() {
        return buses;
    }

    private void loadData() {
        List<String> lines = FileManager.loadLines(FILE_NAME);
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 6) {
                String busNumber = parts[0];
                String type = parts[1];
                String from = parts[2];
                String to = parts[3];
                int totalSeats = Integer.parseInt(parts[4]);
                double baseFare = Double.parseDouble(parts[5]);
                
                if (type.equals("AC")) buses.add(new ACBus(busNumber, from, to, totalSeats, baseFare));
                else if (type.equals("Sleeper")) buses.add(new SleeperBus(busNumber, from, to, totalSeats, baseFare));
                else buses.add(new NonACBus(busNumber, from, to, totalSeats, baseFare));
            }
        }
    }

    private void saveData() {
        List<String> lines = new ArrayList<>();
        for (Bus b : buses) {
            lines.add(b.toCsv());
        }
        FileManager.saveLines(FILE_NAME, lines);
    }
}
"""

classes["PassengerManager.java"] = """
import java.util.*;

// Module 2: Passenger Management
public class PassengerManager implements Manageable<Passenger> {
    private List<Passenger> passengers = new ArrayList<>();
    private static final String FILE_NAME = "passengers.csv";

    public PassengerManager() {
        loadData();
    }

    @Override
    public void add(Passenger item) throws Exception {
        if (get(item.getId()) != null) {
            throw new InvalidInputException("Passenger ID already exists!");
        }
        passengers.add(item);
        saveData();
    }

    @Override
    public void update(Passenger item) throws Exception {
        Passenger existing = get(item.getId());
        if (existing == null) throw new InvalidInputException("Passenger not found");
        passengers.remove(existing);
        passengers.add(item);
        saveData();
    }

    @Override
    public void delete(String id) throws Exception {
        Passenger existing = get(id);
        if (existing != null) {
            passengers.remove(existing);
            saveData();
        }
    }

    @Override
    public Passenger get(String id) {
        return passengers.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Passenger> getAll() {
        return passengers;
    }

    private void loadData() {
        List<String> lines = FileManager.loadLines(FILE_NAME);
        for (String line : lines) {
            Passenger p = Passenger.fromCsv(line);
            if (p != null) passengers.add(p);
        }
    }

    private void saveData() {
        List<String> lines = new ArrayList<>();
        for (Passenger p : passengers) {
            lines.add(p.toCsv());
        }
        FileManager.saveLines(FILE_NAME, lines);
    }
}
"""

classes["Seat.java"] = """
// Module 3: Seat Reservation
public class Seat {
    private String busNumber;
    private int seatNumber;
    private boolean isBooked;

    public Seat(String busNumber, int seatNumber, boolean isBooked) {
        this.busNumber = busNumber;
        this.seatNumber = seatNumber;
        this.isBooked = isBooked;
    }
    
    public String getBusNumber() { return busNumber; }
    public int getSeatNumber() { return seatNumber; }
    public boolean isBooked() { return isBooked; }
    public void setBooked(boolean booked) { isBooked = booked; }
}
"""

classes["Booking.java"] = """
// Module 3: Seat Reservation & Booking
public class Booking {
    private String id;
    private String busNumber;
    private String passengerId;
    private int seatNumber;
    private double totalFare;

    public Booking(String id, String busNumber, String passengerId, int seatNumber, double totalFare) {
        this.id = id;
        this.busNumber = busNumber;
        this.passengerId = passengerId;
        this.seatNumber = seatNumber;
        this.totalFare = totalFare;
    }

    public String getId() { return id; }
    public String getBusNumber() { return busNumber; }
    public String getPassengerId() { return passengerId; }
    public int getSeatNumber() { return seatNumber; }
    public double getTotalFare() { return totalFare; }

    public String toCsv() {
        return id + "," + busNumber + "," + passengerId + "," + seatNumber + "," + totalFare;
    }

    public static Booking fromCsv(String csv) {
        String[] parts = csv.split(",");
        if (parts.length == 5) {
            return new Booking(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), Double.parseDouble(parts[4]));
        }
        return null;
    }
}
"""

classes["FareCalculator.java"] = """
// Module 4: Fare Calculation
public class FareCalculator {
    public static double calculateFinalFare(Bus bus, Passenger passenger) {
        double base = bus.calculateFare(); // polymorphic call
        if (passenger.getAge() >= 60) {
            base = base * 0.9; // 10% senior discount
        } else if (passenger.getAge() < 12) {
            base = base * 0.5; // 50% child discount
        }
        return base;
    }
}
"""

classes["Payment.java"] = """
// Module 4: Fare Calculation & Ticket Gen
public class Payment {
    public static boolean processPayment(double amount) {
        // In a real system, interface with payment gateway.
        // For this, we just approve all payments.
        return true; 
    }
}
"""

classes["BookingManager.java"] = """
import java.util.*;
import java.util.stream.Collectors;

// Module 3: Seat Reservation & Booking
public class BookingManager implements Manageable<Booking> {
    private List<Booking> bookings = new ArrayList<>();
    private static final String FILE_NAME = "bookings.csv";
    
    private BusManager busManager;
    private PassengerManager passengerManager;

    public BookingManager(BusManager busManager, PassengerManager passengerManager) {
        this.busManager = busManager;
        this.passengerManager = passengerManager;
        loadData();
    }

    public List<Integer> getBookedSeats(String busNumber) {
        return bookings.stream()
            .filter(b -> b.getBusNumber().equals(busNumber))
            .map(Booking::getSeatNumber)
            .collect(Collectors.toList());
    }

    public Booking bookSeat(String busNumber, String passengerId, int seatNumber) throws Exception {
        Bus bus = busManager.get(busNumber);
        if (bus == null) throw new BusNotFoundException("Bus not found");
        Passenger passenger = passengerManager.get(passengerId);
        if (passenger == null) throw new InvalidInputException("Passenger not found");
        
        if (seatNumber < 1 || seatNumber > bus.getTotalSeats()) {
            throw new InvalidInputException("Invalid seat number");
        }

        List<Integer> booked = getBookedSeats(busNumber);
        if (booked.contains(seatNumber)) {
            throw new SeatAlreadyBookedException("Seat " + seatNumber + " is already booked on bus " + busNumber);
        }

        double fare = FareCalculator.calculateFinalFare(bus, passenger);
        if (!Payment.processPayment(fare)) {
            throw new InvalidInputException("Payment failed");
        }

        String bookingId = UUID.randomUUID().toString().substring(0, 8);
        Booking b = new Booking(bookingId, busNumber, passengerId, seatNumber, fare);
        add(b);
        return b;
    }

    @Override
    public void add(Booking item) throws Exception {
        bookings.add(item);
        saveData();
    }

    @Override
    public void update(Booking item) throws Exception {
        Booking existing = get(item.getId());
        if(existing != null) {
            bookings.remove(existing);
            bookings.add(item);
            saveData();
        }
    }

    @Override
    public void delete(String id) throws Exception {
        Booking existing = get(id);
        if (existing != null) {
            bookings.remove(existing);
            saveData();
        }
    }

    @Override
    public Booking get(String id) {
        return bookings.stream().filter(b -> b.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Booking> getAll() {
        return bookings;
    }

    private void loadData() {
        List<String> lines = FileManager.loadLines(FILE_NAME);
        for (String line : lines) {
            Booking b = Booking.fromCsv(line);
            if (b != null) bookings.add(b);
        }
    }

    private void saveData() {
        List<String> lines = new ArrayList<>();
        for (Booking b : bookings) {
            lines.add(b.toCsv());
        }
        FileManager.saveLines(FILE_NAME, lines);
    }
}
"""

classes["Validation.java"] = """
// Module 5: Data Storage & Exception Handling
public class Validation {
    public static void validateNotEmpty(String value, String fieldName) throws InvalidInputException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " cannot be empty");
        }
    }

    public static void validateAge(int age) throws InvalidInputException {
        if (age < 0 || age > 120) {
            throw new InvalidInputException("Invalid age provided");
        }
    }

    public static void validatePhone(String phone) throws InvalidInputException {
        if (phone == null || !phone.matches("\\\\d{10}")) {
            throw new InvalidInputException("Phone number must be exactly 10 digits");
        }
    }
}
"""

classes["Report.java"] = """
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
"""

classes["WebServer.java"] = """
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

// Module 6: System Integration
public class WebServer {
    private HttpServer server;
    private BusManager busManager;
    private PassengerManager passengerManager;
    private BookingManager bookingManager;

    public WebServer(int port, BusManager bm, PassengerManager pm, BookingManager bkm) throws IOException {
        this.busManager = bm;
        this.passengerManager = pm;
        this.bookingManager = bkm;
        
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new HomeHandler());
        server.createContext("/buses", new BusHandler());
        server.createContext("/passengers", new PassengerHandler());
        server.createContext("/book", new BookHandler());
        server.createContext("/bookings", new BookingListHandler());
        server.createContext("/ticket", new TicketHandler());
        server.createContext("/reports", new ReportHandler());
        
        server.setExecutor(null);
    }

    public void start() {
        server.start();
        System.out.println("Web server started at http://localhost:" + server.getAddress().getPort());
    }

    private String getHeader(String title) {
        return \"\"\"
        <html>
        <head>
            <title>%s - Bus Reservation</title>
            <style>
                body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f4f7f6; margin: 0; padding: 0; }
                header { background: #2c3e50; color: #ecf0f1; padding: 15px 20px; display: flex; justify-content: space-between; align-items: center; }
                header a { color: #ecf0f1; text-decoration: none; margin-left: 15px; font-weight: bold; }
                header a:hover { color: #3498db; }
                .container { max-width: 1000px; margin: 30px auto; background: white; padding: 25px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
                h1, h2 { color: #2c3e50; }
                table { width: 100%%; border-collapse: collapse; margin-top: 15px; }
                th, td { padding: 12px; border: 1px solid #ddd; text-align: left; }
                th { background: #34495e; color: white; }
                .btn { background: #3498db; color: white; padding: 8px 12px; border: none; border-radius: 4px; cursor: pointer; text-decoration: none; display: inline-block; }
                .btn:hover { background: #2980b9; }
                .btn-danger { background: #e74c3c; }
                .btn-danger:hover { background: #c0392b; }
                .btn-success { background: #2ecc71; }
                .btn-success:hover { background: #27ae60; }
                .form-group { margin-bottom: 15px; }
                .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
                .form-group input, .form-group select { width: 100%%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
                .alert { padding: 15px; background-color: #f44336; color: white; margin-bottom: 15px; border-radius: 4px; }
                .seat-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; margin-top: 20px; }
                .seat { padding: 15px; text-align: center; border-radius: 4px; font-weight: bold; color: white; }
                .seat.available { background: #2ecc71; cursor: pointer; }
                .seat.booked { background: #e74c3c; cursor: not-allowed; }
                .card { border: 1px solid #ddd; padding: 20px; border-radius: 8px; text-align: center; margin-bottom:20px; }
            </style>
            <script>
                function selectSeat(seatNo) {
                    document.getElementById('selectedSeat').value = seatNo;
                    alert('Seat ' + seatNo + ' selected. Now choose a passenger and book!');
                }
            </script>
        </head>
        <body>
            <header>
                <div><h3>Bus Reservation System</h3></div>
                <div>
                    <a href="/">Home</a>
                    <a href="/buses">Buses</a>
                    <a href="/passengers">Passengers</a>
                    <a href="/book">Book Ticket</a>
                    <a href="/bookings">Bookings</a>
                    <a href="/reports">Reports</a>
                </div>
            </header>
            <div class="container">
        \"\"\".formatted(title);
    }

    private String getFooter() {
        return "</div></body></html>";
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
    
    private Map<String, String> parseForm(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null) return result;
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                try {
                    result.put(entry[0], URLDecoder.decode(entry[1], "UTF-8"));
                } catch (UnsupportedEncodingException e) {}
            } else if (entry.length == 1) {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder html = new StringBuilder(getHeader("Dashboard"));
            html.append("<h1>Welcome to Bus Reservation System</h1>");
            html.append("<div style='display:flex; gap: 20px; margin-top:20px;'>");
            html.append("<div class='card' style='flex:1'><h2>").append(busManager.getAll().size()).append("</h2><p>Buses</p></div>");
            html.append("<div class='card' style='flex:1'><h2>").append(passengerManager.getAll().size()).append("</h2><p>Passengers</p></div>");
            html.append("<div class='card' style='flex:1'><h2>").append(bookingManager.getAll().size()).append("</h2><p>Total Bookings</p></div>");
            html.append("</div>");
            html.append(getFooter());
            sendResponse(exchange, 200, html.toString());
        }
    }

    class BusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String error = null;

            if ("POST".equalsIgnoreCase(method)) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                Map<String, String> params = parseForm(br.readLine());
                
                String action = params.get("action");
                if ("add".equals(action)) {
                    try {
                        String busNo = params.get("busNumber");
                        String type = params.get("type");
                        String from = params.get("from");
                        String to = params.get("to");
                        int seats = Integer.parseInt(params.get("seats"));
                        double fare = Double.parseDouble(params.get("fare"));
                        
                        Validation.validateNotEmpty(busNo, "Bus Number");
                        Validation.validateNotEmpty(from, "From Route");
                        Validation.validateNotEmpty(to, "To Route");
                        
                        Bus b = null;
                        if ("AC".equals(type)) b = new ACBus(busNo, from, to, seats, fare);
                        else if ("Sleeper".equals(type)) b = new SleeperBus(busNo, from, to, seats, fare);
                        else b = new NonACBus(busNo, from, to, seats, fare);
                        
                        busManager.add(b);
                    } catch (Exception e) {
                        error = e.getMessage();
                    }
                } else if ("delete".equals(action)) {
                    try {
                        busManager.delete(params.get("busNumber"));
                    } catch(Exception e) {
                        error = e.getMessage();
                    }
                }
                
                if (error == null) {
                    exchange.getResponseHeaders().set("Location", "/buses");
                    exchange.sendResponseHeaders(302, -1);
                    return;
                }
            }

            StringBuilder html = new StringBuilder(getHeader("Manage Buses"));
            if (error != null) {
                html.append("<div class='alert'>").append(error).append("</div>");
            }
            html.append("<h2>Add New Bus</h2>");
            html.append("<form method='POST'>");
            html.append("<input type='hidden' name='action' value='add'>");
            html.append("<div class='form-group'><label>Bus Number</label><input type='text' name='busNumber' required></div>");
            html.append("<div class='form-group'><label>Type</label><select name='type'><option>Non-AC</option><option>AC</option><option>Sleeper</option></select></div>");
            html.append("<div class='form-group'><label>From</label><input type='text' name='from' required></div>");
            html.append("<div class='form-group'><label>To</label><input type='text' name='to' required></div>");
            html.append("<div class='form-group'><label>Total Seats</label><input type='number' name='seats' required></div>");
            html.append("<div class='form-group'><label>Base Fare</label><input type='number' step='0.01' name='fare' required></div>");
            html.append("<button type='submit' class='btn btn-success'>Add Bus</button>");
            html.append("</form>");

            html.append("<h2>Available Buses</h2>");
            html.append("<table><tr><th>Bus No</th><th>Type</th><th>Route</th><th>Seats</th><th>Base Fare</th><th>Action</th></tr>");
            for (Bus b : busManager.getAll()) {
                html.append("<tr>")
                    .append("<td>").append(b.getBusNumber()).append("</td>")
                    .append("<td>").append(b.getType()).append("</td>")
                    .append("<td>").append(b.getFromRoute()).append(" -> ").append(b.getToRoute()).append("</td>")
                    .append("<td>").append(b.getTotalSeats()).append("</td>")
                    .append("<td>&#8377;").append(b.getBaseFare()).append("</td>")
                    .append("<td><form method='POST' style='margin:0;'><input type='hidden' name='action' value='delete'><input type='hidden' name='busNumber' value='").append(b.getBusNumber()).append("'><button class='btn btn-danger' type='submit'>Delete</button></form></td>")
                    .append("</tr>");
            }
            html.append("</table>");
            html.append(getFooter());
            sendResponse(exchange, 200, html.toString());
        }
    }

    class PassengerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String error = null;

            if ("POST".equalsIgnoreCase(method)) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                Map<String, String> params = parseForm(br.readLine());
                
                String action = params.get("action");
                if ("add".equals(action)) {
                    try {
                        String id = "P" + System.currentTimeMillis() % 10000;
                        String name = params.get("name");
                        int age = Integer.parseInt(params.get("age"));
                        String gender = params.get("gender");
                        String phone = params.get("phone");
                        
                        Validation.validateNotEmpty(name, "Name");
                        Validation.validateAge(age);
                        Validation.validatePhone(phone);
                        
                        Passenger p = new Passenger(id, name, age, gender, phone);
                        passengerManager.add(p);
                    } catch (Exception e) {
                        error = e.getMessage();
                    }
                }
                
                if (error == null) {
                    exchange.getResponseHeaders().set("Location", "/passengers");
                    exchange.sendResponseHeaders(302, -1);
                    return;
                }
            }

            StringBuilder html = new StringBuilder(getHeader("Manage Passengers"));
            if (error != null) {
                html.append("<div class='alert'>").append(error).append("</div>");
            }
            html.append("<h2>Add New Passenger</h2>");
            html.append("<form method='POST'>");
            html.append("<input type='hidden' name='action' value='add'>");
            html.append("<div class='form-group'><label>Name</label><input type='text' name='name' required></div>");
            html.append("<div class='form-group'><label>Age</label><input type='number' name='age' required></div>");
            html.append("<div class='form-group'><label>Gender</label><select name='gender'><option>Male</option><option>Female</option><option>Other</option></select></div>");
            html.append("<div class='form-group'><label>Phone (10 digits)</label><input type='text' name='phone' required></div>");
            html.append("<button type='submit' class='btn btn-success'>Add Passenger</button>");
            html.append("</form>");

            html.append("<h2>Passenger Directory</h2>");
            html.append("<table><tr><th>ID</th><th>Name</th><th>Age</th><th>Gender</th><th>Phone</th></tr>");
            for (Passenger p : passengerManager.getAll()) {
                html.append("<tr>")
                    .append("<td>").append(p.getId()).append("</td>")
                    .append("<td>").append(p.getName()).append("</td>")
                    .append("<td>").append(p.getAge()).append("</td>")
                    .append("<td>").append(p.getGender()).append("</td>")
                    .append("<td>").append(p.getPhone()).append("</td>")
                    .append("</tr>");
            }
            html.append("</table>");
            html.append(getFooter());
            sendResponse(exchange, 200, html.toString());
        }
    }

    class BookHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String error = null;
            String successId = null;

            if ("POST".equalsIgnoreCase(method)) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                Map<String, String> params = parseForm(br.readLine());
                
                try {
                    String busNo = params.get("busNumber");
                    String passId = params.get("passengerId");
                    int seatNo = Integer.parseInt(params.get("seatNumber"));
                    
                    Booking b = bookingManager.bookSeat(busNo, passId, seatNo);
                    successId = b.getId();
                    
                    exchange.getResponseHeaders().set("Location", "/ticket?id=" + successId);
                    exchange.sendResponseHeaders(302, -1);
                    return;
                } catch (Exception e) {
                    error = e.getMessage();
                }
            }

            Map<String, String> queryParams = parseForm(exchange.getRequestURI().getQuery());
            String selectedBus = queryParams.get("bus");

            StringBuilder html = new StringBuilder(getHeader("Book Ticket"));
            if (error != null) html.append("<div class='alert'>").append(error).append("</div>");

            html.append("<h2>Select Bus to View Layout</h2>");
            html.append("<form method='GET'>");
            html.append("<div class='form-group'><select name='bus' onchange='this.form.submit()'>");
            html.append("<option value=''>-- Select Bus --</option>");
            for (Bus b : busManager.getAll()) {
                String sel = b.getBusNumber().equals(selectedBus) ? "selected" : "";
                html.append("<option value='").append(b.getBusNumber()).append("' ").append(sel).append(">")
                    .append(b.getBusNumber()).append(" (").append(b.getFromRoute()).append(" - ").append(b.getToRoute()).append(")")
                    .append("</option>");
            }
            html.append("</select></div></form>");

            if (selectedBus != null && !selectedBus.isEmpty()) {
                Bus bus = busManager.get(selectedBus);
                if (bus != null) {
                    html.append("<h3>Seat Layout: ").append(bus.getBusNumber()).append("</h3>");
                    List<Integer> booked = bookingManager.getBookedSeats(selectedBus);
                    
                    html.append("<div class='seat-grid'>");
                    for (int i = 1; i <= bus.getTotalSeats(); i++) {
                        if (booked.contains(i)) {
                            html.append("<div class='seat booked'>Seat ").append(i).append("<br>(Booked)</div>");
                        } else {
                            html.append("<div class='seat available' onclick='selectSeat(").append(i).append(")'>Seat ").append(i).append("</div>");
                        }
                    }
                    html.append("</div>");

                    html.append("<h3 style='margin-top:30px;'>Complete Booking</h3>");
                    html.append("<form method='POST'>");
                    html.append("<input type='hidden' name='busNumber' value='").append(selectedBus).append("'>");
                    html.append("<div class='form-group'><label>Selected Seat Number</label><input type='text' id='selectedSeat' name='seatNumber' readonly required></div>");
                    
                    html.append("<div class='form-group'><label>Select Passenger</label><select name='passengerId' required>");
                    html.append("<option value=''>-- Choose Passenger --</option>");
                    for (Passenger p : passengerManager.getAll()) {
                        html.append("<option value='").append(p.getId()).append("'>").append(p.getName()).append(" (ID: ").append(p.getId()).append(", Age: ").append(p.getAge()).append(")</option>");
                    }
                    html.append("</select></div>");
                    
                    html.append("<button type='submit' class='btn btn-success'>Confirm Booking</button>");
                    html.append("</form>");
                }
            }

            html.append(getFooter());
            sendResponse(exchange, 200, html.toString());
        }
    }

    class BookingListHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            
            if ("POST".equalsIgnoreCase(method)) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                Map<String, String> params = parseForm(br.readLine());
                
                try {
                    bookingManager.delete(params.get("id"));
                } catch(Exception e) {}
                
                exchange.getResponseHeaders().set("Location", "/bookings");
                exchange.sendResponseHeaders(302, -1);
                return;
            }

            StringBuilder html = new StringBuilder(getHeader("All Bookings"));
            html.append("<h2>Booking Records</h2>");
            html.append("<table><tr><th>Booking ID</th><th>Bus</th><th>Passenger</th><th>Seat No</th><th>Fare</th><th>Action</th></tr>");
            for (Booking b : bookingManager.getAll()) {
                html.append("<tr>")
                    .append("<td>").append(b.getId()).append("</td>")
                    .append("<td>").append(b.getBusNumber()).append("</td>")
                    .append("<td>").append(b.getPassengerId()).append("</td>")
                    .append("<td>").append(b.getSeatNumber()).append("</td>")
                    .append("<td>&#8377;").append(b.getTotalFare()).append("</td>")
                    .append("<td>")
                    .append("<a href='/ticket?id=").append(b.getId()).append("' class='btn'>Ticket</a> ")
                    .append("<form method='POST' style='display:inline;'><input type='hidden' name='id' value='").append(b.getId()).append("'><button class='btn btn-danger' type='submit'>Cancel</button></form>")
                    .append("</td>")
                    .append("</tr>");
            }
            html.append("</table>");
            html.append(getFooter());
            sendResponse(exchange, 200, html.toString());
        }
    }

    class TicketHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = parseForm(exchange.getRequestURI().getQuery());
            String id = query.get("id");
            
            StringBuilder html = new StringBuilder(getHeader("E-Ticket"));
            
            Booking b = bookingManager.get(id);
            if (b == null) {
                html.append("<div class='alert'>Ticket not found</div>");
            } else {
                Bus bus = null;
                try { bus = busManager.get(b.getBusNumber()); } catch(Exception e){}
                Passenger pass = passengerManager.get(b.getPassengerId());
                
                html.append("<div class='card' style='max-width: 600px; margin: 0 auto; background: #fffdfa; border: 2px dashed #ccc;'>");
                html.append("<h1 style='color: #27ae60;'>CONFIRMED TICKET</h1>");
                html.append("<hr>");
                html.append("<h3 style='text-align:left;'>Booking ID: <b>").append(b.getId()).append("</b></h3>");
                if (bus != null) {
                    html.append("<p style='text-align:left;'><b>Route:</b> ").append(bus.getFromRoute()).append(" to ").append(bus.getToRoute()).append("</p>");
                    html.append("<p style='text-align:left;'><b>Bus:</b> ").append(bus.getBusNumber()).append(" (").append(bus.getType()).append(")</p>");
                }
                if (pass != null) {
                    html.append("<p style='text-align:left;'><b>Passenger:</b> ").append(pass.getName()).append(" (").append(pass.getAge()).append(" Yrs, ").append(pass.getGender()).append(")</p>");
                }
                html.append("<h2 style='text-align:left;'>Seat Number: <span style='background:#f1c40f; padding:5px 10px; border-radius:4px;'>").append(b.getSeatNumber()).append("</span></h2>");
                html.append("<h2 style='text-align:right; color:#e74c3c;'>Total Fare: &#8377;").append(b.getTotalFare()).append("</h2>");
                html.append("<button class='btn' onclick='window.print()'>Print Ticket</button>");
                html.append("</div>");
            }
            
            html.append(getFooter());
            sendResponse(exchange, 200, html.toString());
        }
    }
    
    class ReportHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder html = new StringBuilder(getHeader("Reports"));
            html.append("<h2>System Reports</h2>");
            
            html.append("<div class='card'><h3>Total Bookings: ").append(Report.getTotalBookings(bookingManager)).append("</h3></div>");
            
            html.append("<h3>Revenue Per Bus</h3>");
            html.append("<table><tr><th>Bus Number</th><th>Total Revenue Generated</th></tr>");
            Map<String, Double> rev = Report.generateRevenuePerBus(bookingManager);
            double total = 0;
            for (Map.Entry<String, Double> entry : rev.entrySet()) {
                html.append("<tr><td>").append(entry.getKey()).append("</td><td>&#8377;").append(entry.getValue()).append("</td></tr>");
                total += entry.getValue();
            }
            html.append("<tr><th>Total System Revenue</th><th>&#8377;").append(total).append("</th></tr>");
            html.append("</table>");
            
            html.append(getFooter());
            sendResponse(exchange, 200, html.toString());
        }
    }
}
"""

classes["Main.java"] = """
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
"""
classes["README.md"] = """
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
   [http://localhost:6969](http://localhost:6969)
   
Sample data is automatically injected if the database files (`data/*.csv`) are empty.
"""

for path, content in classes.items():
    if path.endswith(".java"):
        with open(os.path.join(src_dir, path), "w", encoding="utf-8") as f:
            f.write(content.strip())
    else:
        with open(os.path.join(r"e:\d drive\antigravity projects\java_project", path), "w", encoding="utf-8") as f:
            f.write(content.strip())
print("Generation complete.")
