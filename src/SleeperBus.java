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
