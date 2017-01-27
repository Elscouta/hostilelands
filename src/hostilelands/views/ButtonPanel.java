/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.views;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author Elscouta
 */
public abstract class ButtonPanel extends JPanel implements Togglable
{
    boolean pressed;
    Border pressedBorder;
    Border releasedBorder;
    
    ButtonPanel()
    {
        Border raisedBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        releasedBorder = BorderFactory.createCompoundBorder(raisedBorder, raisedBorder);
        
        Border loweredBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        pressedBorder = BorderFactory.createCompoundBorder(loweredBorder, loweredBorder);
        
        setReleased();
    }

    @Override
    public void setPressed()
    {
        pressed = true;
        
        setBorder(pressedBorder);
    }
    
    @Override
    public void setReleased()
    {
        pressed = false;
        
        setBorder(releasedBorder);
    }
}
