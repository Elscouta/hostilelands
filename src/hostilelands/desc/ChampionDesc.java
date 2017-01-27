/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.desc;

import hostilelands.AbstractIdentifiable;
import hostilelands.DataStore;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;

/**
 *
 * @author Elscouta
 */
public class ChampionDesc extends AbstractIdentifiable
{
    ImageIcon portrait;
    String name;
    School school;
    
    public String getName()
    {
        return name;
    }
    
    public ImageIcon getPortrait()
    {
        return portrait;
    }
    
    public School getSchool()
    {
        return school;
    }
    
    public static class Loader extends XMLLoader<ChampionDesc>
    {
        @Override
        public ChampionDesc loadXML(Element e, DataStore store) 
                throws IOException, XPathExpressionException
        {
            ChampionDesc desc = new ChampionDesc();
            desc.loadXMLIdentifiable(e, store, this);
            desc.portrait = loadNodeAsIcon("portrait", e);
            desc.name = getNodeAsString("name", e);
            desc.school = getNodeAsObjectFromStore("class", e, store, store::getSchool);
            
            return desc;
        }
    }
}
