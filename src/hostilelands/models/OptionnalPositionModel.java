/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.models;

/**
 * Model representing a map position, or an absence of the object.
 * Fully synchronized.
 * 
 * @author Elscouta
 */
public class OptionnalPositionModel extends PositionModel
{
    boolean e = false;
    
    @Override 
    public synchronized void setValue(double x, double y)
    {
        e = true;
        super.setValue(x, y);
    }
    
    @Override
    public synchronized void translate(double dx, double dy)
    {
        assert(e == true);
        super.translate(dx, dy);
    }
    
    public synchronized void suppress()
    {
        e = false;
        fireStateChanged();
    }
    
    public synchronized boolean exists()
    {
        return e;
    }
}
