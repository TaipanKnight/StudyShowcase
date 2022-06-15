/*
    Filename: DB_Write.java
    Purpose: Class for writing to SQL database
    Author: Tim Wickham
    Date Started: 28 Mar 2022
    Version: 1.0
    License: Creative Commons
    Notes: 
 */
package goldcoast_esports_gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DB_Write {
    private String db_URL;
    private String db_usrName;
    private String db_pwd;
    private Connection conn;
    private Statement stmt;
    private String errorMessage;
    
    public DB_Write(String sql)
    {
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
            conn = DriverManager.getConnection(db_URL, db_usrName, db_pwd);
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
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
    
    public String getErrorMessage()
    {
        return errorMessage;
    }
}
