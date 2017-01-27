/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.champion;

import hostilelands.desc.ChampionDesc;
import hostilelands.desc.School;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elscouta
 */
public class Stats extends PowerSourceAggregate
{
    School main_school;
    
    public Stats(ChampionDesc desc)
    {
        main_school = desc.getSchool();
    }
    
    public School getMainSchool()
    {
        return main_school;
    }

    @Override
    protected List<PowerSource> getPowerSources() 
    {
        return new ArrayList<>();
    }
            
}
