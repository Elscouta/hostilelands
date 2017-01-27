/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.events.GameEvent;
import hostilelands.models.ListModel;
import java.util.NoSuchElementException;

/**
 *
 * @author Elscouta
 */
public class HomeTown extends Town
                      implements ModifiableTeam
{
    ListModel<Unit> characters;
        
    public HomeTown()
    {
        characters = new ListModel();
    }
    
    @Override public boolean canAddUnit(Unit u)
    {
        return true;
    }
    
    @Override public boolean canRemoveUnit(Unit u)
    {
        if (!characters.contains(u))
            throw new NoSuchElementException();
        return true;
    }
    
    @Override public void addUnit(Unit u)
    {
        if (!canAddUnit(u))
            throw new FailedRequirementException();
        characters.add(u);
    }
    
    @Override public void removeUnit(Unit u)
    {
        if (!canRemoveUnit(u))
            throw new FailedRequirementException();
        characters.remove(u);
    }

    @Override public ListModel<Unit> getUnits()
    {
        return characters;
    }
    
    private class Enter implements GameEvent
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
    public GameEvent onEnter()
    {
        return new Enter();
    }
}
