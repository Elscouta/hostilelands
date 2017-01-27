/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.models;

import hostilelands.desc.Dice;

/**
 *
 * @author Elscouta
 */
public class DiceModel extends ChangeEventSource
{
    Dice dice;
    Dice.Face current_face;

    public DiceModel(Dice dice)
    {
        this.dice = dice;
        this.current_face = null;
    }
    
    public synchronized Dice getDice()
    {
        return dice;
    }
    
    public synchronized Dice.Face getFace()
    {
        return current_face;
    }
    
    public synchronized Dice.Face roll()
    {
        assert(current_face == null);
        current_face = dice.roll();
        fireStateChanged();
        return current_face;
    }
    
    public synchronized Dice.Face reroll()
    {
        assert(current_face != null);
        current_face = dice.roll();
        fireStateChanged();
        return current_face;
    }
            
    public synchronized boolean rolled()
    {
        return current_face != null;
    }
}
