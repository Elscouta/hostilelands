/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.TerrainType;
import hostilelands.WorldSquare;
import hostilelands.tools.CardinalMap;
import hostilelands.tools.Grid2x2;
import hostilelands.tools.Grid3x3;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elscouta
 */
public class World1x1Square implements IWorldSquare
{
    List<CreationObject> objects;
    TileDataSummary summary;
    
    public World1x1Square()
    {
        objects = new ArrayList<>();
    }
    
    @Override
    public void addObject(CreationObject obj)
    {
        if (obj != null)
            objects.add(obj);
    }
    
    @Override
    public TileData getTile(int x, int y)
    {
        TileData data = new TileData();
        
        for (CreationObject obj : objects)
            data = obj.fillTileData(data);
        
        return data;
    }
    
    @Override
    public TileDataSummary getSummary()
    {
        if (summary != null)
            return summary;
        
        TileData data = getTile(0, 0);
        summary = new TileDataSummary(1);
        summary.addTerrainGuess(data.getTerrainType(), 1d, true);
        if (data.getLocation() != null)
            summary.addLocation(data.getLocation());
        
        return summary;
    }
    
    @Override
    public void postProcess(int cx, int cy, WorldSquare sq, int x, int y)
    {
        for (CreationObject obj : objects)
            obj.postProcess(sq, x, y);
    }
    
    @Override
    public void generate()
    {
        
    }
    
    @Override
    public void setNeighbors(Grid3x3<IWorldSquare> neighbors)
    {
    }
    
    @Override
    public Grid2x2<IWorldSquare> getChilds()
    {
        return null;
    }
}
