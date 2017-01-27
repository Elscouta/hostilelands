/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.events;

import hostilelands.Game;

/**
 * This event is called to end the combat. It is simply used to wrap a call
 * to endCombat before calling the followup event.
 * 
 * @author Elscouta
 */
public class GameEventEndCombat implements GameEvent
{
    GameEvent after;
    
    /**
     * Creates a new end combat event, with a given followup.
     * 
     * @param after the event that should be processed after the combat,
     * or null to resume the game flow.
     */
    public GameEventEndCombat(GameEvent after)
    {
        this.after = after;
    }
    
    /**
     * Simply ends the combat with the given followup
     * 
     * @param g The main game object.
     */
    @Override
    public void run(Game g)
    {
        g.endCombat(after);
    }
    
    /**
     * This event should always been called while the game is paused.
     * 
     * @return true 
     */
    @Override
    public boolean isPausing()
    {
        return true;
    }
}
