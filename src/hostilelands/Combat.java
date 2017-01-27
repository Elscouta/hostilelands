/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.desc.CombatType;
import hostilelands.events.GameEvent;
import hostilelands.desc.Dice;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import hostilelands.models.DiceModel;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Elscouta
 */
public class Combat 
{
    public static final int FRIEND_TEAM = 0;
    public static final int ENEMY_TEAM = 1;
    
    CombatType combatType;
    
    Team friendTeam;
    Team enemyTeam;
    
    List<DiceModel> friendDices;
    List<DiceModel> enemyDices;
    
    Map<Unit, List<DiceModel>> unitDices;
    
    public Combat(CombatType combatType, Team friend, Team enemy)
    {
        this.combatType = combatType;
        this.friendTeam = friend;
        this.enemyTeam = enemy;
        this.unitDices = new HashMap<>();
        
        friendDices = populateTeamDices(friendTeam);
        enemyDices = populateTeamDices(enemyTeam);
    }
    
    /**
     * Only call this once team dices have been populated. Internal use 
     * only.
     * 
     * Calling this with a team object distinct from friendTeam or
     * enemyTeam will crash.
     * 
     * @return The list of dices belonging to a team.
     */
    private List<DiceModel> getTeamDices(Team team)
    {
        if (team == friendTeam)      
            return friendDices;
        
        if (team == enemyTeam)
            return enemyDices;

        assert(false);
        return null; 
    }
    
    /**
     * Returns the opposing team. Internal use only.
     * 
     * Calling this with a team object distinct from friendTeam or
     * enemyTeam will crash.
     * 
     * @param team a team in the combat
     * @return the opposing team
     */
    private Team getOpposingTeam(Team team)
    {
        if (team == friendTeam)      
            return enemyTeam;
        
        if (team == enemyTeam)
            return friendTeam;

        assert(false);
        return null; 
    }
    
    /**
     * @return The friendly team
     */
    public Team getFriendTeam()
    {
        return friendTeam;
    }
    
    /**
     * @return The enemy team
     */
    public Team getEnemyTeam()
    {
        return enemyTeam;
    }
    
    /**
     * Allows to get the list of dices a unit is entitled to. Use this function
     * during combat, and don't use the methods from the Unit class.
     * 
     * The dices can be subscribed to for updates (when they are rolled).
     * 
     * @param u The unit 
     * @return An iterator on the list of dice models.
     */
    public Iterator<DiceModel> getUnitDices(Unit u)
    {
        return unitDices.get(u).iterator();
    }
    
    /**
     * Allows to create the models of dices a team is entitled to. This is called
     * during construction and should not be called again.
     * 
     * The dices can be subscribed to for updates (when they are rolled).
     * 
     * @param t The team 
     */
    private List<DiceModel> populateTeamDices(Team t)
    {
        List<DiceModel> retValue = new ArrayList<>();
        
        for (Unit u : t.getUnits())
        {
            List<DiceModel> unitDiceList = new ArrayList<>();
        
            for (Dice dice : u.getDices(null))
            {
                DiceModel diceModel = new DiceModel(dice);
                unitDiceList.add(diceModel);
                retValue.add(diceModel);
            }
                
            unitDices.put(u, unitDiceList);
        }
        
        return retValue;
    }
    
    /**
     * Rolls all dices. It is possible to roll some specific dices by using
     * the roll methods associated to these specific dices.
     */
    public void rollAllDices()
    {
        Iterator<DiceModel> i;
        
        i = friendDices.iterator();
        while (i.hasNext())
            i.next().roll();
        
        i = enemyDices.iterator();
        while (i.hasNext())
            i.next().roll();
    }
    
    /**
     * Subscribe the following listener to all the dices of
     * a specific team.
     * 
     * FIXME: Yeah, fix that shit.
     */
    public void addTeamChangeListener(Team team, ChangeListener l)
    {
        for (DiceModel dModel : getTeamDices(team))
            dModel.addChangeListener(l);            
    }
    
    /**
     * Returns the number of symbol that a given side has rolled.
     * 
     * @param team The team being considered
     * @return The number of occureces of each symbol
     */
    public Map<Dice.Symbol, Integer> countSymbols(Team team)
    {
        List<DiceModel> diceList = getTeamDices(team);
        Map<Dice.Symbol, Integer> retValue = new HashMap<>();
        
        for (Dice.Symbol s : combatType.getRelevantSymbols())
            retValue.put(s, 0);
        
        for (DiceModel d : diceList)
        {
            if (!d.rolled())
                continue;
            
            for (Dice.Symbol s : d.getFace().getSymbols())
                retValue.replace(s, retValue.get(s) + 1);
        }
        
        return retValue;
    }
    
    /**
     * Returns the number of symbol that a given side has
     * rolled, once rules have been taken into effect.
     * 
     * @param team The team being considerd
     * @return a mapping from symbols to their number of occurences
     */
    public Map<Dice.Symbol, Integer> countFinalSymbols(Team team)
    {
        Map<Dice.Symbol, Integer> ownSymbols = countSymbols(team);
        
        Team opposingTeam = getOpposingTeam(team);
        Map<Dice.Symbol, Integer> opposingSymbols = countSymbols(opposingTeam);
        
        return combatType.getLiveSymbols(ownSymbols, opposingSymbols);
    }
    
    /**
     * Resolves the fight. 
     * 
     * @return The game event that continues the fight.
     */
    public Summary resolveFight()
    {
        Summary summary = new Summary();
        summary.text = "No one got hurt! Everyone is fine!";
        summary.after = null;
        
        return summary;
    }
    
    /**
     * A summary of the fight resolution, to be displayed 
     */
    public class Summary
    {
        public String text;
        public GameEvent after;
    }
}
