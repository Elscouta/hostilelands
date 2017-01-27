/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import java.awt.geom.Point2D;

/**
 *
 * @author Elscouta
 */
public abstract class Deformation extends Polygon
{
    abstract boolean isPositive();
            
    private Deformation(Point2D a, Point2D b, Point2D c)
    {
        super();
        
        addPoint(a);
        addPoint(b);
        addPoint(c);
    }
    
    public static class Positive extends Deformation
    {
        @Override
        public boolean isPositive() { return true; }
        
        public Positive(Point2D a, Point2D b, Point2D c)
        {
            super(a, b, c);
        }
    }
    
    public static class Negative extends Deformation
    {
        @Override
        public boolean isPositive() { return false; }
        
        public Negative(Point2D a, Point2D b, Point2D c)
        {
            super(a, b, c);
        }
    }
}
