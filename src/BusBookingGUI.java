import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BusBookingGUI extends JFrame {

    private BusDatabaseDAO busDAO;
    private JTable busTable;
    private DefaultTableModel tableModel;

    private JTextField txtName, txtPhone, txtSeatNumber;

    public BusBookingGUI() {
        busDAO = new BusDatabaseDAO();
        setTitle("Bus Reservation System - Java Swing + JDBC");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
        loadBuses();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top Panel: Table of buses
        tableModel = new DefaultTableModel(new String[]{"Bus Number", "Type", "From", "To", "Seats", "Base Fare"}, 0);
        busTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(busTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel: Booking Form
        JPanel bookingPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        bookingPanel.setBorder(BorderFactory.createTitledBorder("Book a Ticket"));

        bookingPanel.add(new JLabel("Passenger Name:"));
        txtName = new JTextField();
        bookingPanel.add(txtName);

        bookingPanel.add(new JLabel("Phone Number:"));
        txtPhone = new JTextField();
        bookingPanel.add(txtPhone);

        bookingPanel.add(new JLabel("Seat Number:"));
        txtSeatNumber = new JTextField();
        bookingPanel.add(txtSeatNumber);

        JButton btnBook = new JButton("Book Ticket");
        btnBook.addActionListener(new BookTicketListener());
        bookingPanel.add(new JLabel("")); // empty cell
        bookingPanel.add(btnBook);

        mainPanel.add(bookingPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadBuses() {
        tableModel.setRowCount(0); // clear existing
        List<Bus> buses = busDAO.getAllBuses();
        for (Bus bus : buses) {
            // Demonstration of OOP: Polymorphism is used here as calculateFare() depends on the actual class (AC, NonAC, Sleeper)
            tableModel.addRow(new Object[]{
                    bus.getBusNumber(),
                    bus.getType(),
                    bus.getFromRoute(),
                    bus.getToRoute(),
                    bus.getTotalSeats(),
                    bus.calculateFare() // Polymorphic call
            });
        }
    }

    private class BookTicketListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = busTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(BusBookingGUI.this, "Please select a bus from the table first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String busNumber = tableModel.getValueAt(selectedRow, 0).toString();
            double fare = Double.parseDouble(tableModel.getValueAt(selectedRow, 5).toString());

            String name = txtName.getText();
            String phone = txtPhone.getText();
            String seatStr = txtSeatNumber.getText();

            if (name.isEmpty() || phone.isEmpty() || seatStr.isEmpty()) {
                JOptionPane.showMessageDialog(BusBookingGUI.this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int seatNumber;
            try {
                seatNumber = Integer.parseInt(seatStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(BusBookingGUI.this, "Seat number must be an integer.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Perform Data Processing & Storage via JDBC
            boolean success = busDAO.bookTicket(busNumber, name, phone, seatNumber, fare);
            
            if (success) {
                JOptionPane.showMessageDialog(BusBookingGUI.this, "Booking Successful!\nBus: " + busNumber + "\nFare Paid: $" + fare, "Success", JOptionPane.INFORMATION_MESSAGE);
                txtName.setText("");
                txtPhone.setText("");
                txtSeatNumber.setText("");
            } else {
                JOptionPane.showMessageDialog(BusBookingGUI.this, "Booking Failed. Please verify database connection.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        // Run GUI in Event Dispatching Thread
        SwingUtilities.invokeLater(() -> {
            new BusBookingGUI().setVisible(true);
        });
    }
}
