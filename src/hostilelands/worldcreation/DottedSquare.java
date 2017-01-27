/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.tools.Grid2x2;
import hostilelands.tools.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author Elscouta
 */
public class DottedSquare 
{
    public static final int SOUTH = 0;
    public static final int EAST = 1;
    public static final int NORTH = 2;
    public static final int WEST = 3;
    public static final int INTERNAL = 4;
    
    private final DottedLine southEdge;
    private final DottedLine eastEdge;
    private final DottedLine northEdge;
    private final DottedLine westEdge;
    private final int size;
    
    /**
     * Iterates over an edge and returns the point2D representing the toggles
     * over that edge.
     */
    private class EdgePointIterator implements Iterator<Point2D>
    {
        private final int myIdentifier;
        private final DottedLine edge;
        private final Iterator<Integer> edgeIterator;
    
        public EdgePointIterator(int myIdentifier, DottedLine edge)
        {
            this.myIdentifier = myIdentifier;
            this.edge = edge;
            this.edgeIterator = edge.getToggleIterator();
        }
        
        @Override
        public boolean hasNext()
        {
            return edgeIterator.hasNext();
        }
        
        @Override
        public Point2D next()
        {
            return next(0);
        }
        
        public Point2D next(int delta)
        {
            int pixel = edgeIterator.next() + delta;
            
            switch (myIdentifier)
            {
                case SOUTH: return new Point2D.Double(pixel, 0);
                case NORTH: return new Point2D.Double(size - pixel, size);
                case EAST:  return new Point2D.Double(size, pixel);
                case WEST:  return new Point2D.Double(0, size - pixel);
                default:    assert(false); return null;
            }            
        }
    }

    /**
     * A simple iterator over the four possible edges, starting at south.
     */
    private class EdgeIterator implements Iterator<DottedLine>
    {
        private int i;
        
        public EdgeIterator() { i = 0; }
        
        @Override 
        public boolean hasNext() { return i <= 3; }
        
        @Override 
        public DottedLine next() 
        {
            i++;
            switch (i)
            {
                case SOUTH+1: return southEdge;
                case EAST+1: return eastEdge;
                case NORTH+1: return northEdge;
                case WEST+1: return westEdge;
                default: assert(false); return null;
            }
        }
        
        public int getNextIdentifier() { return i; }
    }
    
    
    /**
     * A point iterator. Iterates over all the points in the square, returning
     * Point2D's instances.
     */
    protected class PointIterator implements Iterator<Point2D>
    {
        private final EdgeIterator edgeIterator;
        private Iterator<Point2D> pointIterator;
        private final Function<Integer, Iterator<Point2D>> missingEdgeProvider;
        private int currentEdge;
        private int edgeCount;
        
        public PointIterator(Function<Integer, Iterator<Point2D>> onMissingEdge)
        {
            edgeIterator = new EdgeIterator();
            missingEdgeProvider = onMissingEdge;
            pointIterator = getNextPointIterator();
        }
        
        private Iterator<Point2D> getNextPointIterator()
        {
            int identifier = edgeIterator.getNextIdentifier();
            
            currentEdge = identifier;
            DottedLine edge = edgeIterator.next();
            edgeCount = 0;
            
            Iterator<Point2D> it = new EdgePointIterator(currentEdge, edge);
            if (!it.hasNext())
            {
                currentEdge = INTERNAL;
                return missingEdgeProvider.apply(identifier);
            }
            else
                return it;
        }
        
        private void advance()
        {
            while (edgeIterator.hasNext() && !pointIterator.hasNext())
                pointIterator = getNextPointIterator();
        }
        
        @Override
        public boolean hasNext()
        {
            advance();
            return pointIterator.hasNext();
        }
        
        @Override
        public Point2D next()
        {
            advance();
            
            edgeCount++;
            return pointIterator.next();
        }
        
        /**
         * Returns the current edge being iterated on.
         * @return SOUTH/WEST/NORTH/EAST if on an edge, INTERNAL otherwise. 
         */
        protected int getNextPointType()
        {
            advance();
            
            return currentEdge;
        }
        
        /**
         * Returns whether the next edge represents a "hole" in the square
         */
        protected boolean isNextAHole()
        {
            return currentEdge != INTERNAL &&
                   pointIterator.hasNext() == true &&
                   edgeCount != 0 && 
                   edgeCount % 2 == 0;
        }
    }
    
    /**
     * A Hole iterator. Iterates over all the holes that are internal
     * to an edge.
     */
    protected class HoleIterator implements Iterator<Line2D>
    {
        private final EdgeIterator edgeIterator;
        private EdgePointIterator pointIterator;
        private Point2D lastPoint;

        protected HoleIterator()
        {
            edgeIterator = new EdgeIterator();
            pointIterator = getNextIterator();
        }
        
        private EdgePointIterator getNextIterator()
        {
            int edgeCurrentIdentifier = edgeIterator.getNextIdentifier();
            EdgePointIterator it = new EdgePointIterator(edgeCurrentIdentifier, 
                                                         edgeIterator.next());

            if (it.hasNext())
            {
                it.next();
                lastPoint = it.next(1);
            }
            
            return it;
        }
        
        private void advance()
        {
            while (edgeIterator.hasNext() && !pointIterator.hasNext())
                pointIterator = getNextIterator();
        }
        
        @Override
        public boolean hasNext()
        {
            advance();
            
            return pointIterator.hasNext();
        }
        
        @Override
        public Line2D next()
        {
            assert(hasNext());

            advance();
            
            Point2D p1 = lastPoint;
            Point2D p2 = pointIterator.next(-1);
            lastPoint = pointIterator.next(1);
            
            return new Line2D(p1, p2);
        }
    }
    
    /**
     * Constructs an empty square
     * @param size the size of the square.
     */
    public DottedSquare(int size)
    {
        this.size = size;
        this.southEdge = new DottedLine(size);
        this.eastEdge = new DottedLine(size);
        this.northEdge = new DottedLine(size);
        this.westEdge = new DottedLine(size);
    }
    
    /**
     * Constructs a 2x2 square based on a grid
     * @param grid a boolean grid 
     */
    public DottedSquare(Grid2x2<Boolean> grid)
    {
        size = 2;
        southEdge = new DottedLine(2);
        southEdge.setPixel(0, grid.southwest);
        southEdge.setPixel(1, grid.southeast);
        
        eastEdge = new DottedLine(2);
        eastEdge.setPixel(0, grid.southeast);
        eastEdge.setPixel(1, grid.northeast);
        
        northEdge = new DottedLine(2);
        northEdge.setPixel(0, grid.northeast);
        northEdge.setPixel(1, grid.northwest);

        westEdge = new DottedLine(2);
        westEdge.setPixel(0, grid.northwest);
        westEdge.setPixel(1, grid.southwest);
    }
    
    /**
     * Constructs a square from the four edges
     * @param south The south edge
     * @param east The east edge
     * @param north The north edge
     * @param west The west edge
     */
    public DottedSquare(DottedLine south, DottedLine east, DottedLine north, DottedLine west)
    {
        assert(south.getLength() == east.getLength());
        assert(east.getLength() == north.getLength());
        assert(north.getLength() == west.getLength());
        
        this.size = south.getLength();
        this.southEdge = south;
        this.eastEdge = east;
        this.northEdge = north;
        this.westEdge = west;

        assert(south.getPixel(size) == east.getPixel(0));
        assert(east.getPixel(size) == north.getPixel(0));
        assert(north.getPixel(size) == west.getPixel(0));
        assert(west.getPixel(size) == south.getPixel(0));

    }
    
    /**
     * Returns one of the square edges.
     * @param edge the identifier of an edge (SOUTH, EAST, NORTH or WEST)
     * @return the requested edge
     */
    public DottedLine getEdge(int edge)
    {
        switch (edge)
        {
            case SOUTH: return southEdge;
            case EAST: return eastEdge;
            case NORTH: return northEdge;
            case WEST: return westEdge;
            default: assert(false); return null;
        }
    }
    
    /**
     * Returns the number of edges that are empty.
     */
    public int countEmptyEdges()
    {
        int c = 0;
        if (southEdge.isEmpty()) c++;
        if (eastEdge.isEmpty()) c++;
        if (northEdge.isEmpty()) c++;
        if (westEdge.isEmpty()) c++;
        
        return c;
    }
    
    /**
     * Returns whether the square is empty.
     */
    public boolean isEmpty()
    {
        return countEmptyEdges() == 4;
    }
    
    /**
     * Returns the size of the square
     * @return The size of the square
     */
    public int getSize()
    {
        return size;
    }
    
    /**
     * Returns the value of the bottom left corner
     * @return true if the bottom left corner is positive, false otherwise
     */
    public boolean getBottomLeftCorner()
    {
        assert (southEdge.getPixel(0) == westEdge.getPixel(size));

        return southEdge.getPixel(0);
    }
    
    /**
     * Returns an iterator point.
     * 
     * @param onMissingEdge A way to generate point on edges that are empty.
     * @return A point iterator
     */
    protected PointIterator getPointIterator(Function<Integer, Iterator<Point2D>> onMissingEdge)
    {
        return new PointIterator(onMissingEdge);
    }
    
    /**
     * Returns an iterator over the holes.
     * @return an iterator over the holes.
     */
    protected Iterator<Line2D> getHoleIterator()
    {
        return new HoleIterator();
    }
    
    /**
     * Returns the ratio of positive pixels (over the total)
     * @return the ratio of positive pixels
     */
    public double getRatio()
    {
        return (southEdge.getRatio() + eastEdge.getRatio() + northEdge.getRatio() + westEdge.getRatio()) / 4d; 
    }
    
    /**
     * Returns a polygon shape corresponding to this border constraint, with
     * the input from the interpreter.
     * 
     * DEPRECATED. Use DottedInterpreter.getDeformedPolygon instead.
     * 
     * @param interpreter how to interpret the various edges.
     * @return a polygon
     */
    public Polygon toPolygon(DottedInterpreter interpreter)
    {
        int edgeCount = 0;
        Polygon polygon = new Polygon();

        int missingEdges = countEmptyEdges();
        PointIterator it = new PointIterator(interpreter.onMissingEdge(missingEdges));
        while (it.hasNext())
        {
            Point2D p = it.next();
            edgeCount++;
            
            polygon.addPoint(p);
        }
                        
        return polygon;
    }
    
    @Override
    public String toString()
    {
        String ret = new String();
        return southEdge + " " + eastEdge + " " + northEdge + " " + westEdge;
    }
}
