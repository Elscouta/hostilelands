/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.desc;

import hostilelands.DataStore;
import hostilelands.Identifiable;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;

/**
 *
 * @author Elscouta
 */
public class BasicUnitPattern implements LevelDependant<BasicUnitDesc>, Identifiable
{
    private String identifier;
    private String name;
    private ImageIcon portrait;
    private LevelDependantList<Dice> dices;
    
    @Override
    public BasicUnitDesc atLevel(int level)
    {
        return new BasicUnitDesc(name, level, portrait, dices.atLevel(level));
    }
    
    @Override
    public String getIdentifier()
    {
        return identifier;
    }
    
    /**
     * The loader for the class.
     */
    public static class Loader extends XMLLoader<BasicUnitPattern>
    {
        @Override
        public BasicUnitPattern loadXML(Element e, DataStore store) 
                throws XPathExpressionException, IOException
        {
            BasicUnitPattern desc = new BasicUnitPattern();
            desc.identifier = getAttributeAsString("identifier", e);
            desc.name = getNodeAsString("name", e);
            desc.portrait = loadNodeAsIcon("portrait", e);
            desc.dices = getNodesAsObjectLDListFromStore("dice", e, store, store::getDicePattern);
        
            return desc;
        }
    }
}
