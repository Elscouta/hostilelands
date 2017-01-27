/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.desc;

import hostilelands.DataStore;
import hostilelands.Spawn;
import java.io.IOException;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;

/**
 *
 * @author Elscouta
 */
public class SpawnDesc implements LevelDependant<Spawn>
{
    private TroopDesc spawningType;
    private LevelDependantInteger level;
    private LevelDependantInteger averageTime;
    
    @Override
    public Spawn atLevel(int lvl)
    {
        return new Spawn(spawningType, level.atLevel(lvl), averageTime.atLevel(lvl));
    }
    
    public static class Loader extends XMLLoader<SpawnDesc>
    {

        @Override
        public SpawnDesc loadXML(Element e, DataStore store) 
                throws XPathExpressionException, IOException 
        {
            SpawnDesc obj = new SpawnDesc();
            obj.spawningType = getNodeAsObjectFromStore("type", e, store, store::getTroopDesc);
            obj.level = getNodeAsObject("level", e, new LevelDependantInteger.Loader(), store);
            obj.averageTime = getNodeAsObject("time", e, new LevelDependantInteger.Loader(), store);
            
            return obj;
        }
        
    }
}
