/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.models;

import javax.swing.SwingUtilities;
import javax.swing.event.*;

/**
 * Base class that provide basic event firing functionality.
 * Fully synchronized.
 * 
 * @author Elscouta
 */
public class ChangeEventSource 
{
    protected ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();
    
    public synchronized void addChangeListener(ChangeListener l) 
    {
        listenerList.add(ChangeListener.class, l);
    }
 
    public synchronized void removeChangeListener(ChangeListener l) 
    {
        listenerList.remove(ChangeListener.class, l);
    }
 
    protected synchronized void fireStateChanged() 
    {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -=2 ) {
            if (listeners[i] == ChangeListener.class) 
            {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                
                final ChangeListener l = (ChangeListener) listeners[i+1];
                SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run() {
                        l.stateChanged(changeEvent);                       
                    }
                });

            }
        }
    }
}
