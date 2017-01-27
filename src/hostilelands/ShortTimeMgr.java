/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import java.util.*;

/**
 *
 * @author Elscouta
 */
public class ShortTimeMgr extends Thread 
{
    ArrayList<ShortTimeEntity> entities = new ArrayList<>();
    boolean paused = false;
    
    private ShortTimeMgr()
    {
    }
    
    @Override
    public void run()
    {
        while (true)
        {
            synchronized (this)
            {
                if (!paused)
                {
                    for (int i = 0; i < entities.size(); i++)
                    {
                        entities.get(i).simulateTime(1);
                    }
                }
            }
            
            try
            {
                sleep(100);
            }
            catch (InterruptedException e)
            {
                return;
            }
        }
    }
    
    public synchronized void addEntity(ShortTimeEntity e)
    {
        entities.add(e);
    }
    
    public synchronized void removeEntity(ShortTimeEntity e)
    {
        entities.remove(e);
    }
    
    static public ShortTimeMgr create()
    {
        ShortTimeMgr mgr = new ShortTimeMgr();
        mgr.start();
        return mgr;
    }
    
    public synchronized void pause()
    {
        paused = true;
    }
    
    public synchronized void unpause()
    {
        paused = false;
    }
}
