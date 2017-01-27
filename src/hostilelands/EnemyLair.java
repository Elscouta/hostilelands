/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.desc.LairDesc;
import hostilelands.events.GameEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elscouta
 */
public class EnemyLair extends MapLocation
{
    private final LairDesc desc;
    private int level;
    private List<Spawn> spawns;
    
    public EnemyLair(LairDesc desc, int level)
    {
        super(desc.getIconIdentifier(), "lair");
        
        this.desc = desc;
        this.level = level;        
    }
    
    private void updateSpawns()
    {
        if (spawns != null)
            spawns.stream()
                  .forEach(s -> s.unbind(shortTimeMgr));
        
        spawns = desc.getSpawns(level);
        
        if (spawns != null)
            spawns.stream()
                  .forEach(s -> s.bind(shortTimeMgr, world, getX(), getY()));
    }
    
    @Override
    public void init(ShortTimeMgr stm, World w, double x, double y)
    {
        super.init(stm, w, x, y);
        
        updateSpawns();
    }
    
    /**
     * Does nothing, the spawning is managed by the spawn class itself.
     * @param time The number of minutes elapsed since last call.
     */
    @Override
    public void simulateTime(int time)
    {
        
    }
    
    /**
     * TO BE IMPLEMENTED
     */
    @Override
    public Map<String, GameEvent> getPossibleEncounters()
    {
        return new HashMap();
    }
}
