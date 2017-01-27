/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.champion.Champion;
import hostilelands.models.ListModel;
import java.util.*;
import javax.swing.event.*;

import hostilelands.models.PositionModel;
import hostilelands.models.OptionnalPositionModel;

/**
 * This class acts as controller of a party.
 * Multiple threads (the UI, the ShortTimeMgr) can access and modify it.
 * 
 * Two locks are available:
 * - Main lock the whole party
 * - Destination lock, for actions that only affect the destination.
 * 
 * @author Elscouta
 */
public class MainParty 
        extends MapObject 
        implements ModifiableTeam, ShortTimeEntity 
{
    final ListModel<Unit> persons = new ListModel<>();
    final MapObject destination = new BasicMapObject("move_marker");
    
    public MainParty()
    {
        position.setValue(7f, 7f);
    }
    
    public MapObject getDestination()
    {
        return destination;
    }
    
    /*
     * PersonListController Interface
     */
    @Override 
    public synchronized boolean canAddUnit(Unit c)
    {
        return true;
    }
    
    @Override 
    public synchronized boolean canRemoveUnit(Unit c)
    {
        if (!persons.contains(c))
            throw new NoSuchElementException();
        return true;
    }
    
    @Override 
    public synchronized void addUnit(Unit c)
    {
        if (!canAddUnit(c))
            throw new FailedRequirementException();
        persons.add(c);
    }
    
    @Override 
    public synchronized void removeUnit(Unit c)
    {
        if (!canRemoveUnit(c))
            throw new FailedRequirementException();
        persons.remove(c);
    }
        
    @Override 
    public synchronized ListModel<Unit> getUnits()
    {
        return persons;
    }    
        
    /*
     * MapDrawable interface
     */
    @Override 
    public String getIconIdentifier()
    {
        return "party";
    }

    /*
     * ShortTimeEntity interface
     */
    @Override
    public synchronized void simulateTime(int minutes)
    {
        double vX, vY, v;
        double speed = 0.5f;
        
        synchronized(destination)
        {        
            if (!destination.exists())
                return;
       
            vX = destination.getX() - position.getX();
            vY = destination.getY() - position.getY();
            v = Math.sqrt(vX*vX + vY*vY);
       
            if (v < Settings.PRECISION_DISTANCE || v < minutes * speed)
            {
                position.setValue(destination.getX(), destination.getY());
                destination.suppress();
                return;
            }
        }
            
        vX = speed * vX / v;
        vY = speed * vY / v;
       
        position.translate(vX * minutes, vY * minutes);
    }
}
