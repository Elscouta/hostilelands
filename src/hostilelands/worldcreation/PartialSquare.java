/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.World;
import hostilelands.tools.Grid2x2;
import hostilelands.tools.Grid3x3;

/**
 * Each object is implicitly associated to a world square and its behavior
 * can only be defined with regards to that world square.
 * 
 * @author Elscouta
 */
public interface PartialSquare extends SummarySource
{
    /**
     * Returns a 2x2 grid containing the square "split" into four parts, where
     * some parts may be null. Implementation of that class should cache this
     * generation to ensure that multiple calls to that method are efficient.
     *     * 
     * @return A 2x2 grid containing the square "split" into four parts.
     * 
     * @throws hostilelands.World.GenerationFailure Some externals
     * constraints prevented the object to be split, and the object
     * was considered too important to be discarded.
     */
    @Override
    public Grid2x2<PartialSquare> getChilds()
            throws World.GenerationFailure;
    
    /**
     * Contributes the object properties to a partially constructed tile. 
     * Calling this method will generate recursively all childs.
     * 
     * @param x The x coordinate of the tile to fetch.
     * @param y The y coordinate of the tile to fetch
     * 
     * @return A partially constructed tile, with some properties based on 
     * the partial square definition.
     */
    public PartialTileData getTileData(int x, int y)
            throws World.GenerationFailure;
    
    /**
     * Implementation can override this to performs additionnal operations 
     * after the tile has been fully constructed.
     */
    public void postProcess();

    
    /**
     * Returns the x-coordinate of the topleft corner of the square, with 
     * respect the full world being created.
     *
     * @return the x coordinate
     */
    public int getOrigX();

    /**
     * Returns the y-coordinate of the topleft corner of the square, with 
     * respect the full world being created.
     * 
     * @return the y coordinate
     */
    public int getOrigY();
    
    /**
     * Returns how many layers are present below that one
     * 
     * @return ^
     */
    public int getLayerCount();
    
    /**
     * Factory interface for object creation
     * 
     * @param <T> the type of object that the factory supports
     */
    public interface Factory<T>
    {
        public T create(int size, int origX, int origY, 
                        PartialSquare previousLayers, Grid3x3<SummarySource> neighbors);
    }
}
