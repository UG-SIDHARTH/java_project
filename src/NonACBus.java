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
