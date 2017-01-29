/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.TerrainType;
import hostilelands.tools.Grid2x2;

/**
 * Represents a square that is out of bounds. Size doesn't matter, it just
 * returns "full of sea" summaries.
 * 
 * @author Elscouta
 */
public class OutOfBoundsSquare implements SummarySource
{
    private final int size;
    
    public OutOfBoundsSquare(int size)
    {
        this.size = size;
    }
    
    @Override
    public Grid2x2<SummarySource> getChilds()
    {
        return Grid2x2.generate(() -> (new OutOfBoundsSquare(size / 2)));
    }
    
    @Override
    public int getSize()
    {
        return size;
    }

    @Override
    public PartialSquareSummary getSummary() 
    {
        PartialSquareSummary summary = new PartialSquareSummary(size);
        summary.addTerrainGuess(TerrainType.SEA, 1d, true);
        return summary;
    }
    
    @Override
    public SummarySource getPreviousLayers()
    {
        return this;
    }
}
