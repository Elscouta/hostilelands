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

/**
 *
 * @author Elscouta
 */
public class OutOfBoundsSquare implements IWorldSquare
{
    int size;
    
    public OutOfBoundsSquare(int size)
    {
        this.size = size;
    }

    @Override
    public void generate() 
    {
        throw new UnsupportedOperationException("Should not generate out of bounds squares."); 
    }
    
    @Override
    public void setNeighbors(Grid3x3<IWorldSquare> neighbors)
    {
        throw new UnsupportedOperationException("Neighbors of outofbounds square should not be set");
    }
    
    @Override
    public Grid2x2<IWorldSquare> getChilds()
    {
        if (size == 1)
            return null;
        
        return Grid2x2.generate(() -> new OutOfBoundsSquare(size / 2));
    }

    @Override
    public TileData getTile(int x, int y) 
    {
        throw new UnsupportedOperationException("Should not request out of bounds tiles."); 
    }
    
    @Override
    public void postProcess(int cx, int cy, WorldSquare ws, int x, int y)
    {
        throw new UnsupportedOperationException("Should not request out of bounds tiles.");         
    }

    @Override
    public TileDataSummary getSummary() 
    {
        TileDataSummary summary = new TileDataSummary(size);
        summary.addTerrainGuess(TerrainType.SEA, 1d, true);
        return summary;
    }

    @Override
    public void addObject(CreationObject obj) 
    {
        throw new UnsupportedOperationException("Should not add objects out of bounds."); 
    }
    
}
