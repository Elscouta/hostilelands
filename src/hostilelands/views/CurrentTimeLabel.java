/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.views;

import javax.swing.*;
import javax.swing.event.*;

import hostilelands.models.IntegerModel;

/**
 *
 * @author Elscouta
 */
public final class CurrentTimeLabel extends JLabel 
{
    IntegerModel time;
    
    CurrentTimeLabel(IntegerModel time)
    {
        this.time = time;
        this.time.addChangeListener(new Listener());
    
        setText(String.format("Time: %d", time.getValue()));
        repaint();
    }
        
    class Listener implements ChangeListener
    {
        @Override
        public void stateChanged(ChangeEvent e)
        {
            setText(String.format("Time: %d", time.getValue()));
            repaint();
        }
    }
}
