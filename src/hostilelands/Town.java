/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.events.GameEvent;
import java.util.*;

/**
 *
 * @author Elscouta
 */
public abstract class Town extends MapLocation
{
    protected abstract GameEvent onEnter();
    
    protected Town()
    {
        super("town", "town");
    }
    
    @Override
    protected Map<String, GameEvent> getPossibleEncounters()
    {
        Map<String, GameEvent> actions = new HashMap<>();
        actions.put("enter", onEnter());
        return actions;
    }
    
    @Override
    public void simulateTime(int minutes)
    {        
    }
}
