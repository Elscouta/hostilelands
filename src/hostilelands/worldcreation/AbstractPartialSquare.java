/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.World;
import hostilelands.tools.CombineFunc;
import hostilelands.tools.Grid2x2;
import hostilelands.tools.Grid3x3;

/**
 * Partial implementation of PartialSquare to share common features.
 *
 * @author Elscouta
 */
public abstract class AbstractPartialSquare implements PartialSquare
{
    final int size;
    final int origX;
    final int origY;
    final PartialSquare previousLayers;
    final Grid3x3<SummarySource> neighbors;
    
    boolean generated;
    Grid2x2<PartialSquare> childs;
    
    public AbstractPartialSquare(int size, int origX, int origY,
                                 PartialSquare previousLayers,
                                 Grid3x3<SummarySource> neighbors)
    {
        this.size = size;
        this.origX = origX;
        this.origY = origY;
        
        this.previousLayers = previousLayers;
        
        this.neighbors = neighbors != null ? new Grid3x3<>(neighbors) : null;
        if (neighbors != null)
        {
            assert (previousLayers != null);
            this.neighbors.o22 = previousLayers;
        }
        
        this.generated = false;
        this.childs = null;
               
        assert(size % 2 == 0 || size == 1);
        assert (previousLayers == null || 
                previousLayers.getLayerCount() < getLayerCount());
    }

    /**
     * Child classes must implement how the child partial squares are 
     * generated.
     * 
     * @return A grid representing the four child subsquares.
     */
    public abstract Grid2x2<PartialSquare> generate()
            throws World.GenerationFailure;
    
    /**
     * Functionnal interface for child factories
     * @param <F> An additionnal optional parameter
     * @param <C> The exact type of the child
     */
    @FunctionalInterface
    protected interface ChildFactoryDetailed<F, C>
    {
        C create(int size, int origX, int origY, 
                 PartialSquare previousLayers,
                 Grid3x3<SummarySource> neighbors, 
                 int quarter, F pData);
    }
    
    
    /**
     * Can be used by child classes to instantiate easily the Grid.
     * 
     * @param <F> The type of extra data that can be passed to the factories.
     * @param <C> The child type.
     * @param factory A method to construct child instances.
     * @param pData Additionnal data to be passed to the factories.
     * 
     * @return A grid with newly created partial squares .
     */
    protected <F, C extends PartialSquare> 
        Grid2x2<C> createChilds(ChildFactoryDetailed<F, C> factory,
                                Grid2x2<F> pData)
    {
        assert (size % 2 == 0 || size == 1);
        assert (size != 0);
        
        int offset;
        int newSize;
        if (size > 1)
        {
            offset = size / 2;
            newSize = size / 2;
        }
        else 
        {
            offset = 1;
            newSize = 0;
        }
        
        assert (neighbors == null || size == neighbors.o11.getSize());
        
        Grid2x2< Grid3x3<SummarySource> > splitNeighbors = neighbors != null ?
                neighbors.split3x3(x -> x.getChilds()) :
                new Grid2x2<>();
        Grid2x2< PartialSquare > splitPreviousLayers = previousLayers != null ?
                previousLayers.getChilds() :
                new Grid2x2<>();
        
        Grid2x2<C> ret = new Grid2x2();
        ret.northwest = factory.create(newSize, origX, origY, 
                splitPreviousLayers.northwest, splitNeighbors.northwest, 
                Grid2x2.NORTHWEST, pData.northwest);
        ret.northeast = factory.create(newSize, origX+offset, origY, 
                splitPreviousLayers.northeast, splitNeighbors.northeast, 
                Grid2x2.NORTHEAST, pData.northeast);
        ret.southwest = factory.create(newSize, origX, origY+offset,
                splitPreviousLayers.southwest, splitNeighbors.southwest, 
                Grid2x2.SOUTHWEST, pData.southwest);
        ret.southeast = factory.create(newSize, origX+offset, origY+offset,
                splitPreviousLayers.southeast, splitNeighbors.southeast, 
                Grid2x2.SOUTHEAST, pData.southeast);
        
        return ret;
    }
        
    protected <C extends PartialSquare> 
        Grid2x2<C> createChilds(Factory<C> factory)
    {
        return createChilds(
                (_size, _origX, _origY, _previousLayers, _neighbors, _quarter, _pData) ->
                factory.create(_size, _origX, _origY, _previousLayers, _neighbors),
                new Grid2x2<>());
    }
    
    @Override
    public Grid2x2<PartialSquare> getChilds()
    {
        if (generated)
            return childs;
        
        childs = generate();
        generated = true;
        
        return childs;
    }
    
    public abstract PartialTileData getTileData();
    
    @Override
    public PartialTileData getTileData(int x, int y)
    {
        if (size == 0)
            return getTileData();
                
        try {
            return getChilds().funcAtPosition((c, lx, ly) -> c.getTileData(lx, ly), 
                    PartialTileData::combine, size, x, y, 0);
        } catch (CombineFunc.CombineFailure e) {
            throw new World.GenerationFailure("Unable to merge data.");
        }
    }
    
    @Override
    public void postProcess()
    {
    }
    
    @Override
    public int getSize()
    {
        return size;
    }
    
    @Override
    public int getOrigX()
    {
        return origX;
    }
    
    @Override
    public int getOrigY()
    {
        return origY;
    }
    
    @Override
    public SummarySource getPreviousLayers()
    {
        return previousLayers;
    }
    
    @Override
    public final int getLayerCount()
    {
        if (previousLayers == null)
        {
            assert (neighbors == null);
            return 0;
        }
        else
        {
            assert (neighbors != null);
            assert (previousLayers == neighbors.o22);
            return 1 + previousLayers.getLayerCount();
        }
    }
}
