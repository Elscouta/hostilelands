/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.events;

import hostilelands.Game;

/**
 *
 * @author Elscouta
 */
public interface GameEvent 
{
    public void run(Game g);
    public boolean isPausing();
}
