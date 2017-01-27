/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.desc.TroopDesc;
import hostilelands.models.PositionModel;
import java.util.function.Consumer;

/**
 * This class synchronizes itself on the shorttimemgr itself.
 * @author Elscouta
 */
public class Spawn implements ShortTimeEntity
{
    private final TroopDesc troopType;
    private final int troopLevel;
    private final int averageTime;
    
    private World world;
    private double x;
    private double y;
    
    public Spawn(TroopDesc troopType, 
                 int troopLevel, 
                 int averageTime)
    {
        this.troopType = troopType;
        this.troopLevel = troopLevel;
        this.averageTime = averageTime;
    }
    
    public void bind(ShortTimeMgr mgr, World world, double x, double y)
    {
        assert(world != null);
        
        this.world = world;
        this.x = x;
        this.y = y;

        mgr.addEntity(this);
    }
    
    public void unbind(ShortTimeMgr mgr)
    {
        mgr.removeEntity(this);

        this.world = null;
    }
    
    
    @Override
    public void simulateTime(int minutes)
    {
        assert(world != null);
        
        double odds = (double) minutes / (double) averageTime;
        
        if (Math.random() < odds)
        {
            EnemyTroop obj = new EnemyTroop(troopType, troopLevel);
            try {
                world.addEntity(obj, x, y);
            } catch (World.OutOfWorldBoundsError e) {
                assert(false);
            }
        }
    }
}
