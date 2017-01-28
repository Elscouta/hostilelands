/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.World;
import hostilelands.WorldSquare;
import hostilelands.tools.Grid2x2;
import hostilelands.tools.Grid3x3;
import java.util.ArrayList;
import java.util.List;
import hostilelands.tools.CombineFunc;

/**
 *
 * @author Elscouta
 */
public class CreationWorldSquare implements IWorldSquare
{
    final int size;
    
    List<CreationObject> objects;
    Grid2x2<IWorldSquare> childs;
    Grid3x3<IWorldSquare> neighbors;
    boolean generated;
    TileDataSummary summary;        
    
    public CreationWorldSquare(int size)
    {
        this.size = size;
        this.objects = new ArrayList<>();
        this.generated = false;
        this.summary = null;
               
        assert(size % 2 == 0 || size == 1);
        
        createQuarters();
    }
    
    @Override
    public void setNeighbors(Grid3x3<IWorldSquare> neighbors)
    {
        assert(this == neighbors.o22);
        
        this.neighbors = neighbors;
        if (size > 1)
        {
            neighbors.split3x3(s -> s.getChilds())
                     .iter(n -> n.o22.setNeighbors(n));
        }
    }
    
    @Override
    public void addObject(CreationObject object)
    {
        summary = null;
        
        if (object != null)
            objects.add(object);
    }
    
    private IWorldSquare createEmptyQuarter()
    {
        if (size > 1)
            return new CreationWorldSquare(size / 2);
        else
            return new World1x1Square();                
    }
        
    private void createQuarters()
    {
        childs = Grid2x2.generate(this::createEmptyQuarter);
    }
        
    @Override
    public void generate()
            throws World.GenerationFailure
    {
        if (generated)
            return;
        
        for (CreationObject obj : objects)
        {
            Grid2x2<CreationObject> g = 
                    obj.split(childs, neighbors.map(n -> n.getSummary()));
            childs.southeast.addObject(g.southeast);
            childs.northeast.addObject(g.northeast);
            childs.northwest.addObject(g.northwest);
            childs.southwest.addObject(g.southwest);
        }
        
        generated = true;
        
        childs.iter(x -> x.generate());
    }
    
    @Override
    public Grid2x2<IWorldSquare> getChilds()
    {
        return childs;
    }
    
    @Override
    public TileData getTile(int x, int y)
    {
        assert(generated);
        
        try {
            return childs.funcAtPosition(
                    (o, mx, my) -> o.getTile(mx, my), 
                    TileData::combine,
                    size, x, y, 0);
        } catch (CombineFunc.CombineFailure e) {
            assert(false);
            return null;
        }
    }
    
    @Override
    public TileDataSummary getSummary()
    {
        if (summary != null)
            return summary;
        
        summary = new TileDataSummary(size);
        
        for (CreationObject obj : objects)
            summary = obj.fillTileDataSummary(summary);
        
        return summary;
    }
    
    @Override
    public void postProcess(int cx, int cy, WorldSquare square, int x, int y)
    {
        try {
            childs.funcAtPosition(
                    (o, ox, oy) -> { o.postProcess(ox, oy, square, x, y); return true; },
                    (a, b) -> { return true; },
                    size, cx, cy, 0 
            );
        } catch (CombineFunc.CombineFailure e) {
            assert(false);
        }
    }
}
