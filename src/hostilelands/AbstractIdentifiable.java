/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.desc.XMLBasicLoader;
import org.w3c.dom.Element;

/**
 *
 * @author Elscouta
 */
public abstract class AbstractIdentifiable implements Identifiable
{
    private String identifier;
        
    public String getIdentifier()
    {
        return identifier;
    }
    
    public void loadXMLIdentifiable(Element e, DataStore store, XMLBasicLoader loader)
    {
        identifier = loader.getAttributeAsString("identifier", e);
    }
}
