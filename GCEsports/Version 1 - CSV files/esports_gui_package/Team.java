// header comments
package esports_gui_package;

import java.util.ArrayList;


public class Team 
{
    //private variables
    private String teamName;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private final ArrayList<String> players;
    
    
    //constructor
    public Team(String teamName, String contactName, String contactPhone, String contactEmail)
    {
        this.teamName = teamName;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.players = new ArrayList();
        
    }
    
    //get methods
    public String getTeamName()
    {
        return teamName;
    }
    
    public String getContactName()
    {
        return contactName;
    }
    
    public String getContactPhone()
    {
        return contactPhone;
    }
    
    public String getContactEmail()
    {
        return contactEmail;
    }
    
    public ArrayList<String> getPlayers()
    {
        return players;
    }
    
    //set methods
    public void setTeamName (String teamName)
    {
        this.teamName = teamName;
    }
    
    public void setContactName (String contactName)
    {
        this.contactName = contactName;
    }
    
    public void setContactPhone (String contactPhone)
    {
        this.contactPhone = contactPhone;
    }
    
    public void setContactEmail (String contactEmail)
    {
        this.contactEmail = contactEmail;
    }
    
    public void setPlayer(String playerName)
    {
        this.players.add(playerName);
    }
    
    public void removePlayers()
    {
        this.players.clear();
    }
    
    public String toCSVString()
    {
        return teamName + "," + contactName + "," + contactPhone + "," + contactEmail;
    }
    
    
}



