/*
Competition class
 */
package esports_gui_package;

public class Competition 
{
    // Private data fields - instance variables
    private String gameName;
    private String date;
    private String location;
    private String teamName;
    private int points;
    
    // public methods
    //constructor method
    public Competition(String gameName, String date, String location, String teamName, int points)
    {
        this.gameName = gameName;
        this.date = date;
        this.location = location;
        this.teamName = teamName;
        this.points = points;
    }
    //set methods (mutator methods)
    public void setGameName(String gameName)
    {
        this.gameName = gameName;
    }
    
    public void setDate(String date)
    {
        this.date = date;
    }
    
    public void setLocation(String location)
    {
        this.location = location;
    }
    
    public void setTeamName(String teamName)
    {
        this.teamName = teamName;
    }
    
    public void setPoints(int points)
    {
        this.points = points;
    }
    
    //get methods (retriever methods)
    public String getGameName()
    {
        return gameName;
    }
    
    public String getDate()
    {
        return date;
    }
    
    public String getLocation()
    {
        return location;
    }
    
    public String getTeamName()
    {
        return teamName;
    }
    
    public int getPoints()
    {
        return points;
    }
    
    public String toCSVString()
    {
        return gameName + "," + date + "," + location + "," + teamName + "," + points;
    }
}
