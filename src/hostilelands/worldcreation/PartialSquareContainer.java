/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.World;
import hostilelands.tools.Grid2x2;
import hostilelands.tools.Grid3x3;
import hostilelands.tools.Pair;
import java.util.ArrayList;
import java.util.List;

/**
 * A container to aggregate all partial squares into a fully fledged 
 * square in construction.
 * 
 * @author Elscouta
 */
public class PartialSquareContainer extends AbstractPartialSquare
{
    private final PartialSquare object;
    
    public PartialSquareContainer(int size, int origX, int origY,
                                  PartialSquare previousLayers, 
                                  Grid3x3<SummarySource> neighbors,
                                  PartialSquare object)
    {
        super(size, origX, origY, previousLayers, neighbors);
        
        assert (object != null);
        this.object = object;
    }
       
    @Override
    public Grid2x2<PartialSquare> generate() 
            throws World.GenerationFailure 
    {
        Grid2x2<PartialSquare> ret = 
                createChilds((s, oX, oY, p, n, q, o) ->
                o != null ? new PartialSquareContainer(s, oX, oY, p, n, o) : p,
                object.getChilds());
                
        return ret;
    }

    @Override
    public PartialTileData getTileData() 
    {
        assert (size == 0);
        
        PartialTileData data = previousLayers.getTileData(0, 0);

        data.overwrite(object.getTileData(0, 0));
    
        return data;
    }

    @Override
    public PartialSquareSummary getSummary() 
    {
        PartialSquareSummary summary = previousLayers.getSummary();
        
        summary.overwrite(object.getSummary());
        
        return summary;
    }
}
