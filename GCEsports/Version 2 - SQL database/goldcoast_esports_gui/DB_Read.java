/*
    Filename: DB_Read.java
    Purpose: Class for reading from SQL database
    Author: Tim Wickham
    Date Started: 28 Mar 2022
    Version: 1.0
    License: Creative Commons
    Notes: 
 */
package goldcoast_esports_gui;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DB_Read {
    private String db_URL;
    private String db_usrName;
    private String db_pwd;
    private Connection conn;
    private Statement stmt;
    private ResultSet resultset;
    private int recordCount;
    private Object [][] objDataSet;
    private String [] strDataSet;
    private int [] intDataSet;
    private String errorMessage;
    
    public DB_Read (String sql, String queryType)
    {
        recordCount = 0;
        errorMessage = "";
        
        // Get properties from config file
        try
        {
            InputStream input = new FileInputStream("app.config");
            Properties prop = new Properties();
            prop.load(input);
            db_URL = prop.getProperty("dbURL");
            db_usrName = prop.getProperty("usrID");
            db_pwd = prop.getProperty("usrPWD");
        }
        /* display errors to console */
        catch (FileNotFoundException e)
        {
            errorMessage = e.getMessage();
            System.out.println("Error: config file not found\n" + errorMessage);
            System.exit(1);
        }
        catch (IOException e)
        {
            errorMessage = e.getMessage();
            System.out.println("Error: IO Exception\n" + errorMessage);
            System.exit(1);
        }
        catch (Exception e)
        {
            errorMessage = e.getMessage();
            System.out.println("Unknown Error: " + errorMessage);
            System.exit(1);
        }
        
        try
        {
            conn = DriverManager.getConnection(db_URL, db_usrName, db_pwd );
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultset = stmt.executeQuery(sql);
            if (resultset != null)
            {
                resultset.beforeFirst();
                resultset.last();
                recordCount = resultset.getRow();
            }

            if (recordCount > 0)
            {
                int counter = 0;
                objDataSet = new Object[recordCount][];
                strDataSet = new String[recordCount];
                intDataSet = new int[recordCount];
                String strTemp;
                resultset.beforeFirst();
                while (resultset.next())
                {
                    switch (queryType) 
                    {
                        case "select_comp":
                        {
                            Object [] obj = new Object[5];
                            obj[0] = resultset.getString("gameName");
                            obj[1] = resultset.getString("team1");
                            obj[2] = resultset.getInt("team1Points");
                            obj[3] = resultset.getString("team2");
                            obj[4] = resultset.getInt("team2Points");
                            objDataSet[counter] = obj;
                            counter++;
                            break;
                        }
                        case "select_event_long":
                        {
                            strTemp = resultset.getString("name");
                            strTemp += " (";
                            strTemp += resultset.getString("date");
                            strTemp += " ";
                            strTemp += resultset.getString("location");
                            strTemp += ")";
                            strDataSet[counter] = strTemp;
                            counter++;
                            break;
                        }
                        case "select_team_long":
                        {
                            strDataSet = new String[3];
                            strDataSet[0] = resultset.getString("contact");
                            strDataSet[1] = resultset.getString("phone");
                            strDataSet[2] = resultset.getString("email");
                            break;
                        }
                        case "select_points":
                        {
                            intDataSet[counter] = resultset.getInt("totalPoints");
                            counter++;
                            break;
                        }
                        // shared case code
                        case "select_team":
                        case "select_game":
                        case "select_event":
                        {
                            strDataSet[counter] = resultset.getString("name");
                            counter++;
                            break;
                        }
                        case "select_date":
                        {
                            strDataSet[0] = resultset.getString("date") + 
                                    " " + resultset.getString("location");
                            break;
                        }
                        case "select_compID":
                        {
                            intDataSet[counter] = resultset.getInt("competitionID");
                            counter++;
                        }
                        default:
                            break;
                    }
                }
            }
            conn.close();
        }
        /* display errors to console */
        catch (SQLException e)
        {
            errorMessage = e.getMessage();
            System.out.println("SQL Exception: " + errorMessage);
        }
        catch (Exception e)
        {
            errorMessage = e.getMessage();
            System.out.println("Unknown Error: " + errorMessage);
        }
        
    }
    
    // public get methods
    public int getRecordCount()
    {
        return recordCount;
    }
    
    public String getErrorMessage()
    {
        return errorMessage;
    }
    
    public Object[][] getObjDataSet()
    {
        return objDataSet;
    }
    
    public String[] getStrDataSet()
    {
        return strDataSet;
    }
    
    public int[] getIntDataSet()
    {
        return intDataSet;
    }
}
