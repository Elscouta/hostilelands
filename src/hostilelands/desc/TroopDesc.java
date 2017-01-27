/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.desc;

import hostilelands.DataStore;
import hostilelands.Identifiable;
import java.io.IOException;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;

/**
 *
 * @author Elscouta
 */
public class TroopDesc implements Identifiable
{
    String identifier;
    String iconIdentifier;
    LevelDependantList<BasicUnitDesc> unit_descs;
    
    private TroopDesc()
    {
    }
    
    @Override
    public String getIdentifier()
    {
        return identifier;
    }
    
    public String getIconIdentifier()
    {
        return iconIdentifier;
    }
    
    public List<BasicUnitDesc> getUnitDescs(int level)
    {
        return unit_descs.atLevel(level);
    }
    
    public static class Loader extends XMLLoader<TroopDesc>
    {
        @Override
        public TroopDesc loadXML(Element e, DataStore store) 
                throws XPathExpressionException, IOException
        {
            TroopDesc teamdesc = new TroopDesc();
            teamdesc.identifier = getAttributeAsString("identifier", e);
            teamdesc.iconIdentifier = getNodeAsString("icon", e);
            teamdesc.unit_descs = getNodesAsObjectLDListFromStore("unit", e, store, store::getUnitPattern);
            
            return teamdesc;
        }    
    }
}
