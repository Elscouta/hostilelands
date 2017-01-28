/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.World;
import hostilelands.WorldSquare;
import hostilelands.tools.CardinalMap;
import hostilelands.tools.Grid2x2;
import hostilelands.tools.Grid3x3;

/**
 * Each object is implicitly associated to a world square and its behavior
 * can only be defined with regards to that world square.
 * 
 * @author Elscouta
 */
public interface CreationObject 
{
    /**
     * Generate a 2x2 grid containing the object "split" into four parts. 
     * The object itself can be reused, and some parts may be null
     *
     * @param childs The four childs squares that must contain the four
     * parts of the object.
     * @param neighbors Neighbors of the square that the current object
     * is associated to. Their size will be the same as the current square.
     * 
     * @return A 2x2 grid containing the object "split" into four parts.
     * 
     * @throws hostilelands.World.GenerationFailure Some externals
     * constraints prevented the object to be split, and the object
     * was considered too important to be discarded.
     */
    public Grid2x2<CreationObject> split(Grid2x2<IWorldSquare> childs,
                                         Grid3x3<TileDataSummary> neighbors)
            throws World.GenerationFailure;
    
    /**
     * Contributes the object properties a partially constructed tile.
     * Should only be used on "final" (aka: associated to 1x1 tiles) objects
     * 
     * @param data A partially constructed tile (consumed, can not be reused).
     * @return A partially constructed tile, with the object properties added
     */
    public TileData fillTileData(TileData data);
    
    /**
     * Contributes the object properties to a partially constructed square.
     * Can be used on any object.
     * 
     * @param data Information regarding the current square 
     * (consumed, can not be reused).
     * @return Information regarding the current square, with the 
     * object properties added
     */
    public TileDataSummary fillTileDataSummary(TileDataSummary data);
    
    /**
     * Performs additionnal operations (implementation-defined) after the tile
     * has been fully constructed inside WorldSquare.
     * 
     * @param sq The world square that the object has been assigned to.
     * @param x The x-coordinate (relative to the WorldSquare) of the tile
     * that has been created.
     * @param y The y-coordinate (relative to the WorldSquare) of the tile
     * that has been created.
     * 
     * Should only be called on final (associated to 1x1 tiles) objects.
     */
    public void postProcess(WorldSquare sq, int x, int y);
    
    /**
     * REMOVE ME
     * @return Whether this object is alive.
     */
    public boolean isAlive();
}
