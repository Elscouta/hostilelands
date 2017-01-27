/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.desc.XMLLoader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @param T the class to..
 * @author Elscouta
 */
public class XMLStore<T extends Identifiable>
{
    XMLLoader<T> loader;
    Map<String, T> store;
    
    public XMLStore(XMLLoader<T> loader)
    {
        this.loader = loader;
    }
    
    public final void load(String pathname, String nodename, DataStore mainstore) 
            throws IOException
    {
        try {
            this.store = new HashMap<>();
            
            File fDesc = new File(pathname);
            Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fDesc);
            xmlDoc.getDocumentElement().normalize();
                
            NodeList nodeList = xmlDoc.getElementsByTagName(nodename);
            for (int i = 0; i < nodeList.getLength(); i++)
            {
                Element e = (Element) nodeList.item(i);

                T desc = loader.loadXML(e, mainstore);
                store.put(desc.getIdentifier(), desc);
            
                System.out.printf("loaded %s: %s - %s\n", nodename, desc.getIdentifier(), desc.toString());
            }
        }
        catch (ParserConfigurationException e) {
            throw new IOException("Could not configure XML parser");
        }
        catch (XPathExpressionException e) {
            throw new IOException("Failed XPath expression");
        }
        catch (SAXException e) {
            throw new IOException("Could not parse XML File");
        }        
    }
    
    public T get(String identifier)
    {
        assert(identifier != null && !identifier.equals(""));
        
        T elem = store.get(identifier);
        
        if (elem == null)
            System.out.printf("WARNING: Store miss [%s]\n", identifier);
        
        return elem;
    }

}
