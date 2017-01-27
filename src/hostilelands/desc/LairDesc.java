/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.desc;

import hostilelands.DataStore;
import hostilelands.EnemyLair;
import hostilelands.Identifiable;
import hostilelands.Spawn;
import hostilelands.worldcreation.CreationTileDesc;
import java.io.IOException;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;

/**
 *
 * @author Elscouta
 */
public class LairDesc implements Identifiable
{
    private LevelDependantList<Spawn> spawns;
    private CreationTileDesc creationDesc;
    private String identifier;
    private String iconIdentifier;

    private LairDesc()
    {        
    }
    
    public List<Spawn> getSpawns(int level)
    {
        return spawns.atLevel(level);
    }
    
    public CreationTileDesc getCreationDesc()
    {
        return creationDesc;
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
    
    public static class Loader extends XMLLoader<LairDesc>
    {

        @Override
        public LairDesc loadXML(Element e, DataStore store) 
                throws XPathExpressionException, IOException 
        {
            LairDesc obj = new LairDesc();
            obj.identifier = getAttributeAsString("identifier", e);
            obj.spawns = getNodesAsObjectLDList("spawn", e, new SpawnDesc.Loader(), store);
            obj.iconIdentifier = getNodeAsString("icon", e);
            
            obj.creationDesc = new CreationTileDesc(() -> new EnemyLair(obj, 1), "lair");
            obj.creationDesc.loadXML(e, store);

            return obj;
        }        
    }
}
