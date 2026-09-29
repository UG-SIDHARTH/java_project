import java.util.*;

// Module 2: Passenger Management
public class PassengerManager implements Manageable<Passenger> {
    private List<Passenger> passengers = new ArrayList<>();
    private static final String FILE_NAME = "passengers.csv";

    public PassengerManager() {
        loadData();
    }

    @Override
    public void add(Passenger item) throws Exception {
        if (get(item.getId()) != null) {
            throw new InvalidInputException("Passenger ID already exists!");
        }
        passengers.add(item);
        saveData();
    }

    @Override
    public void update(Passenger item) throws Exception {
        Passenger existing = get(item.getId());
        if (existing == null) throw new InvalidInputException("Passenger not found");
        passengers.remove(existing);
        passengers.add(item);
        saveData();
    }

    @Override
    public void delete(String id) throws Exception {
        Passenger existing = get(id);
        if (existing != null) {
            passengers.remove(existing);
            saveData();
        }
    }

    @Override
    public Passenger get(String id) {
        return passengers.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Passenger> getAll() {
        return passengers;
    }

    private void loadData() {
        List<String> lines = FileManager.loadLines(FILE_NAME);
        for (String line : lines) {
            Passenger p = Passenger.fromCsv(line);
            if (p != null) passengers.add(p);
        }
    }

    private void saveData() {
        List<String> lines = new ArrayList<>();
        for (Passenger p : passengers) {
            lines.add(p.toCsv());
        }
        FileManager.saveLines(FILE_NAME, lines);
    }
}
