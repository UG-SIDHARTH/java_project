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
