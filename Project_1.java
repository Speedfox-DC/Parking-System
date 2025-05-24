import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Project_1 extends JFrame{
    private static final int TOTAL_SLOTS=20;
    private JTextField licenseF,searchF;
    private JTable carTable;
    private DefaultTableModel tableModel;
    private JLabel slotLabel;
    private boolean signIn=false;

    private ArrayList<String>parkedCars=new ArrayList<>();
    private HashMap<String,String>parkedTime=new HashMap<>();
    private HashMap<String,String>parkedDate=new HashMap<>();
    private HashMap<String,String>user=new HashMap<>();

    private File historyFile;
    private final File userFiles=new File("user.txt");

    ImageIcon image_1=new ImageIcon("two.png");
    ImageIcon image2=new ImageIcon("log.png");
    ImageIcon image3=new ImageIcon("sign.png");
    ImageIcon image4=new ImageIcon("src.png");
    ImageIcon image5=new ImageIcon("signup.png");
    ImageIcon image6=new ImageIcon("signin.png");
    ImageIcon image7=new ImageIcon("history.png");

    public Project_1(){
        loadUsers();
        initializeHistoryFile();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(700,700);
        setTitle("Personal Parking System");
        

        JPanel topPanel=new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(129,212,250));

        JLabel label1=new JLabel("Enter License no: ");

        licenseF=new JTextField(15);
        licenseF.setBackground(new Color(227,242,253));
        licenseF.setPreferredSize(new Dimension(30,26));

        JButton parkB=new JButton("Park");
        parkB.setBackground(new Color(178,235,242));
        JButton removeB=new JButton("Remove");
        removeB.setBackground(new Color(178,235,242));
        JButton historyB=new JButton("History");
        historyB.setBackground(new Color(178,235,242));

        JMenuBar bar=new JMenuBar();
        bar.setBackground(new Color(224,242,241));

        JMenu menu=new JMenu("User");
        Image rI2=image3.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);
        ImageIcon image_3=new ImageIcon(rI2);
        menu.setIcon(image_3);
        JMenu others=new JMenu("Others");
        JMenuItem signInO=new JMenuItem("Sign In");
        Image rI6=image6.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);
        ImageIcon image_6=new ImageIcon(rI6);
        signInO.setIcon(image_6);
        JMenuItem signUpO=new JMenuItem("Sign Up");
        Image rI5=image5.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon image_5=new ImageIcon(rI5);
        signUpO.setIcon(image_5);
        JMenuItem logO=new JMenuItem("Log Out");
        Image rI1=image2.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);
        ImageIcon image_2=new ImageIcon(rI1);
        logO.setIcon(image_2);
        JMenuItem down=new JMenuItem("Download History");
        Image rI7=image7.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);
        ImageIcon image_7=new ImageIcon(rI7);
        down.setIcon(image_7);
        menu.add(signInO);
        menu.add(signUpO);
        menu.add(logO);

        others.add(down);

        bar.add(menu);
        bar.add(others);
        setJMenuBar(bar);

        topPanel.add(label1);
        topPanel.add(licenseF);
        topPanel.add(parkB);
        topPanel.add(removeB);
        topPanel.add(historyB);

        
        add(topPanel,BorderLayout.NORTH);

        String[] columnName={"Parked Cars","Parked Time","Parked Date"};
        tableModel=new DefaultTableModel(columnName,0);
        carTable=new JTable(tableModel);
   
        JScrollPane scrollPane=new JScrollPane(carTable);
        scrollPane.getViewport().setBackground(new Color(224,242,241));
       
        
        add(scrollPane);


        JPanel bottomPanel=new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(129,212,250));
        slotLabel=new JLabel("Available Slots: "+(TOTAL_SLOTS-parkedCars.size()));

        JPanel searchPanel=new JPanel(new FlowLayout());
        searchPanel.setBackground(new Color(129,212,250));
        searchF=new JTextField(15);
        searchF.setBackground(new Color(227,242,253));
        searchF.setPreferredSize(new Dimension(18,28));

        JLabel ss=new JLabel("Search: ");
        
        JButton searchB=new JButton("Search");
        Image rI3=image4.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon image_4=new ImageIcon(rI3);
        searchB.setIcon(image_4);

        searchPanel.add(ss);
        searchPanel.add(searchF);
        searchPanel.add(searchB);

        bottomPanel.add(slotLabel,BorderLayout.WEST);
        bottomPanel.add(searchPanel,BorderLayout.EAST);

        add(bottomPanel,BorderLayout.SOUTH);


        parkB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(!signIn){
                    JOptionPane.showMessageDialog(null, "Sign in first","Authentication",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String license=licenseF.getText().trim();
                
                if(license.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Enter a number first");
                }else if(parkedCars.size()>=TOTAL_SLOTS){
                    JOptionPane.showMessageDialog(null, "No slot avilable");
                }else if(parkedCars.contains(license)){
                    JOptionPane.showMessageDialog(null, "Car already parked");
                }else{
                    parkedCars.add(license);
                    String time=LocalTime.now().withNano(0).toString();
                    parkedTime.put(license,time);
                   
                    DateTimeFormatter dd=DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    String date=LocalDate.now().format(dd);
                    parkedDate.put(license, date);

                    tableModel.addRow(new String[]{license,time,date});

                    updatesSlotLabel();
                    appendToHistory(license, time,date,"parked");

                    JOptionPane.showMessageDialog(null, "Car parked :"+license);
                }
                licenseF.setText("");
            }
            
        });

        removeB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(!signIn){
                    JOptionPane.showMessageDialog(null, "Sign in first","Authentication",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String license=licenseF.getText().trim();
                
                if(license.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Enter a number first","Error",JOptionPane.ERROR_MESSAGE);
                }else if(parkedCars.contains(license)){
                    parkedCars.remove(license);
                    parkedTime.remove(license);
                    parkedDate.remove(license);
                    removeFromTable(license);

                    String time=LocalTime.now().withNano(0).toString();
                    DateTimeFormatter dd=DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    String date=LocalDate.now().format(dd);
                    appendToHistory(license,time,date,"Left");

                    updatesSlotLabel();
                    JOptionPane.showMessageDialog(null, "Car removed: "+license);
                }else{
                    JOptionPane.showMessageDialog(null, "Car not found","Warning",JOptionPane.ERROR_MESSAGE);
                }
                licenseF.setText("");
            }
            
        });

        searchB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String search=searchF.getText().trim();

                if(parkedCars.contains(search)){
                    JOptionPane.showMessageDialog(null, "Car parked at: "+parkedTime.get(search));
                }else{
                    JOptionPane.showMessageDialog(null, "No car found");
                }
            }
            
        });

        signInO.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (signIn) {
                    JOptionPane.showMessageDialog(null, "A user is already signed in!");
                    return;
                }
              String userName=JOptionPane.showInputDialog("Enter User name");
              String password=JOptionPane.showInputDialog("Enter password");
              if(userName !=null && password!=null && user.containsKey(userName) && user.get(userName).equals(password)){
                signIn=true;
                JOptionPane.showMessageDialog(null, "Login successful");
              }else{
                JOptionPane.showMessageDialog(null, "Invalid user name or password");
              }
              
            }
            
        });

        signUpO.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               String userName=JOptionPane.showInputDialog("Enter a user name");
               
               if(userName.isEmpty()){
                JOptionPane.showMessageDialog(null, "Enter a user name first");
                return;
               }

               String password=JOptionPane.showInputDialog("Enter a password");

               if(user.containsKey(userName)){
                JOptionPane.showMessageDialog(null, "User name already exist");
               }
               if(!userName.isEmpty() && !password.isEmpty()){
                user.put(userName, password);
                saveUsers();
                JOptionPane.showMessageDialog(null,"New user added");
               }
            }
            
        });

        logO.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               if(!signIn){
                    JOptionPane.showMessageDialog(null, "Sign in first");
                    return;
                }

                int confirm=JOptionPane.showConfirmDialog(null, "Do you want to logout", "Logout confirmation", JOptionPane.YES_NO_OPTION);

                if(confirm==JOptionPane.YES_OPTION){
                    signIn=false;
                    parkedCars.clear();
                    parkedTime.clear();
                    parkedDate.clear();
                    tableModel.setRowCount(0);

                    updatesSlotLabel();

                    JOptionPane.showMessageDialog(null, "Logout successful");
                }
            }
            
        });

        down.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               if(historyFile==null || !historyFile.exists()){
                JOptionPane.showMessageDialog(null, "No history file found!!","No File",JOptionPane.WARNING_MESSAGE);
                return;
               }
               
               JFileChooser fileChooser=new JFileChooser();
               fileChooser.setDialogTitle("Save parking History as:");
               fileChooser.setSelectedFile(new File("Parking_History.txt"));

               int userSelection=fileChooser.showSaveDialog(null);
 
               if(userSelection==JFileChooser.APPROVE_OPTION){
                File destinationFile=fileChooser.getSelectedFile();
                
                try(BufferedReader reader=new BufferedReader(new FileReader(historyFile));
                    BufferedWriter writer=new BufferedWriter(new FileWriter(destinationFile))){
                        String line;
                        while((line=reader.readLine()) != null){
                            writer.write(line);
                            writer.newLine();
                        }
                        JOptionPane.showMessageDialog(null, "History saved to: "+destinationFile.getAbsolutePath());
                    }catch(IOException ex){
                        JOptionPane.showMessageDialog(null, "Error saving file :"+ex.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
                    }
               }
            }
            
        });

        historyB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(!signIn){
                    JOptionPane.showMessageDialog(null, "Sign in first","Authentication",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(historyFile ==null || !historyFile.exists()){
                    JOptionPane.showMessageDialog(null,"NO history file found" );
                    return;
                }

                StringBuilder historyContent=new StringBuilder();
                try(BufferedReader reader=new BufferedReader(new FileReader(historyFile))){
                    String line;
                    while((line=reader.readLine()) != null){
                        historyContent.append(line).append("\n");
                    }

                    JTextArea textArea = new JTextArea(historyContent.toString());
                    textArea.setEditable(false);
                    textArea.setCaretPosition(0);
                    JScrollPane scrollPane=new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(500,500));
                    JOptionPane.showMessageDialog(null, scrollPane, "Parking History", JOptionPane.INFORMATION_MESSAGE);

                }catch(IOException ex){
                    JOptionPane.showMessageDialog(null, "Error reading history: " + ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                }
            }
            
        });

        setIconImage(image_1.getImage());
        setVisible(true);
    }

    private void initializeHistoryFile(){
        File config=new File("config.txt");
        if(config.exists()){
            try(BufferedReader reader=new BufferedReader(new FileReader(config))){
                String path=reader.readLine();
                if(path != null && !path.isEmpty()){
                    historyFile=new File(path);
                    if(!historyFile.exists()){
                        historyFile.createNewFile();
                    }
                    return;
                }
            }catch(IOException ex){
                JOptionPane.showMessageDialog(null, "Error reading config: " + ex.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
            }
        }

        JFileChooser fileChooser=new JFileChooser();
        fileChooser.setDialogTitle("Choose or create a file");
        int result = fileChooser.showSaveDialog(null);
        if(result==JFileChooser.APPROVE_OPTION){
            historyFile=fileChooser.getSelectedFile();

            try{
                if (!historyFile.exists()) {
                    historyFile.createNewFile();
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(config))) {
                    writer.write(historyFile.getAbsolutePath());
                }
            }catch(IOException e){
                JOptionPane.showMessageDialog(null, "Failed to save file path. History won't be saved.","Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void appendToHistory(String license,String time,String date,String action){
        if(historyFile != null){
            try(BufferedWriter writer =new BufferedWriter(new FileWriter(historyFile,true))){
                 writer.write("Car: " + license + " | " + action + " at: " + time+" on:"+date);
                 writer.newLine();
            }catch(IOException e){
                 JOptionPane.showMessageDialog(null, "Failed to write history: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFiles))) {
            for (Map.Entry<String, String> entry : user.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUsers() {
        if (userFiles.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(userFiles))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        user.put(parts[0], parts[1]);
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error loading users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updatesSlotLabel() {
        slotLabel.setText("Available slots: " + (TOTAL_SLOTS - parkedCars.size()));
    }

     private void removeFromTable(String license) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).equals(license)) {
                tableModel.removeRow(i);
                break;
            }
        }
    }


    public static void main(String[] args) {
        new Project_1();
    }
    
}