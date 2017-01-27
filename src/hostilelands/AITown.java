/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.events.GameEvent;

/**
 *
 * @author Elscouta
 */
public class AITown extends Town
{
    public AITown()
    {
        super();
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
