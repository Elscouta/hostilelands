/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.MapLocation;
import hostilelands.TerrainType;
import hostilelands.tools.Combinable;

/**
 *
 * @author Elscouta
 */
public class TileData implements Combinable<TileData>
{
    private TerrainType terrain;
    private MapLocation location;
    
    public TileData()
    {
        terrain = TerrainType.SEA;
        location = null;
    }
    
    public TerrainType getTerrainType()
    {
        return terrain;
    }

    public void addTerrain(TerrainType other, boolean forceOverwrite)
    {
        if (forceOverwrite || other.canOverwrite(terrain))
            terrain = other;
    }
    
    public void addLocation(MapLocation loc)
    {
        assert (location == null);
        location = loc;
    }
    
    public MapLocation getLocation()
    {
        return location;
    }
    
    @Override
    public TileData combine(TileData o)
            throws Combinable.CombineFailure
    {
        if (o == null)
            return this;
        
        if (this.terrain != null && o.terrain != null && 
            this.terrain.equals(o.terrain) == false)
            throw new Combinable.CombineFailure();
        
        if (this.location != null && o.location != null)
            throw new Combinable.CombineFailure();
        
        if (this.terrain == null)
            this.terrain = o.terrain;
        
        if (this.location == null)
            this.location = o.location;
        
        return this;
    }
}
