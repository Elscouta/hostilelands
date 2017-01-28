/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.MapLocation;
import hostilelands.Settings;
import hostilelands.World;
import hostilelands.WorldSquare;
import hostilelands.tools.CardinalMap;
import hostilelands.tools.Grid2x2;
import hostilelands.tools.Grid3x3;

/**
 * Requests the creation of a location on a single tile.
 * @param <T> The exact type of the location being created.
 * @author Elscouta
 */
public class CreationTile<T extends MapLocation> implements CreationObject
{
    private final CreationTileDesc desc;
    private final T obj;
    private final Listener<T> creationListener;

    /**
     * An interface to be used to specify some behavior on object creation.
     * @param <T> the exact type of the object being created
     */
    public static interface Listener<T extends MapLocation>
    {
        /**
         * Called when the object is created and is about to get assigned to
         * a world square location.
         * @param obj The object that got created
         * @param x The x-coordinate of the object location (relative to the full world) 
         * @param y The y-coordinate of the object location (relative to the full world)
         */
        public void onCreation(T obj, int x, int y);
    }
    
    /**
     * Creates a new request for an object creation (that is limited to one tile)
     * @param desc The specification of the object to be created
     * @param creationListener An interface to be used when the object is created.
     */
    public CreationTile(CreationTileDesc<T> desc, Listener<T> creationListener)
    {
        this.desc = desc;
        this.obj = desc.getLocation();
        this.creationListener = creationListener;
    }
    
    /**
     * Creates a new request for an object creation, without specifying a 
     * specific behavior when the object is created.
     * @param desc The specification of the object to be created 
     */
    public CreationTile(CreationTileDesc<T> desc)
    {
        this.desc = desc;
        this.obj = desc.getLocation();
        this.creationListener = 
            (T l_obj, int l_x, int l_y) -> {};
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
    
    @Override
    public void postProcess(WorldSquare sq, int x, int y)
    {
        creationListener.onCreation(obj, sq.toWorldX(x), sq.toWorldY(y));
    }
}
