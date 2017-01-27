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
public class Vector2D 
{
        private final double x;
        private final double y;
        
        public Vector2D(double x, double y)
        {
            this.x = x;
            this.y = y;
        }
        
        public Vector2D(Point2D start, Point2D end)
        {
            this.x = end.getX() - start.getX();
            this.y = end.getY() - start.getY();
        }
        
        public Vector2D(Line2D line)
        {
            this.x = line.getX2() - line.getX1();
            this.y = line.getY2() - line.getY1();
        }
        
        public Vector2D multiply(double ratio)
        {
            return new Vector2D(x*ratio, y*ratio);
        }
        
        public Vector2D half()
        {
            return multiply(0.5f);
        }
        
        public Vector2D reverse()
        {
            return multiply(-1f);
        }
        
        public double getX()
        {
            return x;
        }
        
        public double getY()
        {
            return y;
        }
        
        public Vector2D rotateQuarterClockwise()
        {
            return new Vector2D(y, - x);
        }
        
        public Vector2D rotateQuarterCounterClockwise()
        {
            return new Vector2D(-y, x);
        }
        
        public Point2D apply(Point2D pt)
        {
            return new Point2D.Double(pt.getX()+x, pt.getY()+y);
        }
    
}
