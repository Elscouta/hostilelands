/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.views;

import hostilelands.desc.Dice;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Elscouta
 */
public final class DiceView extends JLabel 
{
    Dice.Face face;
    
    public DiceView(Dice dice) 
    {
        this.face = null;
        
        setIcon(new ImageIcon(dice.getFavoredFace().getImage()));
        setToolTipText("Isn't that a beautiful dice?");
    }
    
    void setFace(Dice.Face face)
    {
        this.face = face;
        
        setIcon(new ImageIcon(face.getImage()));
    }
}
