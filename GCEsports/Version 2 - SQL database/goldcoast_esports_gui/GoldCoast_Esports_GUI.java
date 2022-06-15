/*
    Filename: GoldCoast_ESports_GUI.java
    Purpose: Manage team and competition data for GC Esports
             Program adds, retrieves, and manipulates data in SQL database
             Program exports competition results and leaderboards to CSV file
    Author: Tim Wickham
    Date Started: 28 Mar 2022
    Version: 0.7 alpha
    License: Creative Commons
    Notes: Works with blank database, can populate data from scratch
           - requires database and tables to exist
           - add game implemented (missing from scope)
    TODO: 
           - change bubble sort to selection sort
    -------------
    Known Issues: 
    -------------
    1) # Sorted combo boxes will display uppercase first character items 
       # in alphabetical order, then lowercase first character items in 
       # alphabetical order
       # POSSIBLE FIX:
       # 1) change first character to uppercase on creation
       # 2) ??? sort based on letter (A,a), rather than ASCII value ???
    2) # User interface elements are not uniform across tabs
       # switching tabs shows elements that should be aligned, changing
       # position or size slightly
       # FIX: resize and realign all offending UI items
 */
package goldcoast_esports_gui;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public final class GoldCoast_Esports_GUI extends javax.swing.JFrame {
    
    // declare variables for use with database
    private String sql;
    private String sqlType;
    private DB_Read db_read;
    private DB_Write db_write;
    
    // declare variables for export to csv
    private Object[][] leaderBoard;
    private Object[][] dataToWrite;
    private String fileToWrite;
        
    // declare Table variables
    private final DefaultTableModel compResultsTable;
    private final DefaultTableModel selectedEventTable;
    private final DefaultTableModel allEventsTable;
    
    // declare ComboBox variables
    private final DefaultComboBoxModel SCR_EventComboBox;
    private final DefaultComboBoxModel SCR_TeamComboBox;
    private final DefaultComboBoxModel NCR_EventComboBox;
    private final DefaultComboBoxModel NCR_GameComboBox;
    private final DefaultComboBoxModel NCR_Team1ComboBox;
    private final DefaultComboBoxModel NCR_Team2ComboBox;
    private final DefaultComboBoxModel UPD_TeamComboBox;
    private final String [] comboBoxArray;
    
    // variable to pause events during processing
    private boolean pauseEvents;
    
    private GoldCoast_Esports_GUI() {
        
        // initialise Table variables 
        compResultsTable = new DefaultTableModel();
        selectedEventTable = new DefaultTableModel();
        allEventsTable = new DefaultTableModel();
        
        // initialise ComboBoxes
        SCR_EventComboBox = new DefaultComboBoxModel();
        SCR_TeamComboBox = new DefaultComboBoxModel();
        NCR_EventComboBox = new DefaultComboBoxModel();
        NCR_GameComboBox = new DefaultComboBoxModel();
        NCR_Team1ComboBox = new DefaultComboBoxModel();
        NCR_Team2ComboBox = new DefaultComboBoxModel();
        UPD_TeamComboBox = new DefaultComboBoxModel();
        
        /* initialise array for combobox population     *
        *  iterate across array to select combobox type *
        *  comboBoxArray[0-1] = event combo boxes       *
        *  comboBoxArray[2-5] = team combo boxes        *
        *  comboBoxArray[6]   = game combo box         */
        comboBoxArray = new String[]{"scr_ecb", "ncr_ecb", "scr_tcb", "ncr_t1cb", "ncr_t2cb", "upd_tcb", "ncr_gcb"};

        // Set column labels for tables
        String[] columnNames_Results = new String[]{"Game", "Team 1", "Pts", "Team 2", "Pts"};
        compResultsTable.setColumnIdentifiers(columnNames_Results);
        String[] columnNames_ChosenEvent = new String[]{"Team", "Total Points - Select Event"};
        selectedEventTable.setColumnIdentifiers(columnNames_ChosenEvent);
        String[] columnNames_AllEvents = new String[]{"Team", "Total Points - All Events"};
        allEventsTable.setColumnIdentifiers(columnNames_AllEvents);
        
        // populate all combo boxes
        for (String cb : comboBoxArray) {
            populateComboBox(cb);
        }
        
        // initialise GUI components
        initComponents();
        
        // populate data fields with values
        updateTeamDataFields();
        newEventDataField();
        
        // show date and location on "new competition result" tab
        dateLocationText();
        
        // display tables
        displayCompResultsTable();
        displayLeaderboardTable();
        
        // resize all table columns
        resizeChosenEventTableColumns();
        resizeAllEventsTableColumns();
        resizeCompResultsTableColumns();    
    }
    
    /*************************************************************************
    Method:     populateComboBox()
    Purpose:    uses comboBoxArray values passed to method to populate 
                specified combo box using switch - case 
    Inputs:     String comboBox
    Outputs:    void
     * @param comboBox
    *************************************************************************/
    
    // suppress warnings about unchecked data type for comboboxes
    @SuppressWarnings("unchecked")
    private void populateComboBox(String comboBox)
    {
        // pause events until processing complete
        pauseEvents = true;
        
        // SELECT query common to combo boxes
        sql = "SELECT name FROM goldcoast_esports.";
        
        // finish SELECT query by combobox type (add table to FROM)
        // and define query type
        switch (comboBox)
        {
            case "ncr_ecb":
            {
                sql += "event;";
                sqlType = "select_event";
                break;
            }
            case "ncr_gcb":
            {
                sql += "game;";
                sqlType = "select_game";
                break;
            }
            // all team combo boxes share case code
            case "scr_tcb":
            case "ncr_t1cb":
            case "ncr_t2cb":
            case "upd_tcb":
            {
                sql += "team;";
                sqlType = "select_team";
                break;
            }
            case "scr_ecb":
            {
                // unique SELECT query this case only, reset sql variable
                sql = "SELECT name, date, location FROM goldcoast_esports.event;";
                sqlType = "select_event_long";
                break;
            }
            // unpause events and exit method if no case resolved
            // shouldn't happen, but protects against broken query
            default:
            {
                pauseEvents = false;
                return;
            }
        }

        // run query
        db_read = new DB_Read(sql, sqlType);

        // populate specified combo box
        if (db_read.getRecordCount() > 0)
        {
            // sort query data array
            Arrays.sort(db_read.getStrDataSet());
            
            switch (comboBox)
            {
                case "scr_ecb":
                {
                    if (SCR_EventComboBox.getSize() > 0) 
                    {
                        SCR_EventComboBox.removeAllElements();
                    }
                    // This combo box has extra item "All Events"
                    SCR_EventComboBox.addElement("All Events");
                    for (int i = 0; i < db_read.getRecordCount(); i++) 
                    {
                        SCR_EventComboBox.addElement(db_read.getStrDataSet()[i]);
                    }
                    break;
                }   
                case "scr_tcb":
                {
                    if (SCR_TeamComboBox.getSize() > 0) 
                    {
                        SCR_TeamComboBox.removeAllElements();
                    }
                    // This combo box has extra item "All Teams"
                    SCR_TeamComboBox.addElement("All Teams");
                    for (int i = 0; i < db_read.getRecordCount(); i++) 
                    {
                        SCR_TeamComboBox.addElement(db_read.getStrDataSet()[i]);
                    }
                    break;
                }
                case "ncr_ecb":
                {
                    if (NCR_EventComboBox.getSize() > 0) 
                    {
                        NCR_EventComboBox.removeAllElements();
                    }
                    for (int i = 0; i < db_read.getRecordCount(); i++) 
                    {
                        NCR_EventComboBox.addElement(db_read.getStrDataSet()[i]);
                    }
                    break;
                }
                case "ncr_gcb":
                {
                    if (NCR_GameComboBox.getSize() > 0) 
                    {
                        NCR_GameComboBox.removeAllElements();
                    }
                    for (int i = 0; i < db_read.getRecordCount(); i++) 
                    {
                        NCR_GameComboBox.addElement(db_read.getStrDataSet()[i]);
                    }
                    break;
                }
                case "ncr_t1cb":
                {
                    if (NCR_Team1ComboBox.getSize() > 0) 
                    {
                        NCR_Team1ComboBox.removeAllElements();
                    }
                    for (int i = 0; i < db_read.getRecordCount(); i++) 
                    {
                        NCR_Team1ComboBox.addElement(db_read.getStrDataSet()[i]);
                    }
                    break;
                }
                case "ncr_t2cb":
                {
                    if (NCR_Team2ComboBox.getSize() > 0) 
                    {
                        NCR_Team2ComboBox.removeAllElements();
                    }
                    for (int i = 0; i < db_read.getRecordCount(); i++) 
                    {
                        NCR_Team2ComboBox.addElement(db_read.getStrDataSet()[i]);
                    }
                    break;
                }
                case "upd_tcb":
                {
                    if (UPD_TeamComboBox.getSize() > 0) 
                    {
                        UPD_TeamComboBox.removeAllElements();
                    }
                    for (int i = 0; i < db_read.getRecordCount(); i++) 
                    {
                        UPD_TeamComboBox.addElement(db_read.getStrDataSet()[i]);
                    }
                    break;
                }
                default:
                    break;
            }
        }
        /* display errors to console */
        if (db_read.getErrorMessage().length() > 0)
        {
            System.out.println(db_read.getErrorMessage());
        }
        
        // processing complete - unpause events
        pauseEvents = false;
    }
    
    /*************************************************************************
    Method:     displyCompResultsTable()
    Purpose:    get selected combo box items on "competition results" tab
                and populate competition results table based on selections
    Inputs:     void
    Outputs:    void
    *************************************************************************/
    private void displayCompResultsTable()
    {
        // only show if there is at least 1 team and 1 event
        if (SCR_EventComboBox.getSize() > 0 && SCR_TeamComboBox.getSize() > 0)
        {
            // build SELECT query based on combo box selections
            sql = "SELECT gameName, team1, team1Points, team2, team2Points FROM goldcoast_esports.competition";
            if (!SCR_EventComboBox.getSelectedItem().equals("All Events") || !SCR_TeamComboBox.getSelectedItem().equals("All Teams"))
            {
                sql += " WHERE ";
                if (!SCR_TeamComboBox.getSelectedItem().equals("All Teams"))
                {
                    sql += "(team1 = '" + SCR_TeamComboBox.getSelectedItem().toString() + 
                            "' OR team2 = '" + SCR_TeamComboBox.getSelectedItem().toString() + "')";
                }
                if (!SCR_EventComboBox.getSelectedItem().equals("All Events") && !SCR_TeamComboBox.getSelectedItem().equals("All Teams"))
                {
                    sql += " AND ";
                }
                if (!SCR_EventComboBox.getSelectedItem().equals("All Events"))
                {
                    // remove date and location info from selected event
                    String tempStr = SCR_EventComboBox.getSelectedItem().toString();
                    String[] tempArr = tempStr.split("\\ \\(");
                    tempStr = tempArr[0];
                    sql += "(eventName = '" + tempStr + "')";
                }
            }
            sql += ";";

            // run query
            db_read = new DB_Read(sql, "select_comp");

            // remove all existing rows
            if (compResultsTable.getRowCount() > 0)
            {
                for (int i = compResultsTable.getRowCount() - 1; i > -1; i--)
                {
                    compResultsTable.removeRow(i);
                }
            }

            // add row data to table
            if (db_read.getRecordCount() > 0)
            {
                if (db_read.getObjDataSet() != null)
                {
                    for (int row = 0; row < db_read.getRecordCount(); row++)
                    {
                        compResultsTable.addRow(db_read.getObjDataSet()[row]);
                    }
                    compResultsTable.fireTableDataChanged();
                }
            }
            /* display errors to console */
            if (db_read.getErrorMessage().length() > 0)
            {
                System.out.println(db_read.getErrorMessage());
            }

            /* place number of records found into text field */
            // create leading spacing based on number size
            String spaceStr;
            if (db_read.getRecordCount() < 10)
            {
                spaceStr = "   ";
            }
            else if (db_read.getRecordCount() < 100)
            {
                spaceStr = "  ";
            }
            else
            {
                spaceStr = " ";
            }
            // add output to text field
            records_JTextField.setText(spaceStr + Integer.toString(db_read.getRecordCount()) + " Records Found");
        }
        // UX for starting with blank database
        // ensures something in field
        if (records_JTextField.getText().isEmpty())
        {
            records_JTextField.setText("   0 Records Found");
        }
    }
        
    
    /*************************************************************************
    Method:     displayLeaderboardTable()
    Purpose:    get selected combo box items on "competition results" tab
                and populate correct leaderboards table based on selections
    Inputs:     void
    Outputs:    void
    *************************************************************************/
    private void displayLeaderboardTable()
    {
        // only show if there is at least 1 team and 1 event
        if (SCR_EventComboBox.getSize() > 0 && SCR_TeamComboBox.getSize() > 0)
        {
            // remove all existing rows from allEventsTable
            if (allEventsTable.getRowCount() > 0)
            {
                for (int i = allEventsTable.getRowCount() - 1; i > -1; i--)
                {
                    allEventsTable.removeRow(i);
                }
            }
            // remove all existing rows from selectedEventTable
            if (selectedEventTable.getRowCount() > 0)
            {
                for (int i = selectedEventTable.getRowCount() - 1; i > -1; i--)
                {
                    selectedEventTable.removeRow(i);
                }
            }

            // build team name array
            sql = "SELECT name FROM goldcoast_esports.team;";
            db_read = new DB_Read(sql, "select_team");
            String [] teamArray = new String[db_read.getRecordCount()];
            for (int i = 0; i < teamArray.length; i++)
            {
                teamArray[i] = db_read.getStrDataSet()[i];
            }

            // build event string for SQL query
            String eventStr;
            if (SCR_EventComboBox.getSelectedItem().equals("All Events"))
            {
                eventStr = "";
            }
            else
            {
                // remove date and location info from selected event
                String tempStr = SCR_EventComboBox.getSelectedItem().toString();
                String[] tempArr = tempStr.split("\\ \\(");
                tempStr = tempArr[0];
                // insert event name into SQL query
                eventStr = "' AND eventName = '" + tempStr;
            }

            // build total points array
            int [] totalPointsArray = new int[db_read.getRecordCount()];
            for (int i = 0; i < teamArray.length; i++)
            {
                // create SQL query to add points
                sql = "(SELECT SUM(team1Points) AS totalPoints FROM goldcoast_esports.competition WHERE team1 = '" +
                        teamArray[i] + eventStr + "') UNION ALL (" +
                        "SELECT SUM(team2Points) AS totalPoints FROM goldcoast_esports.competition" +
                        " WHERE team2 = '" + teamArray[i] + eventStr + "');";
                // run query
                db_read = new DB_Read(sql, "select_points");

                // add results if greater than 0 to totalPointsArray
                /* validation check for null values */
                if (db_read.getRecordCount() > 0)
                {
                    if (db_read.getIntDataSet()[0] > 0)
                    {
                        totalPointsArray[i] += db_read.getIntDataSet()[0];
                    }
                    if (db_read.getIntDataSet()[1] > 0)
                    {
                        totalPointsArray[i] += db_read.getIntDataSet()[1];
                    }
                }
            }
            // sort leaderboard by points
            sortLeaderboard(teamArray, totalPointsArray);
            
            leaderBoard = new Object[teamArray.length][2];
            // choose leaderboard table and build
            for (int row = 0; row < teamArray.length; row++)
            {
                leaderBoard[row][0] = teamArray[row];
                leaderBoard[row][1] = String.valueOf(totalPointsArray[row]);
                if (SCR_EventComboBox.getSelectedItem().equals("All Events"))
                {
                    allEventsTable.addRow(leaderBoard[row]);
                }
                else
                {
                    // does not add rows if top team has no points
                    // keeping table blank
                    if (totalPointsArray[0] > 0)
                    {
                        selectedEventTable.addRow(leaderBoard[row]);
                    }
                }
            }
            if (SCR_EventComboBox.getSelectedItem().equals("All Events"))
            {
                allEventsTable.fireTableDataChanged();
            }
            else
            {
                selectedEventTable.fireTableDataChanged();
            }
        }
    }
    
    /*************************************************************************
    Method:     sortLeaderboard()
    Purpose:    sort leaderboard results by points (bubble sort)
    Inputs:     String[] teamArray, int[] totalpointsArray
    Outputs:    void
     * @param teamArray
     * @param totalPointsArray
    *************************************************************************/
    private void sortLeaderboard(String[] teamArray, int[] totalPointsArray)
    {
        String teamTemp;
        int pointsTemp;
        for (int i = 0; i < teamArray.length - 1; i++) 
        {
            for (int j = 0; j < teamArray.length - 1; j++) 
            {
                if (totalPointsArray[j] < totalPointsArray[j + 1]) 
                {
                    pointsTemp = totalPointsArray[j];
                    teamTemp = teamArray[j];
                    totalPointsArray[j] = totalPointsArray[j + 1];
                    teamArray[j] = teamArray[j + 1];
                    totalPointsArray[j + 1] = pointsTemp;
                    teamArray[j + 1] = teamTemp;
                }
            }
        }
    }
    
    /*************************************************************************
    Method:     resizeCompResultsTableColumns()
    Purpose:    resize table columns for competition results JTable
    Inputs:     void
    Outputs:    void
    *************************************************************************/
    private void resizeCompResultsTableColumns()
    {
        double[] columnWidthPercentage = {0.3f, 0.25f, 0.1f, 0.25f, 0.1f};
        int tableWidth = compResults_JTable.getWidth();
        TableColumn column;
        TableColumnModel tableColumnModel = compResults_JTable.getColumnModel();
        int colsCount = tableColumnModel.getColumnCount();
        for (int i = 0; i < colsCount; i++)
        {
            column = tableColumnModel.getColumn(i);
            float pWidth = Math.round(columnWidthPercentage[i] * tableWidth);
            column.setPreferredWidth((int)pWidth);
        }
    }
    
    /*************************************************************************
    Method:     resizeChosenEventTableColumns()
    Purpose:    resize table columns for selected event JTable
    Inputs:     void
    Outputs:    void
    *************************************************************************/
    private void resizeChosenEventTableColumns()
    {
        double[] columnWidthPercentage = {0.45f, 0.55f};
        int tableWidth = selectedEvent_JTable.getWidth();
        TableColumn column;
        TableColumnModel tableColumnModel = selectedEvent_JTable.getColumnModel();
        int colsCount = tableColumnModel.getColumnCount();
        for (int i = 0; i < colsCount; i++)
        {
            column = tableColumnModel.getColumn(i);
            float pWidth = Math.round(columnWidthPercentage[i] * tableWidth);
            column.setPreferredWidth((int)pWidth);
        }
    }
    
    /*************************************************************************
    Method:     resizeAllEventsTableColumns()
    Purpose:    resize table columns for all events JTable
    Inputs:     void
    Outputs:    void
    *************************************************************************/
    private void resizeAllEventsTableColumns()
    {
        double[] columnWidthPercentage = {0.45f, 0.55f};
        int tableWidth = allEvents_JTable.getWidth();
        TableColumn column;
        TableColumnModel tableColumnModel = allEvents_JTable.getColumnModel();
        int colsCount = tableColumnModel.getColumnCount();
        for (int i = 0; i < colsCount; i++)
        {
            column = tableColumnModel.getColumn(i);
            float pWidth = Math.round(columnWidthPercentage[i] * tableWidth);
            column.setPreferredWidth((int)pWidth);
        }
    }

    /*************************************************************************
    Method:     updateTeamDataFields()
    Purpose:    add data of selected team in combo box on "update team" tab 
                to "update team" tab text fields
                used at program launch to populate fields, otherwise blank
    Inputs:     void
    Outputs:    void
    *************************************************************************/
    private void updateTeamDataFields()
    {
        // check team combobox has items
        if (UPD_TeamComboBox.getSize() > 0)
        {
            // build query
            sql = "SELECT contact, phone, email FROM goldcoast_esports.team " +
                "WHERE name = '" + UPD_TeamComboBox.getSelectedItem().toString() + "';";
            // run query
            db_read = new DB_Read(sql, "select_team_long");
            // set text in data fields
            updateTeamContactName_JTextField.setText(db_read.getStrDataSet()[0]);
            updateTeamContactPhone_JTextField.setText(db_read.getStrDataSet()[1]);
            updateTeamContactEmail_JTextField.setText(db_read.getStrDataSet()[2]);
        }
    }
    
    /*************************************************************************
    Method:     newEventDataFields()
    Purpose:    set default values to "new event" tab text fields
                - today's date and default location
    Inputs:     void
    Outputs:    void
    *************************************************************************/
    private void newEventDataField()
    {
        LocalDate dateObj = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todaysDate = dateObj.format(formatter);
        newEventDate_JTextField.setText(todaysDate);
        newEventLocation_JTextField.setText("TAFE Coomera");
    }

    /*************************************************************************
    Method:     addNewTeam()
    Purpose:    check validity of data and add new team to database
    Inputs:     void
    Outputs:    void
    *************************************************************************/
    private void addNewTeam()
    {
        // get team details
        sql = "SELECT name FROM goldcoast_esports.team;";
        db_read = new DB_Read(sql, "select_team");
        
        /* check validations */
        // team exists - case insensitve
        for (int i = 0; i < db_read.getRecordCount(); i++)
        {
            if (db_read.getStrDataSet()[i].toLowerCase().equals(newTeamName_JTextField.getText().toLowerCase()))
            {
                JOptionPane.showMessageDialog(null, "Team already exists", 
                        "Team Exists", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        // missing data
        String missingFields = "";
        if (newTeamName_JTextField.getText().isEmpty())
        {
            missingFields += "\nTeam Name";
        }
        if (newTeamContactName_JTextField.getText().isEmpty())
        {
            missingFields += "\nContact Name";
        }
        if (newTeamContactPhone_JTextField.getText().isEmpty())
        {
            missingFields += "\nContact Phone Number";
        }
        if (newTeamContactEmail_JTextField.getText().isEmpty())
        {
            missingFields += "\nContact Email Address";
        }
        if (!missingFields.isEmpty())
        {
            JOptionPane.showMessageDialog(null, "Please enter the following data:" + 
                    missingFields, "Missing Data", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // build SQL query
        sql = "INSERT INTO goldcoast_esports.team (name, contact, phone, email) VALUES (" +
                "'" + newTeamName_JTextField.getText() + "', " +
                "'" + newTeamContactName_JTextField.getText() + "', " +
                "'" + newTeamContactPhone_JTextField.getText() + "', " +
                "'" + newTeamContactEmail_JTextField.getText() + "');";
        
        // confirm save
        int yesOrNo = JOptionPane.showConfirmDialog(null, "Adding New Team: " + 
                newTeamName_JTextField.getText() + 
                "\nDo you wish to continue?", "Add New Team", JOptionPane.YES_NO_OPTION);
        if (yesOrNo == JOptionPane.YES_OPTION)
        {
            // write to database
            db_write = new DB_Write(sql);
            if (db_write.getErrorMessage().length() > 0)
            {
                System.out.println(db_write.getErrorMessage());
            }
            else
            {
                // clear fields and display "saved"
                newTeamName_JTextField.setText("");
                newTeamContactName_JTextField.setText("");
                newTeamContactPhone_JTextField.setText("");
                newTeamContactEmail_JTextField.setText("");
                JOptionPane.showMessageDialog(null, "Team Saved to database", 
                        "Team Saved", JOptionPane.PLAIN_MESSAGE);
                // repopulate team combo boxes
                for (int i = 2; i <= 5; i++)
                {
                    populateComboBox(comboBoxArray[i]);
                }
                // repopulate "update team" text fields
                /* populates text fields after first team created
                   also used if new team becomes first alphabetically,
                   as new team is the "selected" team on "update team" tab */
                updateTeamDataFields();
                
                // repopulate tables
                // if new data added to database, tables match selected item
                displayCompResultsTable();
                displayLeaderboardTable();
            }
        }
    }
    
    /*************************************************************************
    Method:     updateTeam()
    Purpose:    check validity of data and update selected team data in database
    Inputs:     void
    Outputs:    void
    *************************************************************************/
    private void updateTeam()
    {
        // do nothing if no team exists (can add else statement)
        // checks for starting with blank database
        if (UPD_TeamComboBox.getSize() > 0)
        {
            /* check validations */
            // missing data
            String missingFields = "";
            if (updateTeamContactName_JTextField.getText().isEmpty())
            {
                missingFields += "\nContact Name";
            }
            if (updateTeamContactPhone_JTextField.getText().isEmpty())
            {
                missingFields += "\nContact Phone Number";
            }
            if (updateTeamContactEmail_JTextField.getText().isEmpty())
            {
                missingFields += "\nContact Email Address";
            }
            if (!missingFields.isEmpty())
            {
                JOptionPane.showMessageDialog(null, "Please enter the following data:" + 
                        missingFields, "Missing Data", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // build SQL query
            sql = "UPDATE goldcoast_esports.team SET " +
                    "contact = '" + updateTeamContactName_JTextField.getText() + "', " +
                    "phone = '" + updateTeamContactPhone_JTextField.getText() + "', " +
                    "email = '" + updateTeamContactEmail_JTextField.getText() + "' " +
                    "WHERE name = '" + UPD_TeamComboBox.getSelectedItem() + "';";

            // confirm save
            int yesOrNo = JOptionPane.showConfirmDialog(null, "Updating Team: " + 
                    UPD_TeamComboBox.getSelectedItem() + 
                    "\nDo you wish to continue?", "Update Team", JOptionPane.YES_NO_OPTION);
            if (yesOrNo == JOptionPane.YES_OPTION)
            {
                // write to database
                db_write = new DB_Write(sql);
                if (db_write.getErrorMessage().length() > 0)
                {
                    System.out.println(db_write.getErrorMessage());
                }
                else
                {
                    // show updated data in fields and display "saved"
                    // ** used to validate database update **
                    updateTeamDataFields();
                    JOptionPane.showMessageDialog(null, "Team Saved to database", 
                            "Team Saved", JOptionPane.PLAIN_MESSAGE);
                }
            }
        }
        /*
        else
        {
            // show message to create a team
        }
        */
    }
    
    /*************************************************************************
    Method:     addNewEvent()
    Purpose:    check validity of data and add new event to database
    Inputs:     void
    Outputs:    void
    *************************************************************************/
    private void addNewEvent()
    {
        // get event details
        sql = "SELECT name FROM goldcoast_esports.event;";
        db_read = new DB_Read(sql, "select_event");
        
        /* check validations */
        // event exists - case insensitive
        for (int i = 0; i < db_read.getRecordCount(); i++)
        {
            if (db_read.getStrDataSet()[i].toLowerCase().equals(
                    newEventName_JTextField.getText().toLowerCase()))
            {
                JOptionPane.showMessageDialog(null, "Event already exists", 
                        "Event Exists", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        // missing data
        String missingFields = "";
        if (newEventName_JTextField.getText().isEmpty())
        {
            missingFields += "\nEvent Name";
        }
        if (newEventDate_JTextField.getText().isEmpty())
        {
            missingFields += "\nEvent Date";
        }
        if (newEventLocation_JTextField.getText().isEmpty())
        {
            missingFields += "\nEvent Location";
        }
        if (!missingFields.isEmpty())
        {
            JOptionPane.showMessageDialog(null, "Please enter the following data:" + 
                    missingFields, "Missing Data", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // date format and checks
        DateTimeFormatter format1 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withResolverStyle(ResolverStyle.STRICT);
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        format2.setLenient(false);
        try 
        {
            // will strictly adhere to date format only
            // will let invalid dates pass
            format1.parse(newEventDate_JTextField.getText());
            
            // will strictly adhere to valid dates only
            // will let non-exact format pass
            format2.parse(newEventDate_JTextField.getText()); 
        }
        // catch incorrect format from format1
        catch (DateTimeException e)
        {
            JOptionPane.showMessageDialog(null, newEventDate_JTextField.getText() + 
                    " is not in the correct format\nyyyy-MM-dd", 
                    "Incorrect Date Format", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // catch invalid dates (eg 31 feb) from format2
        catch (ParseException e)
        {
            JOptionPane.showMessageDialog(null, newEventDate_JTextField.getText() + 
                    " is not a valid date", 
                    "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // build SQL query
        sql = "INSERT INTO goldcoast_esports.event (name, date, location) VALUES (" +
                "'" + newEventName_JTextField.getText() + "', " +
                "'" + newEventDate_JTextField.getText() + "', " +
                "'" + newEventLocation_JTextField.getText() + "');";
        
        // confirm save
        int yesOrNo = JOptionPane.showConfirmDialog(null, "Adding New Event: " + 
                newTeamName_JTextField.getText() + 
                "\nDo you wish to continue?", "Add New Event", JOptionPane.YES_NO_OPTION);
        if (yesOrNo == JOptionPane.YES_OPTION)
        {
            // write to database
            db_write = new DB_Write(sql);
            if (db_write.getErrorMessage().length() > 0)
            {
                System.out.println(db_write.getErrorMessage());
            }
            else
            {
                // clear fields, reset to default and display "saved"
                newEventName_JTextField.setText("");
                newEventDataField();
                JOptionPane.showMessageDialog(null, "Event Saved to database", 
                        "Event Saved", JOptionPane.PLAIN_MESSAGE);
                // repopulate event combo boxes
                for (int i = 0; i <= 1; i++)
                {
                    populateComboBox(comboBoxArray[i]);
                }
                // repopulate tables
                // if new data added to database, tables match selected item
                displayCompResultsTable();
                displayLeaderboardTable();
            }
        }
    }
    
    /*************************************************************************
    Method:     addNewGame()
    Purpose:    check validity of data and add new game to database
    Inputs:     void
    Outputs:    void
    *************************************************************************/
    private void addNewGame()
    {
        // get game details
        sql = "SELECT name FROM goldcoast_esports.game;";
        db_read = new DB_Read(sql, "select_game");
        
        /* check validations */
        // game exists - case insensitive
        for (int i = 0; i < db_read.getRecordCount(); i++)
        {
            if (db_read.getStrDataSet()[i].toLowerCase().equals(addGameName_JTextField.getText().toLowerCase()))
            {
                JOptionPane.showMessageDialog(null, "Game already exists", 
                        "Game Exists", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // missing data
        String missingFields = "";
        if (addGameName_JTextField.getText().isEmpty())
        {
            missingFields += "\nGame Name";
        }
        if (addGameDescription_JTextField.getText().isEmpty())
        {
            missingFields += "\nGame Description";
        }
        if (!missingFields.isEmpty())
        {
            JOptionPane.showMessageDialog(null, "Please enter the following data:" + 
                    missingFields, "Missing Data", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // check description length <= 20 characters
        char[] descTemp = addGameDescription_JTextField.getText().toCharArray();
        if (descTemp.length > 20)
        {
            JOptionPane.showMessageDialog(null, "Description is longer than 20 characters",
                    "Description too long", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // build SQL query
        sql = "INSERT INTO goldcoast_esports.game (name, type) VALUES (" +
                "'" + addGameName_JTextField.getText() + "', " +
                "'" + addGamePlayers_JComboBox.getSelectedItem().toString() + 
                " (" + addGameDescription_JTextField.getText() + ")');";
        
        // confirm save
        int yesOrNo = JOptionPane.showConfirmDialog(null, "Adding New Game: " + 
                addGameName_JTextField.getText() + 
                "\nDo you wish to continue?", "Add New Game", JOptionPane.YES_NO_OPTION);
        if (yesOrNo == JOptionPane.YES_OPTION)
        {
            // write to database
            db_write = new DB_Write(sql);
            if (db_write.getErrorMessage().length() > 0)
            {
                System.out.println(db_write.getErrorMessage());
            }
            else
            {
                // clear fields, reset combo box to default and display "saved"
                addGameName_JTextField.setText("");
                addGameDescription_JTextField.setText("");
                addGamePlayers_JComboBox.setSelectedIndex(0);
                JOptionPane.showMessageDialog(null, "Game Saved to database", 
                        "Game Saved", JOptionPane.PLAIN_MESSAGE);
                // repopulate game combo box
                populateComboBox(comboBoxArray[6]);
            }
        }
    }
    
    /*************************************************************************
    Method:     addNewCompResult()
    Purpose:    check validity of data and save competition result to database
    Inputs:     void
    Outputs:    void
    *************************************************************************/
    private void addNewCompResult()
    {
        // do nothing if any blank combo boxes (can add else statement)
        // checks for starting with blank database
        if (NCR_EventComboBox.getSize() > 0 &&
            NCR_GameComboBox.getSize() > 0 &&
            NCR_Team1ComboBox.getSize() > 0 &&
            NCR_Team2ComboBox.getSize() > 0)
        {
            /* validation checks */
            // check if teams are different 
            if (NCR_Team1ComboBox.getSelectedItem().toString().equals(
                    NCR_Team2ComboBox.getSelectedItem().toString()))
            {
                JOptionPane.showMessageDialog(null, "Teams cannot be the same", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // check data exists in points fields
            if (team1Points_JTextField.getText().isEmpty() || team2Points_JTextField.getText().isEmpty())
            {
                JOptionPane.showMessageDialog(null, "Please enter points", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // check points are numbers
            int points1;
            int points2;
            try
            {
                points1 = Integer.parseInt(team1Points_JTextField.getText());
                points2 = Integer.parseInt(team2Points_JTextField.getText());
            }
            catch (NumberFormatException e)
            {
                JOptionPane.showMessageDialog(null, "Points must be numbers", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // check points between 0 - 2
            if (points1 < 0 || points1 > 2 || points2 < 0 || points2 > 2)
            {
                JOptionPane.showMessageDialog(null, """
                                    Points must be between 0 and 2
                                    2 = Win
                                    1 = Draw
                                    0 = Loss""",  
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // check points = 2
            if (points1 + points2 != 2)
            {
                JOptionPane.showMessageDialog(null, """
                                    Points must be equal to 2
                                    2 = Win
                                    1 = Draw
                                    0 = Loss""", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // check if teams results already exist
            // build SQL query
            sql = "SELECT team1, team2 FROM goldcoast_esports.competition " +
                    "WHERE (eventName = '" + NCR_EventComboBox.getSelectedItem().toString() + "' " +
                    "AND gameName = '" + NCR_GameComboBox.getSelectedItem().toString() + "') AND " +
                    "((team1 = '" + NCR_Team1ComboBox.getSelectedItem().toString() + "' AND " +
                    "team2 = '" + NCR_Team2ComboBox.getSelectedItem().toString() + "') OR " +
                    "(team2 = '" + NCR_Team1ComboBox.getSelectedItem().toString() +"' AND " + 
                    "team1 = '" + NCR_Team2ComboBox.getSelectedItem().toString() + "'));";
            // run query
            db_read = new DB_Read(sql, "");
            // check if record exists and display error
            if (db_read.getRecordCount() > 0)
            {
                JOptionPane.showMessageDialog(null, "Competition result already exists", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // get competition ID
            sql = "SELECT competitionID FROM goldcoast_esports.competition;";
            db_read = new DB_Read (sql, "select_compID");
            // set compID to 1 by default, in case of blank database
            int compID = 1;
            // if competitionID's exist, change compID to next increment
            if (db_read.getRecordCount() > 0)
            {
                Arrays.sort(db_read.getIntDataSet());
                compID = db_read.getIntDataSet()[db_read.getRecordCount() - 1] + 1;
            }
            // build SQL query
            sql = "INSERT INTO goldcoast_esports.competition (competitionID, eventName, gameName, " +
                    "team1, team1Points, team2, team2Points) VALUES (" +
                    "" + compID + ", " +
                    "'" + NCR_EventComboBox.getSelectedItem().toString() + "', " +
                    "'" + NCR_GameComboBox.getSelectedItem().toString() + "', " +
                    "'" + NCR_Team1ComboBox.getSelectedItem().toString() + "', " +
                    "" + points1 + ", " +
                    "'" + NCR_Team2ComboBox.getSelectedItem().toString() + "', " +
                    "" + points2 + ");";

            // confirm save
            int yesOrNo = JOptionPane.showConfirmDialog(null, """
                            Adding New Competition Result
                            Do you wish to continue?""", 
                    "Add New Result", JOptionPane.YES_NO_OPTION);
            if (yesOrNo == JOptionPane.YES_OPTION)
            {
                // write to database
                db_write = new DB_Write(sql);
                if (db_write.getErrorMessage().length() > 0)
                {
                    System.out.println(db_write.getErrorMessage());
                }
                else
                {
                    // clear fields and display "saved"
                    team1Points_JTextField.setText("");
                    team2Points_JTextField.setText("");
                    JOptionPane.showMessageDialog(null, "Competition result saved to database", 
                            "Result Saved", JOptionPane.PLAIN_MESSAGE);
                    // repopulate results tables
                    displayCompResultsTable();
                    displayLeaderboardTable();
                }
            }
        }
        /*
        else
        {
            // show message to create teams, games, and events
        }
        */
    }
    
    /*************************************************************************
    Method:     deteLocationText()
    Purpose:    display date and location of event on 
                "new competition result" tab
    Inputs:     void
    Outputs:    void
    *************************************************************************/
    private void dateLocationText()
    {
        // validate event combo box has items
        if (NCR_EventComboBox.getSize() > 0)
        {
            // build query
            sql = "SELECT date, location FROM goldcoast_esports.event " +
                  "WHERE name = '" + NCR_EventComboBox.getSelectedItem().toString() + "';";
            // run query
            db_read = new DB_Read(sql, "select_date");
            // display text
            dateLocation_JLabel.setText(db_read.getStrDataSet()[0]);
        }
    }
    
    /*************************************************************************
    Method:     exportToCSV()
    Purpose:    selects table, confirms save, and writes to csv file
                csv file will be dependent on which table is showing
                and which button is selected (competition or leaderboard)
    Inputs:     String table
    Outputs:    void
     * @param table
    *************************************************************************/
    private void exportToCSV(String table)
    {
        // set "dataToWrite" to empty
        // required for validation
        dataToWrite = new Object[0][];
        
        switch (table)
        {
            case "comp":
            {
                // recreate db_read data
                displayCompResultsTable();
                // assign filename
                fileToWrite = "competitionTeamScores";
                // build write data
                if (db_read.getRecordCount() > 0)
                {
                    dataToWrite = new Object[db_read.getRecordCount()][];
                    for (int i = 0; i < dataToWrite.length; i++)
                    {
                        dataToWrite[i] = db_read.getObjDataSet()[i];
                    }
                }
                break;
            }
            case "lb":
            {
                // recreate db_read data
                displayLeaderboardTable();
                // validate first team has a score of 1 or more
                int tempInt = Integer.parseInt(leaderBoard[0][1].toString());
                if (db_read.getRecordCount() > 0 && tempInt > 0)
                {
                    // assign filename
                    if (SCR_EventComboBox.getSelectedItem() == "All Events")
                    {
                        fileToWrite = "totalTeamScores";
                    }
                    else 
                    {
                        fileToWrite = "eventTeamScores";
                    }
                    // build write data
                    dataToWrite = new Object[leaderBoard.length][];
                    for (int i = 0; i < dataToWrite.length; i++)
                    {
                        dataToWrite[i] = leaderBoard[i];
                    }
                }
                break;
            }
            default:
                return;
        }

        // do nothing if no table displayed (can add else statement)
        if (dataToWrite.length > 0)
        {
            String directoryToWrite;
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setApproveButtonText("Select Location");
            int option = fileChooser.showDialog(null, null);
            if(option == JFileChooser.APPROVE_OPTION){
               File file = fileChooser.getSelectedFile();
               directoryToWrite = file.getPath();
               directoryToWrite += "\\";
            }else{
               return;
            }
            fileToWrite = directoryToWrite + fileToWrite;
            // confirm save
            int input = JOptionPane.showConfirmDialog(null, "Do you wish to save " + 
                fileToWrite + ".csv?", "Export to CSV", JOptionPane.YES_NO_OPTION);
            if (input == JOptionPane.YES_OPTION)
            {
                
                // save file
                Export_To_CSV.writeCSV(fileToWrite, dataToWrite);
                if (Export_To_CSV.getErrorMessage().length() > 0)
                {
                    System.out.println(Export_To_CSV.getErrorMessage());
                }
                else 
                {
                    JOptionPane.showMessageDialog(null, "File " + fileToWrite + 
                            ".csv Saved", "Saved", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        /*
        else
        {
            // show dialog to ensure a table is displayed
        }
        */
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
        header_JLabel = new javax.swing.JLabel();
        body_JPanel = new javax.swing.JPanel();
        body_JTabbedPane = new javax.swing.JTabbedPane();
        showCompResults_JPanel = new javax.swing.JPanel();
        event_JLabel = new javax.swing.JLabel();
        team_JLabel = new javax.swing.JLabel();
        compResults_JLabel = new javax.swing.JLabel();
        compResults_JScrollPane = new javax.swing.JScrollPane();
        compResults_JTable = new javax.swing.JTable();
        selectedEvent_JScrollPane = new javax.swing.JScrollPane();
        selectedEvent_JTable = new javax.swing.JTable();
        allEvents_JScrollPane = new javax.swing.JScrollPane();
        allEvents_JTable = new javax.swing.JTable();
        leaderBoards_JLabel = new javax.swing.JLabel();
        records_JTextField = new javax.swing.JTextField();
        event_JComboBox = new javax.swing.JComboBox<>();
        team_JComboBox = new javax.swing.JComboBox<>();
        exportCompResults_JButton = new javax.swing.JButton();
        exportLB_JButton = new javax.swing.JButton();
        newCompResult_JPanel = new javax.swing.JPanel();
        newCompResultEvent_JLabel = new javax.swing.JLabel();
        newCompResultGame_JLabel = new javax.swing.JLabel();
        newCompResultTeam1_JLabel = new javax.swing.JLabel();
        newCompResultTeam2_JLabel = new javax.swing.JLabel();
        newCompResultEvent_JComboBox = new javax.swing.JComboBox<>();
        newCompResultGame_JComboBox = new javax.swing.JComboBox<>();
        newCompResultTeam1_JComboBox = new javax.swing.JComboBox<>();
        newCompResultTeam2_JComboBox = new javax.swing.JComboBox<>();
        team1Points_JLabel = new javax.swing.JLabel();
        team2Points_JLabel = new javax.swing.JLabel();
        team1Points_JTextField = new javax.swing.JTextField();
        team2Points_JTextField = new javax.swing.JTextField();
        saveCompResult_JButton = new javax.swing.JButton();
        dateLocation_JLabel = new javax.swing.JLabel();
        addNewTeam_JPanel = new javax.swing.JPanel();
        newTeamName_JLabel = new javax.swing.JLabel();
        newTeamContactName_JLabel = new javax.swing.JLabel();
        newTeamContactPhone_JLabel = new javax.swing.JLabel();
        newTeamContactEmail_JLabel = new javax.swing.JLabel();
        newTeamName_JTextField = new javax.swing.JTextField();
        newTeamContactName_JTextField = new javax.swing.JTextField();
        newTeamContactPhone_JTextField = new javax.swing.JTextField();
        newTeamContactEmail_JTextField = new javax.swing.JTextField();
        saveNewTeam_JButton = new javax.swing.JButton();
        updateTeam_JPanel = new javax.swing.JPanel();
        updateTeamContactName_JTextField = new javax.swing.JTextField();
        updateTeamName_JLabel = new javax.swing.JLabel();
        updateTeamContactPhone_JTextField = new javax.swing.JTextField();
        updateTeamContactEmail_JTextField = new javax.swing.JTextField();
        saveUpdateTeam_JButton = new javax.swing.JButton();
        updateTeamContactName_JLabel = new javax.swing.JLabel();
        updateTeamContactPhone_JLabel = new javax.swing.JLabel();
        updateTeamContactEmail_JLabel = new javax.swing.JLabel();
        updateTeam_JComboBox = new javax.swing.JComboBox<>();
        addNewEvent_JPanel = new javax.swing.JPanel();
        newEventName_JLabel = new javax.swing.JLabel();
        newEventDate_JLabel = new javax.swing.JLabel();
        newEventLocation_JLabel = new javax.swing.JLabel();
        newEventName_JTextField = new javax.swing.JTextField();
        newEventDate_JTextField = new javax.swing.JTextField();
        newEventLocation_JTextField = new javax.swing.JTextField();
        saveNewEvent_JButton = new javax.swing.JButton();
        addNewGame_JPanel = new javax.swing.JPanel();
        addGameName_JLabel = new javax.swing.JLabel();
        addGamePlayers_JLabel = new javax.swing.JLabel();
        addGameDescription_JLabel = new javax.swing.JLabel();
        addGameName_JTextField = new javax.swing.JTextField();
        addGameDescription_JTextField = new javax.swing.JTextField();
        addGamePlayers_JComboBox = new javax.swing.JComboBox<>();
        saveNewGame_JButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        header_JPanel.setPreferredSize(new java.awt.Dimension(940, 129));

        header_JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gc_esports_images/header.png"))); // NOI18N

        javax.swing.GroupLayout header_JPanelLayout = new javax.swing.GroupLayout(header_JPanel);
        header_JPanel.setLayout(header_JPanelLayout);
        header_JPanelLayout.setHorizontalGroup(
            header_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(header_JLabel)
        );
        header_JPanelLayout.setVerticalGroup(
            header_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(header_JPanelLayout.createSequentialGroup()
                .addComponent(header_JLabel)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        body_JPanel.setPreferredSize(new java.awt.Dimension(940, 670));

        body_JTabbedPane.setPreferredSize(new java.awt.Dimension(940, 640));

        showCompResults_JPanel.setPreferredSize(new java.awt.Dimension(640, 632));

        event_JLabel.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        event_JLabel.setText("Event:");

        team_JLabel.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        team_JLabel.setText("Team:");

        compResults_JLabel.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        compResults_JLabel.setText("Competition Results:");

        compResults_JTable.setModel(compResultsTable);
        compResults_JTable.setRowSelectionAllowed(false);
        compResults_JScrollPane.setViewportView(compResults_JTable);

        selectedEvent_JTable.setModel(selectedEventTable);
        selectedEvent_JScrollPane.setViewportView(selectedEvent_JTable);

        allEvents_JTable.setModel(allEventsTable);
        allEvents_JScrollPane.setViewportView(allEvents_JTable);

        leaderBoards_JLabel.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        leaderBoards_JLabel.setText("Event Leader Boards:");

        records_JTextField.setEditable(false);

        event_JComboBox.setModel(SCR_EventComboBox);
        event_JComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                event_JComboBoxItemStateChanged(evt);
            }
        });

        team_JComboBox.setModel(SCR_TeamComboBox);
        team_JComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                team_JComboBoxItemStateChanged(evt);
            }
        });

        exportCompResults_JButton.setText("Export to CSV");
        exportCompResults_JButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportCompResults_JButtonActionPerformed(evt);
            }
        });

        exportLB_JButton.setText("Export to CSV");
        exportLB_JButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportLB_JButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout showCompResults_JPanelLayout = new javax.swing.GroupLayout(showCompResults_JPanel);
        showCompResults_JPanel.setLayout(showCompResults_JPanelLayout);
        showCompResults_JPanelLayout.setHorizontalGroup(
            showCompResults_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(showCompResults_JPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(showCompResults_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(showCompResults_JPanelLayout.createSequentialGroup()
                        .addGroup(showCompResults_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(event_JLabel)
                            .addComponent(team_JLabel))
                        .addGap(18, 18, 18)
                        .addGroup(showCompResults_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(team_JComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 540, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(event_JComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 540, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(showCompResults_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(exportLB_JButton)
                        .addGroup(showCompResults_JPanelLayout.createSequentialGroup()
                            .addGroup(showCompResults_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(showCompResults_JPanelLayout.createSequentialGroup()
                                    .addComponent(records_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(exportCompResults_JButton))
                                .addGroup(showCompResults_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(compResults_JLabel)
                                    .addComponent(compResults_JScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 524, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(29, 29, 29)
                            .addGroup(showCompResults_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(leaderBoards_JLabel)
                                .addComponent(selectedEvent_JScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(allEvents_JScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        showCompResults_JPanelLayout.setVerticalGroup(
            showCompResults_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(showCompResults_JPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(showCompResults_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(event_JLabel)
                    .addComponent(event_JComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(showCompResults_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(team_JLabel)
                    .addComponent(team_JComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(showCompResults_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(compResults_JLabel)
                    .addComponent(leaderBoards_JLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(showCompResults_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(showCompResults_JPanelLayout.createSequentialGroup()
                        .addComponent(selectedEvent_JScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(allEvents_JScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(compResults_JScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(15, 15, 15)
                .addGroup(showCompResults_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(records_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exportCompResults_JButton)
                    .addComponent(exportLB_JButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        body_JTabbedPane.addTab("Competition results", showCompResults_JPanel);

        newCompResultEvent_JLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        newCompResultEvent_JLabel.setText("Event:");

        newCompResultGame_JLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        newCompResultGame_JLabel.setText("Game:");

        newCompResultTeam1_JLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        newCompResultTeam1_JLabel.setText("Team 1:");

        newCompResultTeam2_JLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        newCompResultTeam2_JLabel.setText("Team 2:");

        newCompResultEvent_JComboBox.setModel(NCR_EventComboBox);
        newCompResultEvent_JComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                newCompResultEvent_JComboBoxItemStateChanged(evt);
            }
        });

        newCompResultGame_JComboBox.setModel(NCR_GameComboBox);

        newCompResultTeam1_JComboBox.setModel(NCR_Team1ComboBox);

        newCompResultTeam2_JComboBox.setModel(NCR_Team2ComboBox);

        team1Points_JLabel.setText("Team 1 Points:");

        team2Points_JLabel.setText("Team 2 Points:");

        saveCompResult_JButton.setText("Save");
        saveCompResult_JButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveCompResult_JButtonActionPerformed(evt);
            }
        });

        dateLocation_JLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        javax.swing.GroupLayout newCompResult_JPanelLayout = new javax.swing.GroupLayout(newCompResult_JPanel);
        newCompResult_JPanel.setLayout(newCompResult_JPanelLayout);
        newCompResult_JPanelLayout.setHorizontalGroup(
            newCompResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newCompResult_JPanelLayout.createSequentialGroup()
                .addGap(205, 205, 205)
                .addGroup(newCompResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(newCompResultTeam2_JLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(newCompResultTeam1_JLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(newCompResultGame_JLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(newCompResultEvent_JLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(newCompResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(newCompResultEvent_JComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(newCompResultGame_JComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(newCompResultTeam1_JComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(newCompResultTeam2_JComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(newCompResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(saveCompResult_JButton)
                    .addGroup(newCompResult_JPanelLayout.createSequentialGroup()
                        .addComponent(team1Points_JLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(team1Points_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(newCompResult_JPanelLayout.createSequentialGroup()
                        .addComponent(team2Points_JLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(team2Points_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(dateLocation_JLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(216, Short.MAX_VALUE))
        );
        newCompResult_JPanelLayout.setVerticalGroup(
            newCompResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newCompResult_JPanelLayout.createSequentialGroup()
                .addGap(135, 135, 135)
                .addGroup(newCompResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newCompResultEvent_JLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newCompResultEvent_JComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateLocation_JLabel))
                .addGap(27, 27, 27)
                .addGroup(newCompResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newCompResultGame_JLabel)
                    .addComponent(newCompResultGame_JComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(newCompResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newCompResultTeam1_JLabel)
                    .addComponent(newCompResultTeam1_JComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(team1Points_JLabel)
                    .addComponent(team1Points_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(newCompResult_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newCompResultTeam2_JLabel)
                    .addComponent(newCompResultTeam2_JComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(team2Points_JLabel)
                    .addComponent(team2Points_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(saveCompResult_JButton)
                .addContainerGap(127, Short.MAX_VALUE))
        );

        body_JTabbedPane.addTab("Add new competition result", newCompResult_JPanel);

        newTeamName_JLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        newTeamName_JLabel.setText("New Team Name:");

        newTeamContactName_JLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        newTeamContactName_JLabel.setText("Contact Name:");

        newTeamContactPhone_JLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        newTeamContactPhone_JLabel.setText("Phone Number:");

        newTeamContactEmail_JLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        newTeamContactEmail_JLabel.setText("Email Address:");

        saveNewTeam_JButton.setText("Save");
        saveNewTeam_JButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveNewTeam_JButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout addNewTeam_JPanelLayout = new javax.swing.GroupLayout(addNewTeam_JPanel);
        addNewTeam_JPanel.setLayout(addNewTeam_JPanelLayout);
        addNewTeam_JPanelLayout.setHorizontalGroup(
            addNewTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addNewTeam_JPanelLayout.createSequentialGroup()
                .addGap(239, 239, 239)
                .addGroup(addNewTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(saveNewTeam_JButton)
                    .addGroup(addNewTeam_JPanelLayout.createSequentialGroup()
                        .addGroup(addNewTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(newTeamContactEmail_JLabel)
                            .addComponent(newTeamContactPhone_JLabel)
                            .addComponent(newTeamContactName_JLabel)
                            .addComponent(newTeamName_JLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(addNewTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(newTeamName_JTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                            .addComponent(newTeamContactName_JTextField)
                            .addComponent(newTeamContactPhone_JTextField)
                            .addComponent(newTeamContactEmail_JTextField, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(327, 327, 327))
        );
        addNewTeam_JPanelLayout.setVerticalGroup(
            addNewTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addNewTeam_JPanelLayout.createSequentialGroup()
                .addGap(135, 135, 135)
                .addGroup(addNewTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newTeamName_JLabel)
                    .addComponent(newTeamName_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(addNewTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newTeamContactName_JLabel)
                    .addComponent(newTeamContactName_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(addNewTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newTeamContactPhone_JLabel)
                    .addComponent(newTeamContactPhone_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(addNewTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newTeamContactEmail_JLabel)
                    .addComponent(newTeamContactEmail_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(saveNewTeam_JButton)
                .addContainerGap(136, Short.MAX_VALUE))
        );

        body_JTabbedPane.addTab("Add new team", addNewTeam_JPanel);

        updateTeamName_JLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        updateTeamName_JLabel.setText("Team Name:");

        saveUpdateTeam_JButton.setText("Save");
        saveUpdateTeam_JButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveUpdateTeam_JButtonActionPerformed(evt);
            }
        });

        updateTeamContactName_JLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        updateTeamContactName_JLabel.setText("Contact Name:");

        updateTeamContactPhone_JLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        updateTeamContactPhone_JLabel.setText("Phone Number:");

        updateTeamContactEmail_JLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        updateTeamContactEmail_JLabel.setText("Email Address:");

        updateTeam_JComboBox.setModel(UPD_TeamComboBox);
        updateTeam_JComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateTeam_JComboBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout updateTeam_JPanelLayout = new javax.swing.GroupLayout(updateTeam_JPanel);
        updateTeam_JPanel.setLayout(updateTeam_JPanelLayout);
        updateTeam_JPanelLayout.setHorizontalGroup(
            updateTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(updateTeam_JPanelLayout.createSequentialGroup()
                .addContainerGap(248, Short.MAX_VALUE)
                .addGroup(updateTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(saveUpdateTeam_JButton)
                    .addGroup(updateTeam_JPanelLayout.createSequentialGroup()
                        .addGroup(updateTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(updateTeamContactEmail_JLabel)
                            .addComponent(updateTeamContactPhone_JLabel)
                            .addComponent(updateTeamContactName_JLabel)
                            .addComponent(updateTeamName_JLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(updateTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(updateTeamContactPhone_JTextField)
                            .addComponent(updateTeamContactEmail_JTextField)
                            .addComponent(updateTeamContactName_JTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(updateTeam_JComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 252, Short.MAX_VALUE))))
                .addGap(320, 320, 320))
        );
        updateTeam_JPanelLayout.setVerticalGroup(
            updateTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(updateTeam_JPanelLayout.createSequentialGroup()
                .addGap(135, 135, 135)
                .addGroup(updateTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateTeamName_JLabel)
                    .addComponent(updateTeam_JComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(updateTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateTeamContactName_JLabel)
                    .addComponent(updateTeamContactName_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(updateTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateTeamContactPhone_JLabel)
                    .addComponent(updateTeamContactPhone_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(updateTeam_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateTeamContactEmail_JLabel)
                    .addComponent(updateTeamContactEmail_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(saveUpdateTeam_JButton)
                .addContainerGap(130, Short.MAX_VALUE))
        );

        body_JTabbedPane.addTab("Update existing team", updateTeam_JPanel);

        newEventName_JLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        newEventName_JLabel.setText("New Event Name:");

        newEventDate_JLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        newEventDate_JLabel.setText("Date:");

        newEventLocation_JLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        newEventLocation_JLabel.setText("Location:");

        saveNewEvent_JButton.setText("Save");
        saveNewEvent_JButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveNewEvent_JButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout addNewEvent_JPanelLayout = new javax.swing.GroupLayout(addNewEvent_JPanel);
        addNewEvent_JPanel.setLayout(addNewEvent_JPanelLayout);
        addNewEvent_JPanelLayout.setHorizontalGroup(
            addNewEvent_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addNewEvent_JPanelLayout.createSequentialGroup()
                .addContainerGap(287, Short.MAX_VALUE)
                .addGroup(addNewEvent_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(saveNewEvent_JButton)
                    .addGroup(addNewEvent_JPanelLayout.createSequentialGroup()
                        .addGroup(addNewEvent_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(newEventLocation_JLabel)
                            .addComponent(newEventDate_JLabel)
                            .addComponent(newEventName_JLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(addNewEvent_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(newEventName_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(newEventDate_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(newEventLocation_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(316, 316, 316))
        );
        addNewEvent_JPanelLayout.setVerticalGroup(
            addNewEvent_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addNewEvent_JPanelLayout.createSequentialGroup()
                .addGap(135, 135, 135)
                .addGroup(addNewEvent_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newEventName_JLabel)
                    .addComponent(newEventName_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(addNewEvent_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newEventDate_JLabel)
                    .addComponent(newEventDate_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(addNewEvent_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newEventLocation_JLabel)
                    .addComponent(newEventLocation_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(saveNewEvent_JButton)
                .addContainerGap(186, Short.MAX_VALUE))
        );

        body_JTabbedPane.addTab("Add new event", addNewEvent_JPanel);

        addGameName_JLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        addGameName_JLabel.setText("Game Name:");

        addGamePlayers_JLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        addGamePlayers_JLabel.setText("Players:");

        addGameDescription_JLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        addGameDescription_JLabel.setText("Short Description");

        addGamePlayers_JComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Team", "Team or Solo" }));

        saveNewGame_JButton.setText("Save");
        saveNewGame_JButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveNewGame_JButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout addNewGame_JPanelLayout = new javax.swing.GroupLayout(addNewGame_JPanel);
        addNewGame_JPanel.setLayout(addNewGame_JPanelLayout);
        addNewGame_JPanelLayout.setHorizontalGroup(
            addNewGame_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addNewGame_JPanelLayout.createSequentialGroup()
                .addContainerGap(283, Short.MAX_VALUE)
                .addGroup(addNewGame_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(saveNewGame_JButton)
                    .addGroup(addNewGame_JPanelLayout.createSequentialGroup()
                        .addGroup(addNewGame_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(addGameDescription_JLabel)
                            .addComponent(addGamePlayers_JLabel)
                            .addComponent(addGameName_JLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(addNewGame_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(addGameName_JTextField)
                            .addComponent(addGameDescription_JTextField)
                            .addComponent(addGamePlayers_JComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(320, 320, 320))
        );
        addNewGame_JPanelLayout.setVerticalGroup(
            addNewGame_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addNewGame_JPanelLayout.createSequentialGroup()
                .addGap(135, 135, 135)
                .addGroup(addNewGame_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addGameName_JLabel)
                    .addComponent(addGameName_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(addNewGame_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addGamePlayers_JLabel)
                    .addComponent(addGamePlayers_JComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(addNewGame_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addGameDescription_JLabel)
                    .addComponent(addGameDescription_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(saveNewGame_JButton)
                .addContainerGap(186, Short.MAX_VALUE))
        );

        body_JTabbedPane.addTab("Add new game", addNewGame_JPanel);

        javax.swing.GroupLayout body_JPanelLayout = new javax.swing.GroupLayout(body_JPanel);
        body_JPanel.setLayout(body_JPanelLayout);
        body_JPanelLayout.setHorizontalGroup(
            body_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(body_JTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 940, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        body_JPanelLayout.setVerticalGroup(
            body_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(body_JPanelLayout.createSequentialGroup()
                .addComponent(body_JTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 507, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 2, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(header_JPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(body_JPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(header_JPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(body_JPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // action performed when "save" button used on "update team" tab
    private void saveUpdateTeam_JButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveUpdateTeam_JButtonActionPerformed
        updateTeam();
    }//GEN-LAST:event_saveUpdateTeam_JButtonActionPerformed

    // action performed when "save" button used on "new team" tab
    private void saveNewTeam_JButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveNewTeam_JButtonActionPerformed
        addNewTeam();
    }//GEN-LAST:event_saveNewTeam_JButtonActionPerformed
    
    // action performed when "save" button used on "new event" tab
    private void saveNewEvent_JButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveNewEvent_JButtonActionPerformed
        addNewEvent();
    }//GEN-LAST:event_saveNewEvent_JButtonActionPerformed

    // action performed when "save" button used on "new game" tab
    private void saveNewGame_JButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveNewGame_JButtonActionPerformed
        addNewGame();
    }//GEN-LAST:event_saveNewGame_JButtonActionPerformed

    // action performed when "save" button used on "new competition result" tab
    private void saveCompResult_JButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveCompResult_JButtonActionPerformed
        addNewCompResult();
    }//GEN-LAST:event_saveCompResult_JButtonActionPerformed

    // action performed for "competition results" Export To CSV button
    private void exportCompResults_JButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportCompResults_JButtonActionPerformed
        exportToCSV("comp");
    }//GEN-LAST:event_exportCompResults_JButtonActionPerformed

    // action performed for "leaderboards" Export To CSV button
    private void exportLB_JButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportLB_JButtonActionPerformed
        exportToCSV("lb");
    }//GEN-LAST:event_exportLB_JButtonActionPerformed
    
    // action performed when "team" combo box on "update team" tab is changed
    private void updateTeam_JComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_updateTeam_JComboBoxItemStateChanged
        if(evt.getStateChange() == java.awt.event.ItemEvent.SELECTED)
        {
            // run event if events not paused
            if (!pauseEvents)
            {
                updateTeamDataFields();
            }
        }
    }//GEN-LAST:event_updateTeam_JComboBoxItemStateChanged

    // action performed when "event" combo box on "competition results" tab is changed
    private void event_JComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_event_JComboBoxItemStateChanged
       if(evt.getStateChange() == java.awt.event.ItemEvent.SELECTED)
        {
            // run event if events not paused
            if (!pauseEvents)
            {
                displayCompResultsTable();
                displayLeaderboardTable();
            }
        }
    }//GEN-LAST:event_event_JComboBoxItemStateChanged

    // action performed when "team" combo box on "competition results" tab is changed
    private void team_JComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_team_JComboBoxItemStateChanged
        if(evt.getStateChange() == java.awt.event.ItemEvent.SELECTED)
        {
            // run event if events not paused
            if (!pauseEvents)
            {
                displayCompResultsTable();
                displayLeaderboardTable();
            }
        }
    }//GEN-LAST:event_team_JComboBoxItemStateChanged

    private void newCompResultEvent_JComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_newCompResultEvent_JComboBoxItemStateChanged
        if(evt.getStateChange() == java.awt.event.ItemEvent.SELECTED)
        {
            // run event if events not paused
            if (!pauseEvents)
            {
                dateLocationText();
            }
        }
    }//GEN-LAST:event_newCompResultEvent_JComboBoxItemStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
            java.util.logging.Logger.getLogger(GoldCoast_Esports_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GoldCoast_Esports_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GoldCoast_Esports_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GoldCoast_Esports_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GoldCoast_Esports_GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel addGameDescription_JLabel;
    private javax.swing.JTextField addGameDescription_JTextField;
    private javax.swing.JLabel addGameName_JLabel;
    private javax.swing.JTextField addGameName_JTextField;
    private javax.swing.JComboBox<String> addGamePlayers_JComboBox;
    private javax.swing.JLabel addGamePlayers_JLabel;
    private javax.swing.JPanel addNewEvent_JPanel;
    private javax.swing.JPanel addNewGame_JPanel;
    private javax.swing.JPanel addNewTeam_JPanel;
    private javax.swing.JScrollPane allEvents_JScrollPane;
    private javax.swing.JTable allEvents_JTable;
    private javax.swing.JPanel body_JPanel;
    private javax.swing.JTabbedPane body_JTabbedPane;
    private javax.swing.JLabel compResults_JLabel;
    private javax.swing.JScrollPane compResults_JScrollPane;
    private javax.swing.JTable compResults_JTable;
    private javax.swing.JLabel dateLocation_JLabel;
    private javax.swing.JComboBox<String> event_JComboBox;
    private javax.swing.JLabel event_JLabel;
    private javax.swing.JButton exportCompResults_JButton;
    private javax.swing.JButton exportLB_JButton;
    private javax.swing.JLabel header_JLabel;
    private javax.swing.JPanel header_JPanel;
    private javax.swing.JLabel leaderBoards_JLabel;
    private javax.swing.JComboBox<String> newCompResultEvent_JComboBox;
    private javax.swing.JLabel newCompResultEvent_JLabel;
    private javax.swing.JComboBox<String> newCompResultGame_JComboBox;
    private javax.swing.JLabel newCompResultGame_JLabel;
    private javax.swing.JComboBox<String> newCompResultTeam1_JComboBox;
    private javax.swing.JLabel newCompResultTeam1_JLabel;
    private javax.swing.JComboBox<String> newCompResultTeam2_JComboBox;
    private javax.swing.JLabel newCompResultTeam2_JLabel;
    private javax.swing.JPanel newCompResult_JPanel;
    private javax.swing.JLabel newEventDate_JLabel;
    private javax.swing.JTextField newEventDate_JTextField;
    private javax.swing.JLabel newEventLocation_JLabel;
    private javax.swing.JTextField newEventLocation_JTextField;
    private javax.swing.JLabel newEventName_JLabel;
    private javax.swing.JTextField newEventName_JTextField;
    private javax.swing.JLabel newTeamContactEmail_JLabel;
    private javax.swing.JTextField newTeamContactEmail_JTextField;
    private javax.swing.JLabel newTeamContactName_JLabel;
    private javax.swing.JTextField newTeamContactName_JTextField;
    private javax.swing.JLabel newTeamContactPhone_JLabel;
    private javax.swing.JTextField newTeamContactPhone_JTextField;
    private javax.swing.JLabel newTeamName_JLabel;
    private javax.swing.JTextField newTeamName_JTextField;
    private javax.swing.JTextField records_JTextField;
    private javax.swing.JButton saveCompResult_JButton;
    private javax.swing.JButton saveNewEvent_JButton;
    private javax.swing.JButton saveNewGame_JButton;
    private javax.swing.JButton saveNewTeam_JButton;
    private javax.swing.JButton saveUpdateTeam_JButton;
    private javax.swing.JScrollPane selectedEvent_JScrollPane;
    private javax.swing.JTable selectedEvent_JTable;
    private javax.swing.JPanel showCompResults_JPanel;
    private javax.swing.JLabel team1Points_JLabel;
    private javax.swing.JTextField team1Points_JTextField;
    private javax.swing.JLabel team2Points_JLabel;
    private javax.swing.JTextField team2Points_JTextField;
    private javax.swing.JComboBox<String> team_JComboBox;
    private javax.swing.JLabel team_JLabel;
    private javax.swing.JLabel updateTeamContactEmail_JLabel;
    private javax.swing.JTextField updateTeamContactEmail_JTextField;
    private javax.swing.JLabel updateTeamContactName_JLabel;
    private javax.swing.JTextField updateTeamContactName_JTextField;
    private javax.swing.JLabel updateTeamContactPhone_JLabel;
    private javax.swing.JTextField updateTeamContactPhone_JTextField;
    private javax.swing.JLabel updateTeamName_JLabel;
    private javax.swing.JComboBox<String> updateTeam_JComboBox;
    private javax.swing.JPanel updateTeam_JPanel;
    // End of variables declaration//GEN-END:variables
}
