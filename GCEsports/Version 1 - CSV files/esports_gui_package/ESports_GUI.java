/*
    Filename: ESports_GUI.java
    Purpose: Manage teams and competition data for GC Esports 
    Author: Tim Wickham
    Date: 13 Mar 2022
    Version: 1.0
    License: Creative Commons
    Notes: 
    Known Issues: 
    - Cannot add additional players when updating team, 
      will only allow the same number as initially entered
        Fix: restrict players to exactly 4
 */
package esports_gui_package;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public final class ESports_GUI extends javax.swing.JFrame 
{

    // private data fields
    private final ArrayList<Competition> competitions;
    private final ArrayList<Team> teams;
    private final ArrayList<Player> players;
    private final DefaultTableModel resultsTableModel;
    private final DefaultComboBoxModel teamsComboBoxModel;

    // universal error handling variable
    // TODO: better algorithms than using this
    private boolean error = false;

    public ESports_GUI() 
    {
        // customise JTable(results)
        resultsTableModel = new DefaultTableModel();
        // customise JComboBox(team)
        teamsComboBoxModel = new DefaultComboBoxModel();
        // customised column names
        String[] columnNames_Results = new String[]{"Date", "Location", "Name", "Team", "Points"};
        resultsTableModel.setColumnIdentifiers(columnNames_Results);

        //initialise arraylists
        competitions = new ArrayList();
        teams = new ArrayList();
        players = new ArrayList();

        // read external files
        readData("competitions");
        readData("teams");
        readData("players");
        
        // Generated JFrame code
        initComponents();
        // populate results JTable
        displayJTable();
        // populate teams combobox
        populateTeamsComboBox();
    }

    public void populateTeamsComboBox() 
    {
        // remove items if present
        if (teamsComboBoxModel.getSize() > 0) 
        {
            teamsComboBoxModel.removeAllElements();
        }
        
        if (!teams.isEmpty())
        {
            // sort alphabetically
            String[] teamNamesArray = new String[teams.size()];
            for (int i = 0; i < teams.size(); i++) {
                teamNamesArray[i] = (teams.get(i).getTeamName());
            }
            Arrays.sort(teamNamesArray);

            // populate with team names

            for (int i = 0; i < teams.size(); i++) 
            {
                teamsComboBoxModel.addElement(teamNamesArray[i]);
            }
        }
        
    }

    public void displayJTable() 
    {
        if (!competitions.isEmpty()) 
        {
            Object[][] competitions2DArray = new Object[competitions.size()][];
            // populate array
            for (int i = 0; i < competitions2DArray.length; i++) 
            {
                Object[] competition = new Object[5];
                competition[0] = competitions.get(i).getDate();
                competition[1] = competitions.get(i).getLocation();
                competition[2] = competitions.get(i).getGameName();
                competition[3] = competitions.get(i).getTeamName();
                competition[4] = competitions.get(i).getPoints();
                competitions2DArray[i] = competition;
            }

            // remove existing rows
            if (resultsTableModel.getRowCount() > 0) 
            {
                for (int i = resultsTableModel.getRowCount() - 1; i > -1; i--) 
                {
                    resultsTableModel.removeRow(i);
                }
            }

            // replace new data
            for (Object[] competitions2DArray1 : competitions2DArray) 
            {
                resultsTableModel.addRow(competitions2DArray1);
            }
        }
    }

    // Read data files
    // Inputs: Filename to read without file extension
    public boolean readData(String fileToRead) 
    {
        error = false;
        try 
        {
            FileReader reader = new FileReader(fileToRead + ".csv");
            BufferedReader bufferedreader = new BufferedReader(reader);
            String line;

            while ((line = bufferedreader.readLine()) != null) 
            {
                if (line.length() > 0) 
                {
                    String[] splitData = line.split(",");
                    switch (fileToRead) 
                    {
                        case "competitions": 
                        {
                            String gameName = splitData[0];
                            String date = splitData[1];
                            String location = splitData[2];
                            String teamName = splitData[3];
                            int points = Integer.parseInt(splitData[4]);
                            Competition comp = new Competition(gameName, date, location, teamName, points);
                            competitions.add(comp);
                            break;
                        }
                        case "teams": 
                        {
                            String teamName = splitData[0];
                            String contactName = splitData[1];
                            String contactPhone = splitData[2];
                            String contactEmail = splitData[3];
                            Team team = new Team(teamName, contactName, contactPhone, contactEmail);
                            teams.add(team);
                            break;
                        }
                        case "players": 
                        {
                            String playerName = splitData[0];
                            String teamName = splitData[1];
                            Player player = new Player(playerName, teamName);
                            players.add(player);
                            break;
                        }
                        default:
                            break;
                    } // end switch - case
                } // end if
            } // end while loop
            reader.close();
        } 
        // TODO: meaningful error handling
        // create file if not found
        catch (FileNotFoundException e) 
        {
            int input = JOptionPane.showConfirmDialog(null, "File " + fileToRead + ".csv not found\n"
                    + "Do you wish to create it?", null, JOptionPane.YES_NO_OPTION);
            if (input == JOptionPane.YES_OPTION) {
                writeData(fileToRead);
            }
        } 
        catch (Exception e) 
        {
            error = true;
            System.out.println("Error reading file: " + fileToRead + ".csv");
        }
        return error;
    }


    // Write to data files
    // Inputs: Filename to write without file extension
    public boolean writeData(String fileToWrite) 
    {
        error = false;
        try 
        {
            FileOutputStream outputStream = new FileOutputStream(fileToWrite + ".csv");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            switch (fileToWrite) 
            {
                case "teams":
                    for (int i = 0; i < teams.size(); i++) 
                    {
                        bufferedWriter.write(teams.get(i).toCSVString());
                        bufferedWriter.newLine();
                    }
                    break;
                case "players":
                    for (int i = 0; i < players.size(); i++) 
                    {
                        bufferedWriter.write(players.get(i).toCSVString());
                        bufferedWriter.newLine();
                    }
                    break;
                case "competitions":
                    for (int i = 0; i < competitions.size(); i++) 
                    {
                        bufferedWriter.write(competitions.get(i).toCSVString());
                        bufferedWriter.newLine();
                    }
                    break;
                default:
                    break;
            }
            bufferedWriter.close();
        } 
        // TODO: meaningful error handling
        catch (Exception e) 
        {
            error = true;
            System.out.println("Error writing file: " + fileToWrite + ".csv");
        }
        return error;
    }

    // Clear fields
    // Inouts: tab to clear
    public void clearFields(String tabToClear) 
    {
        if (tabToClear.equals("newteam"))
        {
            addTeamName_JTextField.setText("");
            addContactName_JTextField.setText("");
            addContactPhone_JTextField.setText("");
            addContactEmail_JTextField.setText("");
            addPlayerNames_JTextArea.setText("");
            return;
        }
        if (tabToClear.equals("newresult"))
        {
            newDate_JTextField.setText("");
            newLocation_JTextField.setText("");
            newGame_JTextField.setText("");
            points_JTextField.setText("");
            //return;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        header_JPanel = new javax.swing.JPanel();
        banner_JLabel = new javax.swing.JLabel();
        body_JPanel = new javax.swing.JPanel();
        body_jTabbedPane1 = new javax.swing.JTabbedPane();
        results_JPanel = new javax.swing.JPanel();
        results_JLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        compResults_JTable = new javax.swing.JTable();
        topTeams_JButton = new javax.swing.JButton();
        newResult_JPanel = new javax.swing.JPanel();
        newCompetitonResult_JLabel = new javax.swing.JLabel();
        newDate_JLabel = new javax.swing.JLabel();
        newDate_JTextField = new javax.swing.JTextField();
        newLocation_JTextField = new javax.swing.JTextField();
        newGame_JTextField = new javax.swing.JTextField();
        newLocation_JLabel = new javax.swing.JLabel();
        newGame_JLabel = new javax.swing.JLabel();
        team_JLabel = new javax.swing.JLabel();
        team_JComboBox = new javax.swing.JComboBox<>();
        points_JLabel = new javax.swing.JLabel();
        points_JTextField = new javax.swing.JTextField();
        clearReults_JButton = new javax.swing.JButton();
        saveResult_JButton = new javax.swing.JButton();
        addTeam_JPanel = new javax.swing.JPanel();
        addNeTeam_JLabel = new javax.swing.JLabel();
        teamName_JLabel = new javax.swing.JLabel();
        contactName_JLabel = new javax.swing.JLabel();
        contactPhone_JLabel = new javax.swing.JLabel();
        contactEmail_JLabel = new javax.swing.JLabel();
        playerNames_JLabel = new javax.swing.JLabel();
        addTeamName_JTextField = new javax.swing.JTextField();
        addContactName_JTextField = new javax.swing.JTextField();
        addContactPhone_JTextField = new javax.swing.JTextField();
        addContactEmail_JTextField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        addPlayerNames_JTextArea = new javax.swing.JTextArea();
        saveTeam_JButton = new javax.swing.JButton();
        clearTeams_JButton = new javax.swing.JButton();
        updateTeam_JPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        updatePlayerNames_JTextArea = new javax.swing.JTextArea();
        updateTeam_JLabel = new javax.swing.JLabel();
        udateTeamName_JLabel = new javax.swing.JLabel();
        updateTeam_JButton = new javax.swing.JButton();
        updateContactName_JLabel = new javax.swing.JLabel();
        updateContactPhone_JLabel = new javax.swing.JLabel();
        udateContactEmail_JLabel = new javax.swing.JLabel();
        updatePlayerNames_JLabel = new javax.swing.JLabel();
        updateContactName_JTextField = new javax.swing.JTextField();
        updateContactPhone_JTextField = new javax.swing.JTextField();
        updateContactEmail_JTextField = new javax.swing.JTextField();
        updateTeam_JComboBox = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gold Coast ESports");
        setBackground(new java.awt.Color(255, 255, 255));
        setLocation(new java.awt.Point(0, 0));
        setPreferredSize(new java.awt.Dimension(800, 600));

        header_JPanel.setBackground(new java.awt.Color(255, 255, 255));

        banner_JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esports_images_package/GoldCoastESports_Header.jpg"))); // NOI18N

        javax.swing.GroupLayout header_JPanelLayout = new javax.swing.GroupLayout(header_JPanel);
        header_JPanel.setLayout(header_JPanelLayout);
        header_JPanelLayout.setHorizontalGroup(
            header_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(banner_JLabel)
        );
        header_JPanelLayout.setVerticalGroup(
            header_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(banner_JLabel)
        );

        body_jTabbedPane1.setBackground(new java.awt.Color(153, 153, 153));

        results_JPanel.setBackground(new java.awt.Color(204, 204, 204));

        results_JLabel.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        results_JLabel.setText("Team Competition Results");

        compResults_JTable.setModel(resultsTableModel);
        jScrollPane1.setViewportView(compResults_JTable);

        topTeams_JButton.setText("Display Top Teams");
        topTeams_JButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                topTeams_JButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout results_JPanelLayout = new javax.swing.GroupLayout(results_JPanel);
        results_JPanel.setLayout(results_JPanelLayout);
        results_JPanelLayout.setHorizontalGroup(
            results_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(results_JPanelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(results_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 743, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(results_JPanelLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(results_JLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(29, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, results_JPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(topTeams_JButton)
                .addGap(56, 56, 56))
        );
        results_JPanelLayout.setVerticalGroup(
            results_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(results_JPanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(results_JLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(topTeams_JButton)
                .addContainerGap(150, Short.MAX_VALUE))
        );

        body_jTabbedPane1.addTab("Team Competition Results", results_JPanel);

        newResult_JPanel.setBackground(new java.awt.Color(204, 204, 204));

        newCompetitonResult_JLabel.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        newCompetitonResult_JLabel.setText("Add New Competition Result");
        newCompetitonResult_JLabel.setToolTipText("");

        newDate_JLabel.setText("Date:");

        newLocation_JLabel.setText("Location:");

        newGame_JLabel.setText("Game:");

        team_JLabel.setText("Team:");

        team_JComboBox.setModel(teamsComboBoxModel);

        points_JLabel.setText("Points:");

        clearReults_JButton.setText("Clear");
        clearReults_JButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearReults_JButtonActionPerformed(evt);
            }
        });

        saveResult_JButton.setText("Save Competition Result");
        saveResult_JButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveResult_JButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout newResult_JPanelLayout = new javax.swing.GroupLayout(newResult_JPanel);
        newResult_JPanel.setLayout(newResult_JPanelLayout);
        newResult_JPanelLayout.setHorizontalGroup(
            newResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newResult_JPanelLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(newResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(newCompetitonResult_JLabel)
                    .addGroup(newResult_JPanelLayout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(newResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(newDate_JLabel)
                            .addComponent(newLocation_JLabel)
                            .addComponent(newGame_JLabel)
                            .addComponent(team_JLabel)
                            .addComponent(points_JLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(newResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(newLocation_JTextField)
                            .addComponent(newDate_JTextField)
                            .addComponent(team_JComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(newGame_JTextField)
                            .addGroup(newResult_JPanelLayout.createSequentialGroup()
                                .addGroup(newResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(points_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(newResult_JPanelLayout.createSequentialGroup()
                                        .addComponent(clearReults_JButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(saveResult_JButton)))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(402, 402, 402))
        );
        newResult_JPanelLayout.setVerticalGroup(
            newResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newResult_JPanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(newCompetitonResult_JLabel)
                .addGap(22, 22, 22)
                .addGroup(newResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newDate_JLabel)
                    .addComponent(newDate_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(newResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newLocation_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newLocation_JLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(newResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newGame_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newGame_JLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(newResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(team_JComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(team_JLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(newResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(points_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(points_JLabel))
                .addGap(18, 18, 18)
                .addGroup(newResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clearReults_JButton)
                    .addComponent(saveResult_JButton))
                .addContainerGap(295, Short.MAX_VALUE))
        );

        body_jTabbedPane1.addTab("Add New Competition Result", newResult_JPanel);

        addTeam_JPanel.setBackground(new java.awt.Color(204, 204, 204));

        addNeTeam_JLabel.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        addNeTeam_JLabel.setText("Add New Team");

        teamName_JLabel.setText("Team Name:");

        contactName_JLabel.setText("Contact Person:");

        contactPhone_JLabel.setText("Contact Phone:");

        contactEmail_JLabel.setText("Contact Email:");

        playerNames_JLabel.setText("Player Names:");

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        addPlayerNames_JTextArea.setColumns(20);
        addPlayerNames_JTextArea.setRows(5);
        jScrollPane2.setViewportView(addPlayerNames_JTextArea);

        saveTeam_JButton.setText("Save New Team");
        saveTeam_JButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveTeam_JButtonActionPerformed(evt);
            }
        });

        clearTeams_JButton.setText("Clear");
        clearTeams_JButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearTeams_JButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout addTeam_JPanelLayout = new javax.swing.GroupLayout(addTeam_JPanel);
        addTeam_JPanel.setLayout(addTeam_JPanelLayout);
        addTeam_JPanelLayout.setHorizontalGroup(
            addTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addTeam_JPanelLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(addTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(addTeam_JPanelLayout.createSequentialGroup()
                        .addGroup(addTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(contactName_JLabel)
                            .addComponent(teamName_JLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(contactPhone_JLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(contactEmail_JLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(playerNames_JLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(addTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(addTeam_JPanelLayout.createSequentialGroup()
                                .addComponent(clearTeams_JButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(saveTeam_JButton))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addContactPhone_JTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addContactEmail_JTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addTeamName_JTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addContactName_JTextField, javax.swing.GroupLayout.Alignment.LEADING)))
                    .addComponent(addNeTeam_JLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(425, Short.MAX_VALUE))
        );
        addTeam_JPanelLayout.setVerticalGroup(
            addTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addTeam_JPanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(addNeTeam_JLabel)
                .addGap(22, 22, 22)
                .addGroup(addTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(teamName_JLabel)
                    .addComponent(addTeamName_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(addTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(contactName_JLabel)
                    .addComponent(addContactName_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(addTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addContactPhone_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(contactPhone_JLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(addTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addContactEmail_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(contactEmail_JLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(addTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playerNames_JLabel))
                .addGap(12, 12, 12)
                .addGroup(addTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(saveTeam_JButton)
                    .addComponent(clearTeams_JButton))
                .addContainerGap(227, Short.MAX_VALUE))
        );

        body_jTabbedPane1.addTab("Add New Team", addTeam_JPanel);

        updateTeam_JPanel.setBackground(new java.awt.Color(204, 204, 204));

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        updatePlayerNames_JTextArea.setColumns(20);
        updatePlayerNames_JTextArea.setRows(5);
        jScrollPane3.setViewportView(updatePlayerNames_JTextArea);

        updateTeam_JLabel.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        updateTeam_JLabel.setText("Update Existing Team");

        udateTeamName_JLabel.setText("Team Name:");

        updateTeam_JButton.setText("Update Team");
        updateTeam_JButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateTeam_JButtonActionPerformed(evt);
            }
        });

        updateContactName_JLabel.setText("Contact Person:");

        updateContactPhone_JLabel.setText("Contact Phone:");

        udateContactEmail_JLabel.setText("Contact Email:");

        updatePlayerNames_JLabel.setText("Player Names:");

        updateTeam_JComboBox.setModel(teamsComboBoxModel);
        updateTeam_JComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateTeam_JComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout updateTeam_JPanelLayout = new javax.swing.GroupLayout(updateTeam_JPanel);
        updateTeam_JPanel.setLayout(updateTeam_JPanelLayout);
        updateTeam_JPanelLayout.setHorizontalGroup(
            updateTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(updateTeam_JPanelLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(updateTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(updateTeam_JLabel)
                    .addGroup(updateTeam_JPanelLayout.createSequentialGroup()
                        .addGroup(updateTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(updateContactName_JLabel)
                            .addComponent(udateTeamName_JLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(updateContactPhone_JLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(udateContactEmail_JLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(updatePlayerNames_JLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(updateTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(updateTeam_JButton)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(updateContactPhone_JTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(updateContactEmail_JTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(updateContactName_JTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(updateTeam_JComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(425, Short.MAX_VALUE))
        );
        updateTeam_JPanelLayout.setVerticalGroup(
            updateTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(updateTeam_JPanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(updateTeam_JLabel)
                .addGap(20, 20, 20)
                .addGroup(updateTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(udateTeamName_JLabel)
                    .addComponent(updateTeam_JComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(updateTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateContactName_JLabel)
                    .addComponent(updateContactName_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(updateTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateContactPhone_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updateContactPhone_JLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(updateTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateContactEmail_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(udateContactEmail_JLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(updateTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updatePlayerNames_JLabel))
                .addGap(12, 12, 12)
                .addComponent(updateTeam_JButton)
                .addContainerGap(224, Short.MAX_VALUE))
        );

        body_jTabbedPane1.addTab("Update Existing Team", updateTeam_JPanel);

        javax.swing.GroupLayout body_JPanelLayout = new javax.swing.GroupLayout(body_JPanel);
        body_JPanel.setLayout(body_JPanelLayout);
        body_JPanelLayout.setHorizontalGroup(
            body_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(body_JPanelLayout.createSequentialGroup()
                .addComponent(body_jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 799, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        body_JPanelLayout.setVerticalGroup(
            body_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(body_jTabbedPane1)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(header_JPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(body_JPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(header_JPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(body_JPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void topTeams_JButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_topTeams_JButtonActionPerformed

        String leaderBoardResults = 
            """
            Teams Leaderboard

            Points   Team
            \u2500\u2500\u2500\u2500\u2500\u2500\u253C\u2500\u2500\u2500\u2500\u2500\u2500
            """;
        String spaceStr;

        // initialise bubble sort variables
        int[] resultsArray = new int[teams.size()];
        String[] teamArray = new String[teams.size()];
        // declare bubble sort temp variables
        String teamTemp;
        int resultTemp;

        // create arrays for bubble sort
        for (int i = 0; i < teams.size(); i++) 
        {
            String teamNameStr = teams.get(i).getTeamName();
            int totalPoints = 0;
            for (int j = 0; j < competitions.size(); j++) 
            {
                if (teamNameStr.equals(competitions.get(j).getTeamName())) 
                {
                    totalPoints += competitions.get(j).getPoints();
                }
            }
            resultsArray[i] = totalPoints;
            teamArray[i] = teamNameStr;
        }

        // Bubble Sort results
        for (int i = 0; i < teams.size() - 1; i++) 
        {
            for (int j = 0; j < teams.size() - 1; j++) 
            {
                if (resultsArray[j] < resultsArray[j + 1]) 
                {
                    resultTemp = resultsArray[j];
                    teamTemp = teamArray[j];
                    resultsArray[j] = resultsArray[j + 1];
                    teamArray[j] = teamArray[j + 1];
                    resultsArray[j + 1] = resultTemp;
                    teamArray[j + 1] = teamTemp;
                }
            }
        }

        for (int i = 0; i < teams.size(); i++) 
        {
            if (resultsArray[i] < 10) 
            {
                spaceStr = "     ";
            } 
            else if (resultsArray[i] < 100) 
            {
                spaceStr = "   ";
            } 
            else 
            {
                spaceStr = " ";
            }

            leaderBoardResults += spaceStr + resultsArray[i] + "      " + teamArray[i] + "\n";
        }

        JOptionPane.showMessageDialog(null, leaderBoardResults, "Leaderboard",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_topTeams_JButtonActionPerformed

    private void saveTeam_JButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveTeam_JButtonActionPerformed

        // check if data entered in each field before saving
        error = false;
        String missingFields = "";
        if (addTeamName_JTextField.getText().isEmpty()) 
        {
            error = true;
            missingFields += "Team Name\n";
            /*JOptionPane.showMessageDialog(null, "Please enter a Team Name", null,
                    JOptionPane.ERROR_MESSAGE);*/
        } 
        if (addContactName_JTextField.getText().isEmpty()) 
        {
            error = true;
            missingFields += "Contact Person\n";
            /*JOptionPane.showMessageDialog(null, "Please enter a Contact Name", null,
                    JOptionPane.ERROR_MESSAGE);*/
        } 
        if (addContactPhone_JTextField.getText().isEmpty()) 
        {
            error = true;
            missingFields += "Contact Phone\n";
            /*JOptionPane.showMessageDialog(null, "Please enter a Contact Phone Number", null,
                    JOptionPane.ERROR_MESSAGE);*/
        } 
        if (addContactEmail_JTextField.getText().isEmpty()) 
        {
            error = true;
            missingFields += "Contact Email\n";
            /*JOptionPane.showMessageDialog(null, "Please enter a Contact Email", null,
                    JOptionPane.ERROR_MESSAGE);*/
        } 
        if (addPlayerNames_JTextArea.getText().isEmpty()) 
        {
            error = true;
            missingFields += "Player Names";
            /*JOptionPane.showMessageDialog(null, "Please enter Player Names", null,
                    JOptionPane.ERROR_MESSAGE);*/
        } 
        if (!error)
        {
            for (int i = 0; i < teams.size(); i++)
            {
                if (addTeamName_JTextField.getText().equals(teams.get(i).getTeamName()))
                {
                    JOptionPane.showMessageDialog(null, "Team already exists", null, JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            // TODO (maybe): create new method for Save Cancel option
            // TODO: switch values around for windows 
            String confirmStr = "You are about to save new team: "
                    + addTeamName_JTextField.getText()
                    + "\nDo you wish to save?";
            Object[] options = {"Cancel", "Save"};
            int input = JOptionPane.showOptionDialog(null, confirmStr,
                    "Save Team", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (input == 1) 
            {
                // add team details to arraylist
                Team team = new Team(addTeamName_JTextField.getText(),
                        addContactName_JTextField.getText(),
                        addContactPhone_JTextField.getText(),
                        addContactEmail_JTextField.getText());
                teams.add(team);
                populateTeamsComboBox();
                // add players to arraylist
                String[] playersArray = addPlayerNames_JTextArea.getText().split("\\n");
                for (String playersArray1 : playersArray) 
                {
                    if (playersArray1.length() > 0)
                    {
                        Player player = new Player(playersArray1, addTeamName_JTextField.getText());
                        players.add(player);
                    }
                }

                // write to external files
                writeData("teams");
                writeData("players");

                // if save successful - clear fields and alert success
                // else - keep fields and alert failure
                if (!error) 
                {
                    JOptionPane.showMessageDialog(null, "Team "
                            + addTeamName_JTextField.getText() + " Saved", null,
                            JOptionPane.PLAIN_MESSAGE);
                    clearFields("newteam");
                    populateTeamsComboBox();
                } 
                else 
                {
                    JOptionPane.showMessageDialog(null, "Error! Failed to Save Team "
                            + addTeamName_JTextField.getText(), null,
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else 
        {
            JOptionPane.showMessageDialog(null, "Please enter the following data:\n" + missingFields, null, JOptionPane.ERROR_MESSAGE);
        }
        error = false;
    }//GEN-LAST:event_saveTeam_JButtonActionPerformed


    private void clearTeams_JButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearTeams_JButtonActionPerformed
        clearFields("newteam");
    }//GEN-LAST:event_clearTeams_JButtonActionPerformed

    private void clearReults_JButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearReults_JButtonActionPerformed
        clearFields("newresult");
    }//GEN-LAST:event_clearReults_JButtonActionPerformed

    private void updateTeam_JComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateTeam_JComboBoxActionPerformed
        for (int i = 0; i < teams.size(); i++) {
            if (teamsComboBoxModel.getSelectedItem() == teams.get(i).getTeamName()) 
            {
                updateContactName_JTextField.setText(teams.get(i).getContactName());
                updateContactPhone_JTextField.setText(teams.get(i).getContactPhone());
                updateContactEmail_JTextField.setText(teams.get(i).getContactEmail());
                String playersStr = "";
                for (int j = 0; j < players.size(); j++) {
                    if (players.get(j).getTeamName().equals(teams.get(i).getTeamName())) 
                    {
                        playersStr += players.get(j).getPlayerName() + "\n";
                    }
                }
                updatePlayerNames_JTextArea.setText(playersStr);
            }
        }
    }//GEN-LAST:event_updateTeam_JComboBoxActionPerformed

    private void updateTeam_JButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateTeam_JButtonActionPerformed

        // TODO: code similar to save team - can make new method?
        // check if data entered in each field before saving
        error = false;
        String missingFields = "";
        if (updateContactName_JTextField.getText().isEmpty()) 
        {
            error = true;
            missingFields += "Contact Name\n";
            /*JOptionPane.showMessageDialog(null, "Please enter a Contact Name", null,
                    JOptionPane.ERROR_MESSAGE);*/
        } 
        if (updateContactPhone_JTextField.getText().isEmpty()) 
        {
            error = true;
            missingFields += "Contact Phone\n";
            /*JOptionPane.showMessageDialog(null, "Please enter a Contact Phone Number", null,
                    JOptionPane.ERROR_MESSAGE);*/
        } 
        if (updateContactEmail_JTextField.getText().isEmpty()) 
        {
            error = true;
            missingFields += "Contact Email\n";
            /*JOptionPane.showMessageDialog(null, "Please enter a Contact Email", null,
                    JOptionPane.ERROR_MESSAGE);*/
        } 
        if (updatePlayerNames_JTextArea.getText().isEmpty()) 
        {
            error = true;
            missingFields += "Player Names";
            /*JOptionPane.showMessageDialog(null, "Please enter Player Names", null,
                    JOptionPane.ERROR_MESSAGE);*/
        } 
        if (!error)
        {

            // TODO: create new method for Save Cancel option
            // TODO: switch values around for windows 
            String confirmStr = "You are about to save team: "
                    + teamsComboBoxModel.getSelectedItem()
                    + "\nDo you wish to save?";
            Object[] options = {"Cancel", "Save"};
            int input = JOptionPane.showOptionDialog(null, confirmStr,
                    "Save Team", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (input == 1) {

                for (int i = 0; i < teams.size(); i++)
                {
                    if (teamsComboBoxModel.getSelectedItem() == teams.get(i).getTeamName())
                    {
                        teams.get(i).setContactName(updateContactName_JTextField.getText());
                        teams.get(i).setContactPhone(updateContactPhone_JTextField.getText());
                        teams.get(i).setContactEmail(updateContactEmail_JTextField.getText());
                    }
                }
                String[] playersArray = updatePlayerNames_JTextArea.getText().split("\\n");
                int playerIndex = 0;
                for (int i = 0; i < players.size(); i++) 
                {
                    if (players.get(i).getTeamName().equals(teamsComboBoxModel.getSelectedItem())) 
                    {
                        players.get(i).setPlayerName(playersArray[playerIndex]);
                        playerIndex++;
                    }
                }

                // write to external files
                writeData("teams");
                writeData("players");

                if (!error) 
                {
                    JOptionPane.showMessageDialog(null, "Team "
                            + teamsComboBoxModel.getSelectedItem() + " Saved", null,
                            JOptionPane.PLAIN_MESSAGE);
                } 
                else 
                {
                    JOptionPane.showMessageDialog(null, "Error! Failed to Save Team "
                            + teamsComboBoxModel.getSelectedItem(), null,
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Please enter the following data:\n" + missingFields, null, JOptionPane.ERROR_MESSAGE);
        }
        error = false;
    }//GEN-LAST:event_updateTeam_JButtonActionPerformed

    private void saveResult_JButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveResult_JButtonActionPerformed
        
        error = false;
        String missingFields = "";
        if (newDate_JTextField.getText().isEmpty()) 
        {
            error = true;
            missingFields += "Date\n";
            /*JOptionPane.showMessageDialog(null, "Please enter Date", null,
                    JOptionPane.ERROR_MESSAGE);*/
        } 
        if (newLocation_JTextField.getText().isEmpty()) 
        {
            error = true;
            missingFields += "Location\n";
            /*JOptionPane.showMessageDialog(null, "Please enter Location", null,
                    JOptionPane.ERROR_MESSAGE);*/
        } 
        if (newGame_JTextField.getText().isEmpty()) 
        {
            error = true;
            missingFields += "Game Name\n";
            /*JOptionPane.showMessageDialog(null, "Please enter Game", null,
                    JOptionPane.ERROR_MESSAGE);*/
        } 
        if (points_JTextField.getText().isEmpty()) 
        {
            error = true;
            missingFields += "Points";
            /*JOptionPane.showMessageDialog(null, "Please enter Points", null,
                    JOptionPane.ERROR_MESSAGE);*/
        } 
        if (!error)
        {
            try 
            {
                Integer.parseInt(points_JTextField.getText());
            } 
            catch (Exception e) 
            {
                error = true;
                JOptionPane.showMessageDialog(null, "Points must be a number", null,
                        JOptionPane.ERROR_MESSAGE);
            }
            if (!error && Integer.parseInt(points_JTextField.getText()) > 0) 
            {
                Competition comp = new Competition(newGame_JTextField.getText(),
                        newDate_JTextField.getText(),
                        newLocation_JTextField.getText(),
                        teamsComboBoxModel.getSelectedItem().toString(),
                        Integer.parseInt(points_JTextField.getText()));
                competitions.add(comp);

                writeData("competitions");
                clearFields("newresult");
                displayJTable();
                if (!error) 
                {
                    JOptionPane.showMessageDialog(null, "Competition Result Saved", null,
                            JOptionPane.PLAIN_MESSAGE);
                } 
                else 
                {
                    JOptionPane.showMessageDialog(null, "Error! Failed to Save Result", null,
                            JOptionPane.ERROR_MESSAGE);
                }
            } 
            else if (!error) 
            {
                JOptionPane.showMessageDialog(null, "Points must be greater than zero", null,
                        JOptionPane.ERROR_MESSAGE);
            }  
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Please enter the following data:\n" + missingFields, null, JOptionPane.ERROR_MESSAGE);
        }    
        error = false;
    }//GEN-LAST:event_saveResult_JButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) 
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ESports_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ESports_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ESports_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ESports_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ESports_GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField addContactEmail_JTextField;
    private javax.swing.JTextField addContactName_JTextField;
    private javax.swing.JTextField addContactPhone_JTextField;
    private javax.swing.JLabel addNeTeam_JLabel;
    private javax.swing.JTextArea addPlayerNames_JTextArea;
    private javax.swing.JTextField addTeamName_JTextField;
    private javax.swing.JPanel addTeam_JPanel;
    private javax.swing.JLabel banner_JLabel;
    private javax.swing.JPanel body_JPanel;
    private javax.swing.JTabbedPane body_jTabbedPane1;
    private javax.swing.JButton clearReults_JButton;
    private javax.swing.JButton clearTeams_JButton;
    private javax.swing.JTable compResults_JTable;
    private javax.swing.JLabel contactEmail_JLabel;
    private javax.swing.JLabel contactName_JLabel;
    private javax.swing.JLabel contactPhone_JLabel;
    private javax.swing.JPanel header_JPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel newCompetitonResult_JLabel;
    private javax.swing.JLabel newDate_JLabel;
    private javax.swing.JTextField newDate_JTextField;
    private javax.swing.JLabel newGame_JLabel;
    private javax.swing.JTextField newGame_JTextField;
    private javax.swing.JLabel newLocation_JLabel;
    private javax.swing.JTextField newLocation_JTextField;
    private javax.swing.JPanel newResult_JPanel;
    private javax.swing.JLabel playerNames_JLabel;
    private javax.swing.JLabel points_JLabel;
    private javax.swing.JTextField points_JTextField;
    private javax.swing.JLabel results_JLabel;
    private javax.swing.JPanel results_JPanel;
    private javax.swing.JButton saveResult_JButton;
    private javax.swing.JButton saveTeam_JButton;
    private javax.swing.JLabel teamName_JLabel;
    private javax.swing.JComboBox<String> team_JComboBox;
    private javax.swing.JLabel team_JLabel;
    private javax.swing.JButton topTeams_JButton;
    private javax.swing.JLabel udateContactEmail_JLabel;
    private javax.swing.JLabel udateTeamName_JLabel;
    private javax.swing.JTextField updateContactEmail_JTextField;
    private javax.swing.JLabel updateContactName_JLabel;
    private javax.swing.JTextField updateContactName_JTextField;
    private javax.swing.JLabel updateContactPhone_JLabel;
    private javax.swing.JTextField updateContactPhone_JTextField;
    private javax.swing.JLabel updatePlayerNames_JLabel;
    private javax.swing.JTextArea updatePlayerNames_JTextArea;
    private javax.swing.JButton updateTeam_JButton;
    private javax.swing.JComboBox<String> updateTeam_JComboBox;
    private javax.swing.JLabel updateTeam_JLabel;
    private javax.swing.JPanel updateTeam_JPanel;
    // End of variables declaration//GEN-END:variables
}
