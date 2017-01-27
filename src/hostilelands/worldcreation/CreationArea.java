/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.tools.CardinalMap;
import hostilelands.tools.Grid2x2;
import hostilelands.tools.Grid3x3;
import hostilelands.tools.Intersectable;
import hostilelands.tools.Pair;

/**
 *
 * @author Elscouta
 */
public abstract class CreationArea implements CreationObject
{
    protected final int size;
    protected final DottedInterpreter interpreter;
    protected final DottedSquare edgeConstraints;
    
    protected CreationArea(int size, DottedInterpreter interpreter, DottedSquare edgeConstraints)
    {
        this.size = size;
        this.interpreter = interpreter;
        this.edgeConstraints = edgeConstraints;
    }
    
    /**
     * Child classes must implement this method to provide a method to create
     * quarter childs
     * @param quarter The quarter being created (constants defined in Grid2x2)
     * @param childConstraints The edge constraints
     * @return The newly created child
     */
    protected abstract CreationArea createChild(int quarter, DottedSquare childConstraints);
    
    @Override
    public boolean isAlive()
    {
        return !edgeConstraints.isEmpty() || !interpreter.deleteOnEmptyEdges();
    }
    
    /**
     * A very fast procedure for very small squares
     * @return A 2x2 grid of objects.
     */
    public Grid2x2<CreationObject> split1x1()
    {
        Grid2x2<Boolean> g = new Grid2x2(
                edgeConstraints.getEdge(DottedSquare.SOUTH).getPixel(1),
                edgeConstraints.getEdge(DottedSquare.EAST).getPixel(1),
                edgeConstraints.getEdge(DottedSquare.NORTH).getPixel(1),
                edgeConstraints.getEdge(DottedSquare.WEST).getPixel(1)
        );
        
        return g.mapMarked((q, b) -> b ? createChild(q, null) : null);
    }
    
    /**
     * A faster procedure for small squares.
     * @return A 2x2 grid of objects.
     */
    public Grid2x2<CreationObject> split2x2()
    {
        assert (size == 2);
        
        DottedLine northEdge = edgeConstraints.getEdge(DottedSquare.NORTH);
        DottedLine westEdge = edgeConstraints.getEdge(DottedSquare.WEST);
        DottedLine southEdge = edgeConstraints.getEdge(DottedSquare.SOUTH);
        DottedLine eastEdge = edgeConstraints.getEdge(DottedSquare.EAST);
                
        double r = Math.random();
        boolean center;
        
        if (r < 0.25f) 
            center = southEdge.getPixel(1);
        else if (r < 0.50f) 
            center = eastEdge.getPixel(1);
        else if (r < 0.75f) 
            center = northEdge.getPixel(1);
        else
            center = westEdge.getPixel(1);

        Grid3x3<Boolean> values = new Grid3x3(
            northEdge.getPixel(2), northEdge.getPixel(1), northEdge.getPixel(0),
            westEdge.getPixel(1), center, eastEdge.getPixel(1),
            southEdge.getPixel(0), southEdge.getPixel(1), southEdge.getPixel(2)
        );
                       
        return values.split2x2().mapMarked((q, g) -> createChild(q, new DottedSquare(g)));
    }
    
    @Override
    public Grid2x2<CreationObject> split(Grid2x2<IWorldSquare> childs,
                                         Grid3x3<TileDataSummary> neighbors)
    {
        if (size == 1)
            return split1x1();
        
        if (size == 2)
            return split2x2();
        
        assert(size % 2 == 0);
        int halfsize = size / 2;
        
        Intersectable area = AbstractDottedInterpreter.getDeformedPolygon(interpreter, edgeConstraints);
        
        /**
         * There is 12 different edges there:
         *  --- ---         northPart1Edge  northPart2Edge
         * |   |   |  westPart1Edge northCenterEdge eastPart2Edge
         *  --- ---         westCenterEdge  eastCenterEdge
         * |   |   |  westPart2Edge southCenterEdge eastPart1Edge
         *  --- ---         southPart1Edge  southPart2Edge
         */
        DottedLine northEdge = edgeConstraints.getEdge(DottedSquare.NORTH);
        DottedLine westEdge = edgeConstraints.getEdge(DottedSquare.WEST);
        DottedLine southEdge = edgeConstraints.getEdge(DottedSquare.SOUTH);
        DottedLine eastEdge = edgeConstraints.getEdge(DottedSquare.EAST);

        DottedLine eastCenterEdge = area.getHorizontalIntersections(halfsize, halfsize, size);
        eastCenterEdge.setPixel(halfsize, eastEdge.getPixel(halfsize));
        DottedLine westCenterEdge = area.getHorizontalIntersections(halfsize, 0, halfsize);
        westCenterEdge.setPixel(0, westEdge.getPixel(halfsize));
        DottedLine northCenterEdge = area.getVerticalIntersections(halfsize, halfsize, size);
        northCenterEdge.setPixel(halfsize, northEdge.getPixel(halfsize));
        DottedLine southCenterEdge = area.getVerticalIntersections(halfsize, 0, halfsize);
        southCenterEdge.setPixel(0, southEdge.getPixel(halfsize)); 
        
        boolean middlePixel = eastCenterEdge.getPixel(0);
        westCenterEdge.setPixel(halfsize, middlePixel);
        northCenterEdge.setPixel(0, middlePixel);
        southCenterEdge.setPixel(halfsize, middlePixel);
        assert(southCenterEdge.getPixel(halfsize) == middlePixel);
        assert(northCenterEdge.getPixel(0) == middlePixel);
        assert(westCenterEdge.getPixel(halfsize) == middlePixel);
        
        Pair<DottedLine, DottedLine> p;
        p = northEdge.split();
        DottedLine northPart1Edge = p.first;
        DottedLine northPart2Edge = p.second;
              
        p = westEdge.split();
        DottedLine westPart1Edge = p.first;
        DottedLine westPart2Edge = p.second;

        p = southEdge.split();
        DottedLine southPart1Edge = p.first;
        DottedLine southPart2Edge = p.second;

        p = eastEdge.split();
        DottedLine eastPart1Edge = p.first;
        DottedLine eastPart2Edge = p.second;
        
        Grid2x2<CreationObject> ret = new Grid2x2<>();
        DottedSquare southeastQuarter = new DottedSquare(southPart2Edge, eastPart1Edge, eastCenterEdge.reverse(), southCenterEdge.reverse());
        ret.southeast = createChild(Grid2x2.SOUTHEAST, southeastQuarter);
        
        DottedSquare northeastQuarter = new DottedSquare(eastCenterEdge, eastPart2Edge, northPart1Edge, northCenterEdge.reverse());
        ret.northeast = createChild(Grid2x2.NORTHEAST, northeastQuarter);
        
        DottedSquare northwestQuarter = new DottedSquare(westCenterEdge, northCenterEdge, northPart2Edge, westPart1Edge);
        ret.northwest = createChild(Grid2x2.NORTHWEST, northwestQuarter);
        
        DottedSquare southwestQuarter = new DottedSquare(southPart1Edge, southCenterEdge, westCenterEdge.reverse(), westPart2Edge);        
        ret.southwest = createChild(Grid2x2.SOUTHWEST, southwestQuarter);
        
        ret = ret.map(s -> s.isAlive() ? s : null);
        
        return ret;
    }

}
