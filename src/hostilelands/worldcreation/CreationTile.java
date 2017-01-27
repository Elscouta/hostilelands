/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.MapLocation;
import hostilelands.Settings;
import hostilelands.World;
import hostilelands.tools.CardinalMap;
import hostilelands.tools.Grid2x2;
import hostilelands.tools.Grid3x3;

/**
 *
 * @author Elscouta
 */
public class CreationTile implements CreationObject
{
    CreationTileDesc desc;
    MapLocation obj;
    
    public CreationTile(CreationTileDesc desc)
    {
        this.desc = desc;
        this.obj = desc.getLocation();
    }
    
    @Override
    public boolean isAlive()
    {
        return true;
    }
    
    @Override
    public Grid2x2<CreationObject> split(Grid2x2<IWorldSquare> childs,
                                         Grid3x3<TileDataSummary> neighbors)
            throws World.GenerationFailure
    {        
        Grid2x2<TileDataSummary> childSummaries = childs.map(c -> c.getSummary());
                
        Grid2x2<Grid3x3<TileDataSummary>> hierSummaries = 
                childSummaries.propagateNeighbors(neighbors);

        Grid2x2<Double> childScores = 
                hierSummaries.map(n -> desc.computeScore(n.o22, n));
        
        if (childScores.reduce(0d, (x, y) -> x+y) < Settings.FLOAT_PRECISION)
        {
            if (desc.hasStrictCreation())
            {
                throw new World.GenerationFailure("Unable to position mandatory object.");
            }
            else 
            {
                System.err.printf("Unable to position object: %s!\n", desc.getIdentifier());
                return new Grid2x2<CreationObject>();
            }
        }
        
        Grid2x2<CreationObject> ret = 
                childs.mapRandom(s -> this, s -> null, childScores);

        return ret;
    }
    
    @Override
    public TileData fillTileData(TileData data)
    {
        data.addLocation(obj);
        return data;
    }
    
    @Override
    public TileDataSummary fillTileDataSummary(TileDataSummary summary)
    {
        summary.addLocation(obj);
        return summary;
    }
}
