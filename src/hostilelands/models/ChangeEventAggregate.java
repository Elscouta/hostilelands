/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.models;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Elscouta
 */
public class ChangeEventAggregate extends ChangeEventSource
{
    protected Listener listener;
    
    protected ChangeEventAggregate()
    {
        this.listener = new Listener();
    }
    
    protected class Listener implements ChangeListener
    {
        @Override
        public void stateChanged(ChangeEvent e)
        {
            fireStateChanged();
        }
    }
        
}
