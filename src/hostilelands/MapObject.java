/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import javax.swing.event.*;

import hostilelands.models.PositionModel;
import hostilelands.models.OptionnalPositionModel;

/**
 * Adapter of OptionnalPositionModel to allow easier implementation
 * of MapDrawable
 * 
 * @author Elscouta
 */
public abstract class MapObject implements MapDrawable
{
    World world;
    OptionnalPositionModel position = new OptionnalPositionModel();
    
    public boolean exists()
    {
        return position.exists();
    }
    
    protected void init(World w, double x, double y)
    {
        world = w;
        setPosition(x, y);
    }
    
    public void suppress()
    {
        world = null;
        position.suppress();
    }
    
    public PositionModel getPosition()
    {
        assert(position.exists());
        return position;
    }
    
    public void setPosition(double x, double y)
    {
        position.setValue(x, y);
    }
    
    public void addPositionListener(ChangeListener l)
    {
        position.addChangeListener(l);
    }
    
    public void removePositionListener(ChangeListener l)
    {
        position.removeChangeListener(l);
    }
    
    @Override
    public double getX()
    {
        assert(position.exists());
        return position.getX();
    }
    
    @Override
    public double getY()
    {
        assert(position.exists());
        return position.getY();
    }             
}
