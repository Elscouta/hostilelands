/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.events.GameEvent;

/**
 * Represents a simulable entity on the map, not controlled by the player. 
 * 
 * Typical example: some ennemies running around, a lair spawning troops.
 * 
 * Implementing classes need to define the following procedures:
 * - void simulateTime(int minutes) : simulates the entity for the given time.
 * - GameEvent onPartyEncounter(Party p) : returns the event that should happen
 * when the entity collides with the player.
 * - GameEvent onPartyLeave() : returns the event that should happen when 
 * the entity no longer collides with the player.
 *
 * Additionnally, the following procedures might need to overridden:
 * - void init(ShortTimeMgr mgr, World w, double x, double y)
 * - void suppress()
 * 
 * This object should be locked before performing any action on it.
 * 
 * @author Elscouta
 */
public abstract class MapEntity extends MapObject implements ShortTimeEntity
{   
    public abstract GameEvent onPartyEncounter(MainParty p);
    public abstract GameEvent onPartyLeave(MainParty p);

    private final String iconIdentifier;
    protected ShortTimeMgr shortTimeMgr;

    protected MapEntity(String iconIdentifier)
    {
        this.iconIdentifier = iconIdentifier;
        this.shortTimeMgr = null;
    }
    
    @Override
    public String getIconIdentifier()
    {
        return iconIdentifier;
    }
    
    /**
     * Initializes the entity and registers it to the shortTimeMgr.
     * Because this function synchronizes on the manager, it should only
     * be called when it is safe to do so.
     * @param shortTimeMgr the shorttime manager
     * @param w the world
     * @param x the X coordinate in the world
     * @param y the Y coordinate in the world
     */
    public void init(ShortTimeMgr shortTimeMgr, World w, double x, double y)
    {
        super.init(w, x, y);        
        
        this.shortTimeMgr = shortTimeMgr;
        shortTimeMgr.addEntity(this);
    }

    /**
     * Deletes the entity and unregisters it from the things it subscribed to.
     * Because this function synchronizes on the manager, it should only
     * be called when it is safe to do so.
     */
    @Override
    public void suppress()
    {
        shortTimeMgr.removeEntity(this);
        this.shortTimeMgr = null;
        
        super.suppress();
    }
}
