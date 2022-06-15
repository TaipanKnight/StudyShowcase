/*
    Filename: Export_To_CSV.java
    Purpose: Class for writing to CSV file
    Author: Tim Wickham
    Date Started: 28 Mar 2022
    Version: 1.0
    License: Creative Commons
    Notes: 
 */
package goldcoast_esports_gui;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Export_To_CSV {
    
    private static String errorMessage;
    private static String csvString;
    
    public static void writeCSV(String fileToWrite, Object[][] tableToWrite)
    {
        errorMessage = "";        
        try 
        {
            FileOutputStream outputStream = new FileOutputStream(fileToWrite + ".csv");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
 
            for (int i = 0; i < tableToWrite.length; i++) 
            {
                csvString = tableToWrite[i][0].toString();
                for (int j = 1; j < tableToWrite[i].length; j++)
                {
                    csvString += "," + tableToWrite[i][j].toString();
                }
                bufferedWriter.write(csvString);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } 
        catch (IOException e) 
        {
            System.out.println("Error writing file: " + fileToWrite + ".csv");
            errorMessage = e.getMessage();
        }
    }
    
    public static String getErrorMessage()
    {
        return errorMessage;
    }
}
