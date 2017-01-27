/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.desc.CombatActive;
import hostilelands.desc.CombatPassive;
import hostilelands.desc.Dice;
import hostilelands.desc.MapActive;

import javax.swing.ImageIcon;

/**
 *
 * @author Elscouta
 */
public interface Unit 
{
    public String getName();
    public ImageIcon getPortrait();
    public Iterable<Dice> getDices(SkillCheck check);
    public Iterable<CombatActive> getCombatActives(SkillCheck check);
    public Iterable<CombatPassive> getCombatPassives(SkillCheck check);
    public Iterable<MapActive> getMapActives();
}
