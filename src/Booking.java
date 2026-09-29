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
