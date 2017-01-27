/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.desc;

import hostilelands.DataStore;
import hostilelands.HomeTown;
import hostilelands.Identifiable;
import hostilelands.Town;
import hostilelands.worldcreation.CreationTileDesc;
import java.io.IOException;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;

/**
 *
 * @author Elscouta
 */
public class TownDesc implements Identifiable
{
    private CreationTileDesc creationDesc;
    private boolean homeTown;
    private String identifier;
    private String iconIdentifier;
    
    private TownDesc()
    {        
    }
    
    @Override
    public String getIdentifier()
    {
        return identifier;
    }
    
    public CreationTileDesc getCreationDesc()
    {
        return creationDesc;
    }
    
    public static class Loader extends XMLLoader<TownDesc>
    {
        @Override
        public TownDesc loadXML(Element e, DataStore store)
                throws XPathExpressionException, IOException
        {
            TownDesc obj = new TownDesc();
            
            obj.homeTown = getNodeAsBoolean("hometown", e);
            obj.identifier = getAttributeAsString("id", e);
            obj.iconIdentifier = getNodeAsString("icon", e);
            
            if (obj.homeTown)
                obj.creationDesc = new CreationTileDesc(() -> new hostilelands.HomeTown(), "town");
            else
                obj.creationDesc = new CreationTileDesc(() -> new hostilelands.AITown(), "town");
            
            obj.creationDesc.loadXML(e, store);
            
            return obj;
        }
    }            
}
