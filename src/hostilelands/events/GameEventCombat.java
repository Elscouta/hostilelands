/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.events;

import hostilelands.Combat;
import hostilelands.Game;
import hostilelands.Team;
import hostilelands.desc.CombatType;
import hostilelands.desc.TroopDesc;

/**
 *
 * @author Elscouta
 */
public class GameEventCombat implements GameEvent
{
    Team friendTeam;
    Team enemyTeam;
    
    public GameEventCombat(Team friendTeam, Team enemyTeam)
    {
        this.friendTeam = friendTeam;
        this.enemyTeam = enemyTeam;
    }
    
    @Override
    public void run(Game g)
    {
        Combat c = new Combat(new CombatType(g.getDataStore()), friendTeam, enemyTeam);
        g.startCombat(c);
    }
    
    @Override
    public boolean isPausing()
    {
        return true;
    }
    
}
