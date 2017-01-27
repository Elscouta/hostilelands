/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.desc;

import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;

import hostilelands.DataStore;
import hostilelands.Identifiable;
import java.util.stream.Collectors;


/**
 * The description of a basic unit, that has a set of dices
 * and a portrait.
 * 
 * This is a lightweight storage class, instantiated for temporary use
 * by BasicUnitPattern, that "owns" the used ressources.
 * 
 * @author Elscouta
 */
public class BasicUnitDesc
{
    private final String name;
    private final int level;
    private final ImageIcon portrait;
    private final List<Dice> dices;
    
    BasicUnitDesc(String name, int level, ImageIcon portrait, List<Dice> dices)
    {
        this.name = name;
        this.level = level;
        this.portrait = portrait;
        this.dices = dices;
    }
    
    /**
     * Returns the name of the unit, including the level.
     * 
     * @return A forged string with the name and the level of the unit.
     */
    public String getName()
    {
        return String.format("%s (Lvl %d)", name, level);
    }
    
    /**
     * Returns the portrait of the unit.
     * Does not depend on level.
     * @return A stored imageIcon.
     */
    public ImageIcon getPortrait()
    {
        return portrait;
    }
    
    /**
     * Returns the list of dices the unit is entitled to
     * @return A list of dices.
     */
    public List<Dice> getDices()
    {
        return dices;
    }    
}
