/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.events;

import hostilelands.Game;
import hostilelands.dialogs.DialogDesc;

/**
 *
 * @author Elscouta
 */
public class GameEventDialog implements GameEvent
{
    public DialogDesc desc;
    
    public GameEventDialog(String title, String text)
    {
        this.desc = new DialogDesc(title, text, null);
    }
    
    @Override
    public void run(Game g)
    {
        g.openDialog(desc);
    }
    
    @Override
    public boolean isPausing()
    {
        return true;
    }
}
