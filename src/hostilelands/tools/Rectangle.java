/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.tools;

import hostilelands.Settings;
import java.awt.geom.Point2D;

/**
 *
 * @author Elscouta
 */
public class Rectangle 
{
    public int xMin;
    public int xMax;
    public int yMin;
    public int yMax;
    
    public Rectangle(int width, int height)
    {
        this.xMin = 0;
        this.xMax = width;
        this.yMin = 0;
        this.yMax = height;
    }
    
    protected Rectangle(int xMin, int xMax, int yMin, int yMax)
    {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }
    
    public int getWidth()
    {
        return xMax - xMin;
    }
    
    public int getHeight()
    {
        return yMax - yMin;
    }
    
    public Line2D clip(Line2D line) 
    {   
        Point2D orig = line.getP1();
        double dx = line.getX2() - line.getX1();
        Line2D line2;
        
        if (Math.abs(dx) < Settings.FLOAT_PRECISION)
        {
            if (line.getX1() < xMin || line.getX1() > xMax)
                return null;
            
            line2 = line;
        }
        else
        {
            double ratio1 = (xMin - line.getX1()) / dx;
            double ratio2 = (xMax - line.getX1()) / dx;

            if (ratio1 < 0 && ratio2 < 0)
                return null;
            if (ratio1 > 1 && ratio2 > 1)
                return null;
            
            if (ratio1 < 0)
                ratio1 = 0;
            else if (ratio1 > 1)
                ratio1 = 1;
            
            if (ratio2 < 0)
                ratio2 = 0;
            else if (ratio2 > 1)
                ratio2 = 1;
            
            Vector2D vec = new Vector2D(line);
            line2 = new Line2D(vec.multiply(ratio1).apply(orig),
                              vec.multiply(ratio2).apply(orig));
                    
            assert(line2.getX1() > xMin - Settings.FLOAT_PRECISION);
            assert(line2.getX2() > xMin - Settings.FLOAT_PRECISION);
            assert(line2.getX1() < xMax + Settings.FLOAT_PRECISION);
            assert(line2.getX2() < xMax + Settings.FLOAT_PRECISION);
        }
        

        orig = line2.getP1();
        double dy = line2.getY2() - line2.getY1();
        Line2D line3;
        
        if (Math.abs(dy) < Settings.FLOAT_PRECISION)
        {
            if (line2.getY1() < yMin || line2.getY1() > yMax)
                return null;
            
            line3 = line2;
        }
        else
        {
            double ratio1 = (yMin - line2.getY1()) / dy;
            double ratio2 = (yMax - line2.getY1()) / dy;

            if (ratio1 < 0 && ratio2 < 0)
                return null;
            if (ratio1 > 1 && ratio2 > 1)
                return null;
            
            if (ratio1 < 0)
                ratio1 = 0;
            else if (ratio1 > 1)
                ratio1 = 1;
            
            if (ratio2 < 0)
                ratio2 = 0;
            else if (ratio2 > 1)
                ratio2 = 1;
            
            Vector2D vec = new Vector2D(line2);
            line3 = new Line2D(vec.multiply(ratio1).apply(orig),
                              vec.multiply(ratio2).apply(orig));
                    
            assert(line3.getX1() > xMin - Settings.FLOAT_PRECISION);
            assert(line3.getX2() > xMin - Settings.FLOAT_PRECISION);
            assert(line3.getX1() < xMax + Settings.FLOAT_PRECISION);
            assert(line3.getX2() < xMax + Settings.FLOAT_PRECISION);
        }

        return line3;
    }    
    
    /**
     * Shrinks the rectangle, according to ratio, but at least one pixel
     * @param ratio the ratio of reduction
     * @return a new rectangle
     */
    public Rectangle shrink(double ratio)
    {
        int w = (int) (getWidth()*ratio);
        int h = (int) (getHeight()*ratio);
        
        if (w < 1) w = 1;
        if (h < 1) h = 1;
        
        return new Rectangle(xMin + w, xMax - w,
                             yMin + h, yMax - h);
    }
    
    public boolean contains(Point2D p)
    {
        return p.getX() > xMin - Settings.FLOAT_PRECISION &&
               p.getX() < xMax + Settings.FLOAT_PRECISION &&
               p.getY() > yMin - Settings.FLOAT_PRECISION &&
               p.getY() < yMax + Settings.FLOAT_PRECISION;
    }
    
    public static Rectangle makeSquare(int size)
    {
        return new Rectangle(0, size, 0, size);
    }
}
