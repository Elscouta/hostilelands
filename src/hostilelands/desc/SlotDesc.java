/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.desc;

import hostilelands.DataStore;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;

/**
 *
 * @author Elscouta
 */
public class SlotDesc 
{
    private String name;
    private String trait;
    
    public String getName()
    {
        return name;
    }
    
    public String getTrait()
    {
        return trait;
    }
    
    public static class Loader extends XMLLoader<SlotDesc>
    {
        @Override
        public SlotDesc loadXML(Element e, DataStore store) 
                throws XPathExpressionException
        {
            SlotDesc desc = new SlotDesc();
            desc.name = getNodeAsString("name", e);
            desc.trait = getNodeAsString("trait", e);
            
            return desc;
        }
    }
}
