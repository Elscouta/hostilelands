/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.champion;

import hostilelands.Unit;
import hostilelands.desc.Ability;
import hostilelands.desc.ChampionDesc;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 *
 * @author Elscouta
 */
public class Champion extends PowerSourceAggregate implements Unit
{
    final String name;
    final ImageIcon portrait;
    final Abilities abilities;
    final Equipement equipement;
    final Stats stats;
    
    final List<PowerSource> sources;
    
    public Champion(ChampionDesc desc) 
    {
        this.name = desc.getName();
        this.portrait = desc.getPortrait();
        
        this.stats = new Stats(desc);
        this.abilities = new Abilities(stats.getMainSchool());
        this.equipement = new Equipement(stats.getMainSchool());
        
        this.sources = new ArrayList<>();
        sources.add(abilities);
        sources.add(equipement);
        sources.add(stats);
    }
    
    protected List<PowerSource> getPowerSources()
    {
        return sources;
    }

    @Override
    public String getName()
    {
        return name;
    }
    
    @Override
    public ImageIcon getPortrait()
    {
        return portrait;
    }
    
    public List<Ability> getKnownAbilities()
    {
        return stats.getMainSchool().getPossibleAbilities();
    }
    
    public Abilities getAbilities()
    {
        return abilities;
    }
}
