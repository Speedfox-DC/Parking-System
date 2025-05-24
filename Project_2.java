import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

abstract class ParkingSystem{

    private static final int TOTAL_SLOTS=20;
    protected ArrayList<String>parkedCars=new ArrayList<>();
    protected HashMap<String,String> parkedTime=new HashMap<>();
    protected HashMap<String,String>parkedDate=new HashMap<>();

    public boolean parkCar(String license){
        if(parkedCars.size()>=TOTAL_SLOTS || parkedCars.contains(license)){
            return false;
        }

        parkedCars.add(license);
        String time=LocalTime.now().withNano(0).toString();
        parkedTime.put(license, time);

        DateTimeFormatter dd=DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String date=LocalDate.now().format(dd);
        parkedDate.put(license, date);

        appendToHistory(license,time,date," parked");
        return true;
    }

    public boolean removeCar(String license){
        if(!parkedCars.contains(license)){
            return false;
        }

        parkedCars.remove(license);
        parkedTime.remove(license);
        parkedDate.remove(license);

        String time=LocalTime.now().withNano(0).toString();
        DateTimeFormatter dd=DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String date=LocalDate.now().format(dd);

        appendToHistory(license,time,date," Left");
        return true;
        
    }

    public boolean isCarParked(String license){
        return parkedCars.contains(license);
    }

    public String getTime(String license){
        return parkedTime.get(license);
    }

    public String getDate(String license){
        return parkedDate.get(license);
    }

    public int availableSlots(){
        return TOTAL_SLOTS-parkedCars.size();
    }
    
    protected abstract void appendToHistory(String license, String time, String date, String action);
}

class User{
    protected HashMap<String,String>users=new HashMap<>();
    private final File userFile=new File("user.txt");

    public User(){
        loadUsers();
    }

    public boolean signIn(String userName,String password){
        return users.containsKey(userName) && users.get(userName).equals(password);
    }

    public boolean signUp(String userName,String password){
        if(users.containsKey(userName)){
            return false;
        }
        users.put(userName, password);
        saveUser();
        return true;
    }

    private void saveUser(){
        try(BufferedWriter writer=new BufferedWriter(new FileWriter(userFile))){
            for(Map.Entry<String,String>entry:users.entrySet()){
                writer.write(entry.getKey()+":"+entry.getValue());
                writer.newLine();
            }
        }catch(IOException e){
             JOptionPane.showMessageDialog(null, "Error saving users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUsers(){
        if(!userFile.exists()) return;

        try(BufferedReader reader=new BufferedReader(new FileReader(userFile))){
            String line;
            while((line=reader.readLine())!=null){
                String[] parts=line.split(":");
                if(parts.length==2){
                    users.put(parts[0], parts[1]);
                }
            }
        }catch(IOException e){
            JOptionPane.showMessageDialog(null, "Error loading users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

public class Project_2 extends JFrame{
    
    private ParkingSystem pSystem;
     private JTextField licenseF, searchF;
    private JTable carTable;
    private DefaultTableModel tableModel;
    private JLabel slotLabel;
    private boolean signIn = false;

    private User users;
    private File historyFile;

    private ImageIcon image_1 = new ImageIcon("two.png");
    private ImageIcon image2 = new ImageIcon("log.png");
    private ImageIcon image3 = new ImageIcon("sign.png");
    private ImageIcon image4 = new ImageIcon("src.png");
    private ImageIcon image5 = new ImageIcon("signup.png");
    private ImageIcon image6 = new ImageIcon("signin.png");
    private ImageIcon image7 = new ImageIcon("history.png");

    public Project_2(){
        pSystem=new ParkingSystem() {

            @Override
            protected void appendToHistory(String license, String time, String date, String action) {
              try(BufferedWriter writer=new BufferedWriter(new FileWriter(historyFile,true))){
                writer.write(license+" has "+action+" at "+time+" on "+date);
                writer.newLine();
              }catch(IOException e){
                JOptionPane.showMessageDialog(null, "Failed to save history", "Failed", JOptionPane.ERROR_MESSAGE);
              }
            }
            
        };

        users=new User();
        initializeHistoryFile();

        setTitle("Parking System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("PARKING SYSTEM", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(25, 118, 210));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setPreferredSize(new Dimension(100, 50));
        add(titleLabel, BorderLayout.NORTH);

        
        JPanel leftPanel = new JPanel();
        leftPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(200, 0));
        
        leftPanel.setBackground(new Color(179,229,252));

        JButton signInO = new JButton("Sign In");
        Image rI2=image6.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon image_2=new ImageIcon(rI2);
        signInO.setIcon(image_2);
        signInO.setBackground(new Color(128,222,234));

        JButton signUpO = new JButton("Sign Up");
        Image rI3=image5.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon image_3=new ImageIcon(rI3);
        signUpO.setIcon(image_3);
        signUpO.setBackground(new Color(128,222,234));

        JButton down = new JButton("Download History");
        Image rI4=image7.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon image_4=new ImageIcon(rI4);
        down.setIcon(image_4);
        down.setBackground(new Color(128,222,234));

        JButton historyB = new JButton("View History");
        historyB.setBackground(new Color(128,222,234));

        JButton logO=new JButton("Log out");
        Image rI5=image2.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon image_5=new ImageIcon(rI5);
        logO.setIcon(image_5);
        logO.setBackground(new Color(128,222,234));

        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(signInO);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(signUpO);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(down);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(historyB);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(logO);

        
        JPanel rightPanel = new JPanel(new GridBagLayout());
       
        GridBagConstraints gbc = new GridBagConstraints();

      
        JPanel entryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        entryPanel.setBackground(new Color(224,247,250));
        entryPanel.setBorder(BorderFactory.createTitledBorder("Car Entry Panel"));
        licenseF = new JTextField(15);
        JButton parkB = new JButton("Park");
        parkB.setBackground(new Color(128,222,234));
        JButton removeB = new JButton("Remove");
        removeB.setBackground(new Color(128,222,234));

        entryPanel.add(new JLabel("License Plate:"));
        entryPanel.add(licenseF);
        entryPanel.add(parkB);
        entryPanel.add(removeB);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        rightPanel.add(entryPanel, gbc);

       
        String[] columns = {"Car", "Time", "Date"};
        tableModel = new DefaultTableModel(columns, 0);
        carTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(carTable);
        scrollPane.getViewport().setBackground(new Color(225,245,254));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Parked Cars"));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        rightPanel.add(scrollPane, gbc);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(200);
        add(splitPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(227,242,253));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(227,242,253));
        searchF = new JTextField(15);
        JButton searchB = new JButton("Search");
        Image rI6=image4.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon image_6=new ImageIcon(rI6);
        searchB.setIcon(image_6);
        searchB.setBackground(new Color(128,222,234));

        searchPanel.add(new JLabel("Search License:"));
        searchPanel.add(searchF);
        searchPanel.add(searchB);

        slotLabel = new JLabel("Available Slots: "+pSystem.availableSlots(), JLabel.RIGHT);
        slotLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        footerPanel.add(searchPanel, BorderLayout.WEST);
        footerPanel.add(slotLabel, BorderLayout.EAST);
        add(footerPanel, BorderLayout.SOUTH);

        parkB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(!signIn){
                    JOptionPane.showMessageDialog(null,"Login first","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }

               String license=licenseF.getText().trim();
               if(license.isEmpty()){
                JOptionPane.showMessageDialog(null, "Enter a number First","Error",JOptionPane.ERROR_MESSAGE);
               }else if(pSystem.parkCar(license)){
                tableModel.addRow(new String[]{license,pSystem.getTime(license),pSystem.getDate(license)});
                updateSlotLabel();
               }else{
                if(pSystem.parkedCars.contains(license)){
                    JOptionPane.showMessageDialog(null, "Car already parked");
                }else{
                    JOptionPane.showMessageDialog(null, "No slots available");
                }
               }
               licenseF.setText("");
            }
            
        });

        removeB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if(!signIn){
                    JOptionPane.showMessageDialog(null,"Login first","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String license=licenseF.getText().trim();
                if(license.isEmpty()){
                    JOptionPane.showMessageDialog(null,"Enter a number First","Error",JOptionPane.ERROR_MESSAGE);
                }else if(pSystem.removeCar(license)){
                    removeFromTable(license);
                    updateSlotLabel();
                    JOptionPane.showMessageDialog(null, "Car removed");
                }else{
                    JOptionPane.showMessageDialog(null, "car not found","Warning",JOptionPane.WARNING_MESSAGE);
                }
                licenseF.setText("");
            }
        });

    searchB.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(!signIn){
                    JOptionPane.showMessageDialog(null,"Login first","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
            String search=searchF.getText().trim();

            if(search.isEmpty()){
                JOptionPane.showMessageDialog(null,"Enter a number first","Error",JOptionPane.ERROR_MESSAGE );
            }else if(pSystem.parkedCars.contains(search)){
                JOptionPane.showMessageDialog(null, "Car parked at: "+pSystem.getTime(search));
            }else{
                JOptionPane.showMessageDialog(null, "No car found","Error",JOptionPane.WARNING_MESSAGE);
            }
            searchF.setText("");
        }
        
    });      
    
    historyB.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
           if(!signIn){
            JOptionPane.showMessageDialog(null, "Log in first","Authentication",JOptionPane.ERROR_MESSAGE);
            return;
           }

           displayHistory();
        }
        
    });

    down.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
           if(!signIn){
            JOptionPane.showMessageDialog(null, "Log in first","Authentication",JOptionPane.ERROR_MESSAGE);
            return;
           }

           JFileChooser fileChooser=new JFileChooser();
           int option=fileChooser.showSaveDialog(null);

           if(option==JFileChooser.APPROVE_OPTION){
            File dest=fileChooser.getSelectedFile();
            try(BufferedWriter writer=new BufferedWriter(new FileWriter(dest));
                BufferedReader reader=new BufferedReader(new FileReader(historyFile))){
                    String line;

                    while((line=reader.readLine())!=null){
                        writer.write(line);
                        writer.newLine();
                    }
                    JOptionPane.showMessageDialog(null, "History Downloaded to "+dest.getAbsolutePath());
                }catch(IOException ex){
                     JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
           }
        }
        
    });

    signInO.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
           if(signIn){
            JOptionPane.showMessageDialog(null, "User already signed in");
            return;
           }

           String user=JOptionPane.showInputDialog("Enter user name: ");
           if(user==null || user.isEmpty()) return;
           String pass=JOptionPane.showInputDialog("Enter password");
           if(pass==null || pass.isEmpty()) return;

           if(users.signIn(user, pass)){
            JOptionPane.showMessageDialog(null, "Login successfull");
            signIn=true;
           }else{
            JOptionPane.showMessageDialog(null, "Invalid user or password","Error",JOptionPane.ERROR_MESSAGE);
           }
        }
        
    });

    signUpO.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            String user=JOptionPane.showInputDialog("Enter an user name");
            if(user==null || user.isEmpty()) return;
            String pass=JOptionPane.showInputDialog("Enter a password");
            if(pass==null || pass.isEmpty()) return;

            if(users.signUp(user, pass)){
                JOptionPane.showMessageDialog(null, "New user added");
            }else{
                JOptionPane.showMessageDialog(null, "User already exist","Error",JOptionPane.ERROR_MESSAGE);
            }
        }
        
    });

    logO.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(!signIn){
                JOptionPane.showMessageDialog(null, "Login first","Authentication",JOptionPane.ERROR_MESSAGE);
            }
            int confirm=JOptionPane.showConfirmDialog(null, "Are you want to logout", "Logout", JOptionPane.YES_NO_OPTION);

            if(confirm==JOptionPane.YES_OPTION){
                 signIn=false;
                    pSystem.parkedCars.clear();
                    pSystem.parkedTime.clear();
                    pSystem.parkedDate.clear();
                    tableModel.setRowCount(0);
                    updateSlotLabel();
                    JOptionPane.showMessageDialog(null, "Logout successful");
            }

        }
        
    });

        setVisible(true);
        setIconImage(image_1.getImage());
    }

    private void updateSlotLabel() {
        slotLabel.setText("Available Slots: " + pSystem.availableSlots());
    }

    private void removeFromTable(String license) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).equals(license)) {
                tableModel.removeRow(i);
                break;
            }
        }
    }

    private void displayHistory(){
        try(BufferedReader reader=new BufferedReader(new FileReader(historyFile))){
            StringBuilder sb=new StringBuilder();
            String line;

            while((line=reader.readLine())!=null){
                sb.append(line).append("\n");
            }
            JOptionPane.showMessageDialog(null, sb.length() > 0 ? sb.toString() : "No history available");
        }catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading history: " + e.getMessage());
        }
    }

     private void initializeHistoryFile() {
        historyFile = new File("history.txt");
        if (!historyFile.exists()) {
            try {
                historyFile.createNewFile();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Could not create history file: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new Project_2();
    }
}
