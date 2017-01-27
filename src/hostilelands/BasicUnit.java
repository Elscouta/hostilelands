/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.desc.Dice;
import hostilelands.desc.BasicUnitDesc;
import hostilelands.desc.CombatActive;
import hostilelands.desc.CombatPassive;
import hostilelands.desc.MapActive;
import java.util.Iterator;
import java.util.List;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.ImageIcon;

/**
 *
 * @author Elscouta
 */
public class BasicUnit implements Unit {

    String name;
    ImageIcon portrait;
    List<Dice> dices;

    public BasicUnit(BasicUnitDesc desc) {
        this.name = desc.getName();
        this.portrait = desc.getPortrait();
        this.dices = desc.getDices();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ImageIcon getPortrait() {
        return portrait;
    }

    @Override
    public List<Dice> getDices(SkillCheck check) {
        return dices;
    }

    @Override
    public Iterable<CombatActive> getCombatActives(SkillCheck check) 
    {
        return new ArrayList<>();
    }

    @Override
    public Iterable<CombatPassive> getCombatPassives(SkillCheck check)
    {
        return new ArrayList<>();
    }
    
    @Override
    public Iterable<MapActive> getMapActives()
    {
        return new ArrayList<>();
    }
}
