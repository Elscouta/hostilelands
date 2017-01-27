/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.champion;

import hostilelands.desc.Ability;
import hostilelands.desc.School;

/**
 *
 * @author Elscouta
 */
public class Abilities extends SlotSet<Ability>
{
    public Abilities(School main_school)
    {
        super(main_school.getAbilitySlots());
    }
}
