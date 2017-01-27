/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.champion;

import hostilelands.desc.School;

/**
 *
 * @author Elscouta
 */
public class Equipement extends SlotSet<Item>
{
    public Equipement(School school)
    {
        super(school.getItemSlots());
    }
}
