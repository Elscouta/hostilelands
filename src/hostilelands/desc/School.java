/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.desc;

import hostilelands.AbstractIdentifiable;
import hostilelands.DataStore;
import java.io.IOException;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;

/**
 *
 * @author Elscouta
 */
public class School extends AbstractIdentifiable
{
    private List<Ability> abilities;
    private List<SlotDesc> ability_slots;
    private List<SlotDesc> item_slots;
    
    /**
     * FIXME: This shouldn't be there. Scheduled for removal.
     */
    public List<Ability> getPossibleAbilities()
    {
        return abilities;
    }
    
    public List<SlotDesc> getAbilitySlots()
    {
        return ability_slots;
    }
    
    public List<SlotDesc> getItemSlots()
    {
        return item_slots;
    }
    
    public static class Loader extends XMLLoader<School>
    {
        @Override
        public School loadXML(Element e, DataStore store) 
                throws XPathExpressionException, IOException
        {
            School obj = new School();
            obj.loadXMLIdentifiable(e, store, this);
            
            obj.abilities = getNodesAsObjectListFromStore("ability", e, store, store::getAbility);
            obj.ability_slots = getNodesAsObjectList("slot[@category='ability']", e, new SlotDesc.Loader(), store);
            obj.item_slots = getNodesAsObjectList("slot[@category='item']", e, new SlotDesc.Loader(), store);
            return obj;
        }
    }
}
