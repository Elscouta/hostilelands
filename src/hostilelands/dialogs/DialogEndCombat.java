/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.dialogs;

import hostilelands.Combat;
import hostilelands.events.GameEventEndCombat;

/**
 *
 * @author Elscouta
 */
public class DialogEndCombat extends DialogDesc
{
    public DialogEndCombat(Combat.Summary summary)
    {
        super("Combat Summary", 
              summary.text, 
              new GameEventEndCombat(summary.after));
    }
}
