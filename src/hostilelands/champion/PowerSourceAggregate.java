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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elscouta
 */
public abstract class PowerSourceAggregate implements PowerSource
{
    protected abstract List<PowerSource> getPowerSources();
    
    @Override
    public List<Dice> getDices(SkillCheck c)
    {
        List<Dice> dices = new ArrayList<>();
        
        for (PowerSource source : getPowerSources())
            for (Dice dice : source.getDices(c))
                dices.add(dice);
        
        return dices;
    }

    @Override
    public List<CombatActive> getCombatActives(SkillCheck c)
    {
        List<CombatActive> ret = new ArrayList<>();
        
        for (PowerSource source : getPowerSources())
            for (CombatActive active : source.getCombatActives(c))
                ret.add(active);
        
        return ret;
    }
    
    @Override
    public List<CombatPassive> getCombatPassives(SkillCheck c)
    {
        List<CombatPassive> ret = new ArrayList<>();
        
        for (PowerSource source : getPowerSources())
            for (CombatPassive p : source.getCombatPassives(c))
                ret.add(p);
        
        return ret;
    }
    
    @Override
    public List<MapActive> getMapActives()
    {
        List<MapActive> ret = new ArrayList<>();
        
        for (PowerSource source : getPowerSources())
            for (MapActive a : source.getMapActives())
                ret.add(a);
        
        return ret;
    }
}
