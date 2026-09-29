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
