/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.events.GameEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elscouta
 */
public abstract class MapLocation extends MapEntity
{
    private final List<String> traits;
    
    protected abstract Map<String, GameEvent> getPossibleEncounters();
    
    /**
     * Base constructor for subclasses.
     * @param iconIdentifier The identifier of the map sprite for the location.
     * @param trait A string assigning the location to a category.
     */
    protected MapLocation(String iconIdentifier, String trait)
    {
        super(iconIdentifier);
        
        this.traits = new ArrayList<>();
        this.traits.add(trait);
    }
    
    private class ListOptions implements GameEvent
    {
        @Override
        public void run(Game g)
        {
        }
        
        @Override
        public boolean isPausing()
        {
            return false;
        }
    }
    
    @Override
    public GameEvent onPartyEncounter(MainParty p)
    {
        return new ListOptions();
    }
    
    @Override
    public GameEvent onPartyLeave(MainParty p)
    {
        return null;
    }
    
    public boolean hasTrait(String trait)
    {
        return traits.contains(trait);
    }
}
