/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.Settings;
import hostilelands.tools.Intersectable;
import hostilelands.tools.Pair;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Elscouta
 */
public class Polygon implements Intersectable
{
    List<Point2D> points;
    
    private class LineIterator implements Iterator<Line2D>
    {
        private int index;

        public LineIterator()
        {
            index = 0;
        }
        
        @Override
        public boolean hasNext()
        {
            return points.size() > 1 && index < points.size();
        }
        
        @Override
        public Line2D next()
        {
            int previousindex = index;
            int nextindex = index+1;
            if (nextindex == points.size())
                nextindex = 0;
            
            index++;
            
            return new Line2D.Double(points.get(previousindex), points.get(nextindex));
        }
    }
    
    private interface CoordsAdapter
    {
        public double getFirstCoord(Point2D p);
        public double getSecondCoord(Point2D p);
    }
    
    private static class HorizAdapter implements CoordsAdapter
    {
        @Override public double getFirstCoord(Point2D p) { return p.getX(); }
        @Override public double getSecondCoord(Point2D p) { return p.getY(); }
    }
    
    private static class VertAdapter implements CoordsAdapter
    {
        @Override public double getFirstCoord(Point2D p) { return p.getY(); }
        @Override public double getSecondCoord(Point2D p) { return p.getX(); }        
    }
        
    public Polygon()
    {
        points = new ArrayList<>();
    }
    
    public void addPoint(Point2D point)
    {
        assert (point.getX() > -0.1f);
        assert (point.getY() > -0.1f);
        points.add(point);
    }
    
    private Pair<Integer, Integer> clip(int a1, int a2, int amin, int amax)
    {
        assert(amin <= amax);
        
        if (a1 < amin && a2 < amin)
            return null;
        
        if (a1 > amax && a2 > amax)
            return null;
        
        if (a1 < amin)
            a1 = amin;
        else if (a1 > amax)
            a1 = amax;
        
        if (a2 < amin)
            a2 = amin;
        else if (a2 > amax)
            a2 = amax;
        
        if (a2 < a1)
            return new Pair(a2-amin, a1-amin);
        else
            return new Pair(a1-amin, a2-amin);
    }
    
    private DottedLine getIntersections(int b, int amin, int amax, CoordsAdapter adapt)
    {
        assert(amin < amax);
        
        List<Integer> intersections = new ArrayList<>();
        List<Pair<Integer,Integer>> exact_matches = new ArrayList<>();
        boolean addInitial = false;
        boolean addFinal = false;
        
        LineIterator it = new LineIterator();
        while (it.hasNext())
        {
            Line2D line = it.next();

            int a1 = (int) adapt.getFirstCoord(line.getP1());
            int a2 = (int) adapt.getFirstCoord(line.getP2());
            int b1 = (int) adapt.getSecondCoord(line.getP1());
            int b2 = (int) adapt.getSecondCoord(line.getP2());
            
            
            if (b1 == b && b2 == b)
            {
                Pair<Integer, Integer> p = clip(a1, a2, amin, amax);
                if (p != null)
                    exact_matches.add(p);                
            }
            else if (b1 == b && b2 != b)
            {
                Pair<Integer, Integer> p = clip(a1, a1, amin, amax);
                if (p != null)
                    exact_matches.add(p);                                
            }
            else if (b1 != b && b2 == b)
            {
                Pair<Integer, Integer> p = clip(a2, a2, amin, amax);
                if (p != null)
                    exact_matches.add(p);                                                
            }            
            
            // No intersection: strictly below.
            if (b1 <= b && b2 <= b)
                continue;
            
            // No intersection: strictly above.
            if (b1 > b && b2 > b)
                continue;
            
            double slope = ((double) (a2 - a1)) / ((double) (b2 - b1));
            int aIntersect = (int) (a1 + slope * (b - b1));
            
            if (aIntersect < amin)
                addInitial = !addInitial;
            else if (aIntersect > amax)
                addFinal = !addFinal;
            else
                intersections.add(new Integer(aIntersect - amin));
        }
        
        if (addInitial)
            intersections.add(0);
        
        if (addFinal)
            intersections.add(amax - amin);
        
        return new DottedLine(amax - amin, intersections, exact_matches);        
    }
    
    /**
     * Checks the intersection of the polygon with a horizontal segment
     * @param y the y coordinate of the horizontal segment
     * @param xmin the minimum coordinate of the horizontal segment
     * @param xmax the maximum coordinate of the horizontal segment
     * @return a dotted line representing the parts of the segment that are inside
     * the polygon.
     */
    @Override
    public DottedLine getHorizontalIntersections(int y, int xmin, int xmax)
    {
        return getIntersections(y, xmin, xmax, new HorizAdapter());
    }
    
    /**
     * Checks the intersection of the polygon with a vertical segment
     * @param x the x coordinate of the vertical segment
     * @param ymin the minimum coordinate of the vertical segment
     * @param ymax the maximum coordinate of the vertical segment
     * @return a dotted line representing the parts of the segment that are inside
     * the polygon.
     */
    @Override
    public DottedLine getVerticalIntersections(int x, int ymin, int ymax)
    {
        return getIntersections(x, ymin, ymax, new VertAdapter());
    }
    
    /**
     * Returns the first edge of the polygon.
     * @return the first edge of the polygon
     */
    public Line2D getFirstEdge()
    {
        assert(points.size() >= 2);
        
        return new Line2D.Double(points.get(0), points.get(1));
    }
    
    @Override
    public String toString()
    {
        String s = new String();
        Iterator<Point2D> it = points.iterator();
        while (it.hasNext())
        {
            Point2D p = it.next();
            s += "(" + p.getX() + ", " + p.getY() + ")";
            
            if (it.hasNext())
                s += " -- ";
        }
        
        return s;
    }
}
