/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.champion;

import hostilelands.AbstractIdentifiable;
import hostilelands.DataStore;
import hostilelands.SkillCheck;
import hostilelands.desc.XMLBasicLoader;
import hostilelands.desc.CombatActive;
import hostilelands.desc.CombatPassive;
import hostilelands.desc.Dice;
import hostilelands.desc.MapActive;
import hostilelands.desc.Stat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;

/**
 *
 * @author Elscouta
 */
public class AbstractSlottablePowerSource extends AbstractIdentifiable 
                                          implements SlottablePowerSource
{
    List<Dice> dices;

    protected AbstractSlottablePowerSource()
    {
        dices = new ArrayList<>();
    }
    
    protected void loadXMLSlottablePowerSource(Element e, 
                                               DataStore store, 
                                               XMLBasicLoader loader)
            throws XPathExpressionException, IOException
    {
        loadXMLIdentifiable(e, store, loader);
        
        dices = loader.getNodesAsObjectList("dice", e, store.getLDLoader(store::getDicePattern), store);
    }
    
    @Override
    public List<Dice> getDices(SkillCheck c)
    {
        return dices;
    }
    
    @Override
    public List<CombatActive> getCombatActives(SkillCheck check) 
    {
        return new ArrayList<>();
    }

    @Override
    public List<CombatPassive> getCombatPassives(SkillCheck check)
    {
        return new ArrayList<>();
    }
    
    @Override
    public List<MapActive> getMapActives()
    {
        return new ArrayList<>();
    }
    
    @Override
    public Map<Stat, Integer> getRequirement()
    {
        return null;
    }
    
    @Override
    public boolean hasTrait(String trait)
    {
        return true;
    }
}
