/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.views;

import hostilelands.models.SingletonModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Elscouta
 */
public class SelectionModel<T, U extends JComponent & Togglable>
        extends SingletonModel<T>
{
    /**
     * Invariants: 
     * buttonValues[currentButton] == model.get()
     * adapters.keys() == buttonValues.keys()
     */

    Map<U, Adapter> adapters;
    Map<U, T> buttonValues;
    
    public SelectionModel()
    {
        adapters = new HashMap<>();
        buttonValues = new HashMap<>();
    }
    
    public synchronized void bindButton(U button, T value)
    {
        Adapter adapter = new Adapter(button);
        adapters.put(button, adapter);
        buttonValues.put(button, value);
        
        button.addMouseListener(adapter);
        addChangeListener(adapter);
    }
    
    public synchronized void unbindButton(U button)
    {
        Adapter adapter = adapters.get(button);
        removeChangeListener(adapter);
        button.removeMouseListener(adapter);
        
        adapters.remove(button);
        buttonValues.remove(button);
    }
    
    private class Adapter implements ChangeListener, MouseListener
    {
        U component;
        
        public Adapter(U c)
        {
            this.component = c;
        }
        
        @Override
        public void stateChanged(ChangeEvent e)
        {
            synchronized (SelectionModel.this)
            {
                if (buttonValues.get(component) == get())
                    component.setPressed();
                else
                    component.setReleased();
                    
            }
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            T value = buttonValues.get(component);
            component.setPressed();
            set(value);
        }
        
        @Override
        public void mousePressed(MouseEvent e)
        {
            component.setPressed();
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
        }
        
        @Override
        public void mouseEntered(MouseEvent e)
        {
        }
        
        @Override
        public void mouseExited(MouseEvent e)
        {
        }
    }
}
