/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.champion;

import hostilelands.desc.Stat;
import java.util.Map;

/**
 *
 * @author Elscouta
 */
public interface SlottablePowerSource extends PowerSource
{
    boolean hasTrait(String trait);
    Map<Stat, Integer> getRequirement(); 
}
