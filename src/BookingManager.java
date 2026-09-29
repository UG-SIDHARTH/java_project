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
