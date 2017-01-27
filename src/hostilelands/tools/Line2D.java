/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.tools;

import java.awt.geom.Point2D;

/**
 *
 * @author Elscouta
 */
public class Line2D extends java.awt.geom.Line2D.Double
{
    public Line2D(Point2D orig, Point2D end)
    {
        super(orig,end);
    }
    
    public Line2D(Point2D orig, Vector2D vec)
    {
        super(orig, vec.apply(orig));
    }
    
    public Line2D(double x1, double y1, double x2, double y2)
    {
        super(x1, y1, x2, y2);
    }
    
    public Point2D pointAt(double ratio)
    {
        return (new Vector2D(this)).multiply(ratio).apply(getP1());
    }
    
    public Point2D randomPoint()
    {
        return pointAt(Math.random());
    }
}
