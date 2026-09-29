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
