/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.MapLocation;
import hostilelands.Settings;
import hostilelands.World;
import hostilelands.tools.Grid2x2;
import hostilelands.tools.Grid3x3;

/**
 * Requests the creation of a location on a single tile.
 * @param <T> The exact type of the location being created.
 * @author Elscouta
 */
public class LocationPlacement<T extends MapLocation> extends AbstractPartialSquare
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
     * @param size
     * @param origX
     * @param origY
     * @param neighbors
     * @param desc The specification of the object to be created
     * @param creationListener An interface to be used when the object is created.
     */
    private LocationPlacement(int size, int origX, int origY, 
                              PartialSquare previousLayers,
                              Grid3x3<SummarySource> neighbors,
                              CreationTileDesc<T> desc, 
                              Listener<T> creationListener)
    {
        super(size, origX, origY, previousLayers, neighbors);
        
        this.desc = desc;
        this.obj = desc.getLocation();

        if (creationListener != null)
            this.creationListener = creationListener;
        else
            this.creationListener = (_obj, _x, _y) -> {};
    }
    
    public static <T extends MapLocation> Factory<LocationPlacement<T>> 
        getFactory(CreationTileDesc<T> desc, Listener<T> creationListener)
    {
        return (size, origX, origY, previousLayers, neighbors) ->
                new LocationPlacement(size, origX, origY, previousLayers, neighbors,
                                      desc, creationListener);
    }
     
    @Override
    public Grid2x2<PartialSquare> generate()
            throws World.GenerationFailure
    {        
        Grid2x2<PartialSquareSummary> childSummaries = previousLayers.getChilds().map(c -> c.getSummary());
        Grid3x3<PartialSquareSummary> neighborsSummaries = neighbors.map(c -> c.getSummary());
        
        Grid2x2<Grid3x3<PartialSquareSummary>> hierSummaries = 
                childSummaries.propagateNeighbors(neighborsSummaries);

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
                return new Grid2x2<>();
            }
        }
        
        Grid2x2<PartialSquare> candidates = createChilds((s, oX, oY, p, n) -> 
                new LocationPlacement(s, oX, oY, p, n, desc, creationListener));

        Grid2x2<PartialSquare> ret = 
                candidates.mapRandom(s -> s, s -> null, childScores);

        return ret;
    }
    
    @Override
    public PartialTileData getTileData()
    {
        PartialTileData data = new PartialTileData();
        data.addLocation(obj);
        return data;
    }
    
    @Override
    public PartialSquareSummary getSummary()
    {
        PartialSquareSummary summary = new PartialSquareSummary(size);
        summary.addLocation(obj);
        return summary;
    }
    
    @Override
    public void postProcess()
    {
        creationListener.onCreation(obj, origX, origY);
    }
}
