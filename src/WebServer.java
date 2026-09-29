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
        server.createContext("/admin", new AdminHandler());
        server.createContext("/logout", new LogoutHandler());
        
        server.setExecutor(null);
    }

    public void start() {
        server.start();
        System.out.println("Web server started at http://localhost:" + server.getAddress().getPort());
    }
    
    private boolean isAdmin(HttpExchange exchange) {
        List<String> cookies = exchange.getRequestHeaders().get("Cookie");
        if (cookies != null) {
            for (String cookie : cookies) {
                if (cookie.contains("session=admin_logged_in")) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void requireAdmin(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Location", "/admin");
        exchange.sendResponseHeaders(302, -1);
    }

    private String getHeader(String title, boolean admin) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>").append(title).append(" - Bus Reservation</title>");
        sb.append("<style>");
        sb.append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f4f7f6; margin: 0; padding: 0; }");
        sb.append("header { background: #2c3e50; color: #ecf0f1; padding: 15px 20px; display: flex; justify-content: space-between; align-items: center; }");
        sb.append("header a { color: #ecf0f1; text-decoration: none; margin-left: 15px; font-weight: bold; }");
        sb.append("header a:hover { color: #3498db; }");
        sb.append(".container { max-width: 1000px; margin: 30px auto; background: white; padding: 25px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }");
        sb.append("h1, h2 { color: #2c3e50; }");
        sb.append("table { width: 100%; border-collapse: collapse; margin-top: 15px; }");
        sb.append("th, td { padding: 12px; border: 1px solid #ddd; text-align: left; }");
        sb.append("th { background: #34495e; color: white; }");
        sb.append(".btn { background: #3498db; color: white; padding: 8px 12px; border: none; border-radius: 4px; cursor: pointer; text-decoration: none; display: inline-block; }");
        sb.append(".btn:hover { background: #2980b9; }");
        sb.append(".btn-danger { background: #e74c3c; }");
        sb.append(".btn-danger:hover { background: #c0392b; }");
        sb.append(".btn-success { background: #2ecc71; }");
        sb.append(".btn-success:hover { background: #27ae60; }");
        sb.append(".form-group { margin-bottom: 15px; }");
        sb.append(".form-group label { display: block; margin-bottom: 5px; font-weight: bold; }");
        sb.append(".form-group input, .form-group select { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }");
        sb.append(".alert { padding: 15px; background-color: #f44336; color: white; margin-bottom: 15px; border-radius: 4px; }");
        sb.append(".seat-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; margin-top: 20px; }");
        sb.append(".seat { padding: 15px; text-align: center; border-radius: 4px; font-weight: bold; color: white; }");
        sb.append(".seat.available { background: #2ecc71; cursor: pointer; }");
        sb.append(".seat.booked { background: #e74c3c; cursor: not-allowed; }");
        sb.append(".card { border: 1px solid #ddd; padding: 20px; border-radius: 8px; text-align: center; margin-bottom:20px; }");
        sb.append("</style>");
        sb.append("<script>function selectSeat(seatNo) { document.getElementById('selectedSeat').value = seatNo; alert('Seat ' + seatNo + ' selected.'); }</script>");
        sb.append("</head><body><header><div><h3>Bus Reservation System</h3></div><div>");
        
        sb.append("<a href='/'>Home</a>");
        sb.append("<a href='/buses'>Buses</a>");
        sb.append("<a href='/book'>Book Ticket</a>");
        sb.append("<a href='/bookings'>Bookings</a>");
        
        if (admin) {
            sb.append("<a href='/passengers'>Passengers</a>");
            sb.append("<a href='/reports'>Reports</a>");
            sb.append("<a href='/logout' style='color: #ff7675; margin-left: 20px;'>Logout</a>");
        }
        
        sb.append("</div></header><div class='container'>");
        return sb.toString();
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
    
    class AdminHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (isAdmin(exchange)) {
                exchange.getResponseHeaders().set("Location", "/");
                exchange.sendResponseHeaders(302, -1);
                return;
            }
            
            String method = exchange.getRequestMethod();
            String error = null;

            if ("POST".equalsIgnoreCase(method)) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                Map<String, String> params = parseForm(br.readLine());
                
                String user = params.get("username");
                String pass = params.get("password");
                
                if ("admin".equals(user) && "admin123".equals(pass)) {
                    exchange.getResponseHeaders().set("Set-Cookie", "session=admin_logged_in; Path=/");
                    exchange.getResponseHeaders().set("Location", "/");
                    exchange.sendResponseHeaders(302, -1);
                    return;
                } else {
                    error = "Invalid credentials!";
                }
            }

            StringBuilder html = new StringBuilder();
            html.append("<html><head><title>Admin Login</title><style>");
            html.append("body { font-family: 'Segoe UI', sans-serif; background: #2c3e50; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }");
            html.append(".login-box { background: white; padding: 40px; border-radius: 8px; box-shadow: 0 10px 25px rgba(0,0,0,0.2); width: 320px; text-align: center; }");
            html.append("input { width: 100%; padding: 12px; margin: 10px 0 20px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }");
            html.append(".btn { background: #2ecc71; color: white; font-weight: bold; padding: 12px 15px; border: none; border-radius: 4px; cursor: pointer; width: 100%; }");
            html.append(".alert { padding: 10px; background-color: #e74c3c; color: white; margin-bottom: 15px; border-radius: 4px; }");
            html.append("</style></head><body>");
            html.append("<div class='login-box'><h2>Secure Admin Access</h2>");
            if (error != null) html.append("<div class='alert'>").append(error).append("</div>");
            html.append("<form method='POST'>");
            html.append("<input type='text' name='username' placeholder='Username (admin)' required>");
            html.append("<input type='password' name='password' placeholder='Password (admin123)' required>");
            html.append("<button type='submit' class='btn'>Login</button>");
            html.append("<p style='margin-top:15px;'><a href='/'>Return Home</a></p>");
            html.append("</form></div></body></html>");

            byte[] bytes = html.toString().getBytes("UTF-8");
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }
    
    class LogoutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Set-Cookie", "session=; Path=/; Max-Age=0");
            exchange.getResponseHeaders().set("Location", "/");
            exchange.sendResponseHeaders(302, -1);
        }
    }

    class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            boolean admin = isAdmin(exchange);
            StringBuilder html = new StringBuilder(getHeader("Dashboard", admin));
            html.append("<h1>Welcome to Bus Reservation System</h1>");
            html.append("<div style='display:flex; gap: 20px; margin-top:20px;'>");
            html.append("<div class='card' style='flex:1'><h2>").append(busManager.getAll().size()).append("</h2><p>Active Routes</p></div>");
            
            if (admin) {
                html.append("<div class='card' style='flex:1'><h2>").append(passengerManager.getAll().size()).append("</h2><p>Passengers</p></div>");
            }
            
            html.append("<div class='card' style='flex:1'><h2>").append(bookingManager.getAll().size()).append("</h2><p>Total Bookings</p></div>");
            html.append("</div>");
            
            if (!admin) {
                html.append("<div style='margin-top:30px; text-align:center;'>");
                html.append("<h3>Ready to travel?</h3>");
                html.append("<a href='/book' class='btn btn-success' style='font-size: 18px; padding: 15px 30px;'>Book a Ticket Now</a>");
                html.append("</div>");
            }
            
            html.append(getFooter());
            sendResponse(exchange, 200, html.toString());
        }
    }

    class BusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            boolean admin = isAdmin(exchange);
            String method = exchange.getRequestMethod();
            String error = null;

            if ("POST".equalsIgnoreCase(method)) {
                if (!admin) { requireAdmin(exchange); return; }
                
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

            StringBuilder html = new StringBuilder(getHeader("Buses", admin));
            if (error != null) {
                html.append("<div class='alert'>").append(error).append("</div>");
            }
            
            if (admin) {
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
                html.append("</form><hr>");
            }

            html.append("<h2>Available Buses</h2>");
            html.append("<table><tr><th>Bus No</th><th>Type</th><th>Route</th><th>Seats</th><th>Base Fare</th>");
            if (admin) html.append("<th>Action</th>");
            html.append("</tr>");
            
            for (Bus b : busManager.getAll()) {
                html.append("<tr>")
                    .append("<td>").append(b.getBusNumber()).append("</td>")
                    .append("<td>").append(b.getType()).append("</td>")
                    .append("<td>").append(b.getFromRoute()).append(" -> ").append(b.getToRoute()).append("</td>")
                    .append("<td>").append(b.getTotalSeats()).append("</td>")
                    .append("<td>&#8377;").append(b.getBaseFare()).append("</td>");
                
                if (admin) {
                    html.append("<td><form method='POST' style='margin:0;'><input type='hidden' name='action' value='delete'><input type='hidden' name='busNumber' value='").append(b.getBusNumber()).append("'><button class='btn btn-danger' type='submit'>Delete</button></form></td>");
                }
                html.append("</tr>");
            }
            html.append("</table>");
            html.append(getFooter());
            sendResponse(exchange, 200, html.toString());
        }
    }

    class PassengerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!isAdmin(exchange)) { requireAdmin(exchange); return; }
            boolean admin = true;
            
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

            StringBuilder html = new StringBuilder(getHeader("Manage Passengers", admin));
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
            boolean admin = isAdmin(exchange);
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

            StringBuilder html = new StringBuilder(getHeader("Book Ticket", admin));
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
            boolean admin = isAdmin(exchange);
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

            StringBuilder html = new StringBuilder(getHeader("All Bookings", admin));
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
            boolean admin = isAdmin(exchange);
            Map<String, String> query = parseForm(exchange.getRequestURI().getQuery());
            String id = query.get("id");
            
            StringBuilder html = new StringBuilder(getHeader("E-Ticket", admin));
            
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
            if (!isAdmin(exchange)) { requireAdmin(exchange); return; }
            boolean admin = true;
            
            StringBuilder html = new StringBuilder(getHeader("Reports", admin));
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
