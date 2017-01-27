/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.models;

/**
 * Model storing a map position.
 * Fully synchronized.
 *
 * @author Elscouta
 */
public class PositionModel extends ChangeEventSource
{
    double x = 0;
    double y = 0;
    
    public PositionModel()
    {
        
    }
            
    public PositionModel(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
    
    public synchronized void setValue(double x, double y)
    {
        this.x = x;
        this.y = y;
        fireStateChanged();
    }
    
    public synchronized void setValue(PositionModel p)
    {
        setValue(p.getX(), p.getY());
    }

    public synchronized void translate(double dx, double dy)
    {
        this.x += dx;
        this.y += dy;
        fireStateChanged();
    }
    
    public synchronized double getX()
    {
        return x;
    }
    
    public synchronized double getY()
    {
        return y;
    }    
    
    /**
     * DANGER: This might cause a deadlock.
     * 
     * @param other The other position to compare.
     * @return The distance between the two positions.
     */
    public synchronized double distanceTo(PositionModel other)
    {
        synchronized(other)
        {
            double dx = x - other.x;
            double dy = y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
        }
    }
}
