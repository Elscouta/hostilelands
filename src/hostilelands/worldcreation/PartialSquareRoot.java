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
 *
 * @author Elscouta
 */
public class PartialSquareRoot
{
    final int size;
    Grid3x3<SummarySource> neighbors;
    PartialSquare mainContainer;
    
    public PartialSquareRoot(int size)
    {
        this.size = size;
        this.neighbors = new Grid3x3(null, () -> new OutOfBoundsSquare(size));
        this.mainContainer = new NullContainer(size, 0, 0);        
    }

    public <T extends PartialSquare> void addObject(PartialSquare.Factory<T> fact)
    {
        PartialSquare obj = fact.create(size, 0, 0, mainContainer, neighbors);
        mainContainer = new PartialSquareContainer(size, 0, 0, mainContainer, neighbors, obj);
    }
    
    public void generate()
    {
    }
    
    public PartialTileData getTileData(int x, int y)
    {
        return mainContainer.getTileData(x, y);
    }
    
    public void postProcess()
    {
        mainContainer.postProcess();
    }
    
    private class NullContainer extends AbstractPartialSquare
    {
        NullContainer(int size, int origX, int origY)
        {
            super(size, origX, origY, null, null);
        }
        
        @Override
        public Grid2x2<PartialSquare> generate() 
        {
            return createChilds((s, oX, oY, p, n) ->
                new NullContainer(s, oX, oY));
        }

        @Override
        public PartialTileData getTileData() 
        {
            return new PartialTileData();
        }

        @Override
        public PartialSquareSummary getSummary() 
        {
            return new PartialSquareSummary(size);
        }        
    }
}
