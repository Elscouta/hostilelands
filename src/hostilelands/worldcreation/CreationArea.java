/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.tools.Grid2x2;
import hostilelands.tools.Grid3x3;
import hostilelands.tools.Intersectable;
import hostilelands.tools.Pair;

/**
 *
 * @author Elscouta
 */
public abstract class CreationArea extends AbstractPartialSquare
{
    protected final DottedInterpreter interpreter;
    protected final DottedSquare edgeConstraints;
    
    protected CreationArea(int size, int origX, int origY, 
                           PartialSquare previousLayers,
                           Grid3x3<SummarySource> neighbors,
                           DottedInterpreter interpreter, 
                           DottedSquare edgeConstraints)
    {
        super(size, origX, origY, previousLayers, neighbors);
        this.interpreter = interpreter;
        this.edgeConstraints = edgeConstraints;
        
        assert(size == 0 || edgeConstraints != null);
        assert(interpreter != null);
    }
    
    /**
     * Child classes must implement this method to provide a method to create
     * quarter childs
     * @param size
     * @param origX
     * @param origY
     * @param previousLayers
     * @param neighbors
     * @param quarter The quarter being created (constants defined in Grid2x2)
     * @param childConstraints The edge constraints
     * @return The newly created child
     */
    protected abstract CreationArea createChild(int size, int origX, int origY,
                                                PartialSquare previousLayers,
                                                Grid3x3<SummarySource> neighbors,
                                                int quarter, DottedSquare childConstraints);
    
    private boolean isAlive()
    {
        return !edgeConstraints.isEmpty() || !interpreter.deleteOnEmptyEdges();
    }
    
    /**
     * A very fast procedure for very small squares
     * @return A 2x2 grid of objects.
     */
    public Grid2x2<PartialSquare> generate1x1()
    {
        Grid2x2<Boolean> values = new Grid2x2(
                edgeConstraints.getEdge(DottedSquare.SOUTH).getPixel(1),
                edgeConstraints.getEdge(DottedSquare.EAST).getPixel(1),
                edgeConstraints.getEdge(DottedSquare.NORTH).getPixel(1),
                edgeConstraints.getEdge(DottedSquare.WEST).getPixel(1)
        );
        
        return createChilds((s, oX, oY, p, n, q, v) -> v ?
                             createChild(s, oX, oY, p, n, q, null) : null,
                             values);
    }
    
    /**
     * A faster procedure for small squares.
     * @return A 2x2 grid of objects.
     */
    public Grid2x2<PartialSquare> generate2x2()
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
                       
        return createChilds((s, oX, oY, p, n, q, v) -> 
                             createChild(s, oX, oY, p, n, q, new DottedSquare(v)),
                             values.split2x2());
    }
    
    @Override
    public Grid2x2<PartialSquare> generate()
    {
        if (size == 1)
            return generate1x1();
        
        if (size == 2)
            return generate2x2();
        
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
        
        Grid2x2<DottedSquare> dottedSquares = new Grid2x2<>();
        dottedSquares.southeast = new DottedSquare(southPart2Edge, eastPart1Edge, eastCenterEdge.reverse(), southCenterEdge.reverse());
        dottedSquares.northeast = new DottedSquare(eastCenterEdge, eastPart2Edge, northPart1Edge, northCenterEdge.reverse());
        dottedSquares.northwest = new DottedSquare(westCenterEdge, northCenterEdge, northPart2Edge, westPart1Edge);
        dottedSquares.southwest = new DottedSquare(southPart1Edge, southCenterEdge, westCenterEdge.reverse(), westPart2Edge);        
        
        return createChilds((s, oX, oY, pr, n, q, square) -> 
                             createChild(s, oX, oY, pr, n, q, square),
                             dottedSquares);
    }
}
