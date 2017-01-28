/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.MapLocation;
import hostilelands.TerrainType;
import hostilelands.tools.CombineFunc;

/**
 *
 * @author Elscouta
 */
public class TileData
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
    
    public static TileData combine(TileData o1, TileData o2)
            throws CombineFunc.CombineFailure
    {
        if (o1 == null)
            return o2;
        
        if (o2 == null)
            return o1;
        
        TileData o = new TileData();
        o.terrain = CombineFunc.combineNullOrEqual(o1.terrain, o2.terrain);
        o.location = CombineFunc.combineNull(o1.location, o2.location);
        
        return o;
    }
}
