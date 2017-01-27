/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.desc;

import hostilelands.AbstractIdentifiable;
import hostilelands.DataStore;
import hostilelands.SkillCheck;
import hostilelands.champion.AbstractSlottablePowerSource;
import hostilelands.champion.SlottablePowerSource;
import java.io.IOException;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;

/**
 *
 * @author Elscouta
 */
public class Ability extends AbstractSlottablePowerSource
{
    String name;
    
    public String getName()
    {
        return name;
    }
    
    public static class Loader extends XMLLoader<Ability>
    {
        @Override
        public Ability loadXML(Element e, DataStore store) 
                throws XPathExpressionException, IOException
        {
            Ability ability = new Ability();

            ability.loadXMLSlottablePowerSource(e, store, this);
            ability.name = getNodeAsString("name", e);
            
            return ability;
        }
    }
}
