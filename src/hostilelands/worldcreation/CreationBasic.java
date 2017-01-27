/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.TerrainType;
import hostilelands.tools.CardinalMap;
import hostilelands.tools.Grid2x2;
import hostilelands.tools.Grid3x3;

/**
 *
 * @author Elscouta
 */
public class CreationBasic implements CreationObject
{
    private TerrainType terrain;
    
    public CreationBasic(TerrainType terrain)
    {
        this.terrain = terrain;
    }
            
    @Override
    public Grid2x2<CreationObject> split(Grid2x2<IWorldSquare> childs, 
                                         Grid3x3<TileDataSummary> borders) 
    {
        throw new UnsupportedOperationException("Basic object are final and cannot be split."); 
    }

    @Override
    public TileData fillTileData(TileData data) 
    {
        data.addTerrain(terrain, true);
        return data;
    }

    @Override
    public TileDataSummary fillTileDataSummary(TileDataSummary data) 
    {
        data.addTerrainGuess(terrain, 1d, true);
        return data;
    }

    @Override
    public boolean isAlive() 
    {
        return true;
    }    
}
