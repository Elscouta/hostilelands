/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.desc.BasicUnitDesc;
import hostilelands.desc.TroopDesc;
import hostilelands.events.GameEvent;
import hostilelands.events.GameEventDialog;
import hostilelands.events.GameEventCombat;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Elscouta
 */
public class EnemyTroop extends MapHazard implements Team
{
    List<Unit> units;
    
    public EnemyTroop(TroopDesc desc, int level)
    {
        super(desc.getIconIdentifier(), new hostilelands.strategies.RandomStrategy());

        List<BasicUnitDesc> unitDescs = desc.getUnitDescs(level);
        
        units = unitDescs.stream()
                         .map(d -> new BasicUnit(d))
                         .collect(Collectors.toList());
    }
    
    @Override
    public GameEvent onPartyEncounter(MainParty p)
    {
        return new GameEventCombat(p, this);
    }
    
    @Override
    public GameEvent onPartyLeave(MainParty p)
    {
        return new GameEventDialog("An event", "Your party leaves the monster.");
    }
    
    @Override
    public Iterable<Unit> getUnits()
    {
        return units;
    }
}
