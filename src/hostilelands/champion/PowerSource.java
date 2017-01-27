/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.champion;

import hostilelands.SkillCheck;
import hostilelands.desc.CombatActive;
import hostilelands.desc.CombatPassive;
import hostilelands.desc.Dice;
import hostilelands.desc.MapActive;

/**
 *
 * @author Elscouta
 */
public interface PowerSource 
{
    Iterable<Dice> getDices(SkillCheck c);
    Iterable<CombatActive> getCombatActives(SkillCheck c);
    Iterable<CombatPassive> getCombatPassives(SkillCheck c);
    Iterable<MapActive> getMapActives();
}
