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
