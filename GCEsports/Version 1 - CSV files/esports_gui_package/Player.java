/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package esports_gui_package;

/**
 *
 * @author taipan
 */
public class Player 
{
    private String playerName;
    private String teamName;
    
    public Player(String playerName, String teamName)
    {
        this.playerName = playerName;
        this.teamName = teamName;
    }
    
    public String getPlayerName()
    {
        return playerName;
    }
    
    public String getTeamName()
    {
        return teamName;
    }
    
    public void setPlayerName(String playerName)
    {
        this.playerName = playerName;
    }
    
    public void setTeamName(String teamName)
    {
        this.teamName = teamName;
    }
    
    public String toCSVString()
    {
        return playerName + "," + teamName;
    }
}
