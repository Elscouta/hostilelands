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
public class PartialTileData
{
    private TerrainType.Transform terrain;
    private MapLocation location;
    
    public PartialTileData()
    {
        terrain = (t -> t);
        location = null;
    }
    
    public TerrainType getTerrainType()
    {
        return terrain.apply(TerrainType.SEA);
    }

    public void setTerrain(TerrainType other, boolean forceOverwrite)
    {
        if (forceOverwrite)
            terrain = (t -> other);
        else
            terrain = (t -> t != TerrainType.SEA ? other : t);
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
    
    public void overwrite(PartialTileData other)
    {
        final TerrainType.Transform myFunc = terrain;
        final TerrainType.Transform otherFunc = other.terrain;
        
        terrain = (t -> otherFunc.apply(myFunc.apply(t)));
    }

    public static PartialTileData combine(PartialTileData o1, PartialTileData o2)
            throws CombineFunc.CombineFailure
    {
        if (o1 == null)
            return o2;
        
        if (o2 == null)
            return o1;
        
        PartialTileData o = new PartialTileData();
        o.setTerrain( 
                CombineFunc.combineNullOrEqual(o1.getTerrainType(), o2.getTerrainType()),
                true
        );
        o.location = CombineFunc.combineNull(o1.location, o2.location);
        
        return o;
    }        
}
