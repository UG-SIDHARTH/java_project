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
