import java.util.*;

// Module 1: Bus Management
public class BusManager implements Manageable<Bus> {
    private List<Bus> buses = new ArrayList<>();
    private static final String FILE_NAME = "buses.csv";

    public BusManager() {
        loadData();
    }

    @Override
    public void add(Bus item) throws Exception {
        if (get(item.getBusNumber()) != null) {
            throw new InvalidInputException("Bus with number " + item.getBusNumber() + " already exists!");
        }
        buses.add(item);
        saveData();
    }

    @Override
    public void update(Bus item) throws Exception {
        Bus existing = get(item.getBusNumber());
        if (existing == null) throw new BusNotFoundException("Bus not found");
        buses.remove(existing);
        buses.add(item);
        saveData();
    }

    @Override
    public void delete(String id) throws Exception {
        Bus existing = get(id);
        if (existing == null) throw new BusNotFoundException("Bus not found");
        buses.remove(existing);
        saveData();
    }

    @Override
    public Bus get(String id) {
        return buses.stream().filter(b -> b.getBusNumber().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Bus> getAll() {
        return buses;
    }

    private void loadData() {
        List<String> lines = FileManager.loadLines(FILE_NAME);
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 6) {
                String busNumber = parts[0];
                String type = parts[1];
                String from = parts[2];
                String to = parts[3];
                int totalSeats = Integer.parseInt(parts[4]);
                double baseFare = Double.parseDouble(parts[5]);
                
                if (type.equals("AC")) buses.add(new ACBus(busNumber, from, to, totalSeats, baseFare));
                else if (type.equals("Sleeper")) buses.add(new SleeperBus(busNumber, from, to, totalSeats, baseFare));
                else buses.add(new NonACBus(busNumber, from, to, totalSeats, baseFare));
            }
        }
    }

    private void saveData() {
        List<String> lines = new ArrayList<>();
        for (Bus b : buses) {
            lines.add(b.toCsv());
        }
        FileManager.saveLines(FILE_NAME, lines);
    }
}
