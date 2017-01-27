/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.desc;

import hostilelands.DataStore;
import hostilelands.Identifiable;
import hostilelands.XMLStore;
import hostilelands.desc.XMLLoader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Elscouta
 */
public class XMLBasicLoader 
{        
    protected XPath xpath;
    
    public XMLBasicLoader()
    {
        xpath = XPathFactory.newInstance().newXPath();
    }
    
    /**
     * Returns a predicate on the required level for the node to be active,
     * based on all the enclosing nodes with a level condition.
     * 
     * @param current_node The node being tested
     * @throws XPathExpressionException Shouldn't be thrown.
     * @return The condition on the level for the node being active
     */
    protected Predicate<Integer> getLevelCondition(Element current_node)
            throws XPathExpressionException
    {
        NodeList gtConditions = (NodeList) xpath.evaluate("./ancestor-or-self::*[@if-lvl-gt]", current_node, XPathConstants.NODESET);
        NodeList ltConditions = (NodeList) xpath.evaluate("./ancestor-or-self::*[@if-lvl-lt]", current_node, XPathConstants.NODESET);

        int greaterLowerBound = 0, lowerUpperBound = Integer.MAX_VALUE;
        
        for (int i = 0; i < gtConditions.getLength(); i++)
        {
            Element e = (Element) gtConditions.item(i);
            int lb = Integer.decode(e.getAttribute("if-lvl-gt"));
            if (greaterLowerBound < lb)
                greaterLowerBound = lb;
        }
        
        for (int i = 0; i < ltConditions.getLength(); i++)
        {
            Element e = (Element) ltConditions.item(i);
            int ub = Integer.decode(e.getAttribute("if-lvl-lt"));
            if (lowerUpperBound > ub)
                lowerUpperBound = ub;
        }
        
        final int myGLB = greaterLowerBound;
        final int myLUB = lowerUpperBound;
        
        return lvl -> lvl > myGLB && lvl < myLUB;
    }
    
    /**
     * Returns a function converting the level of the enclosing object to
     * the level of the child being loaded.
     * 
     * FIXME: TO IMPLEMENT
     * 
     * @param current_node The child node being loaded
     * @return A int->int function allowing level conversion
     */
    protected LevelDependant<Integer> getLevelConversion(Element current_node)
    {
        return new LevelDependantInteger.Identity();
    }
    
    public String getAttributeAsString(String key, Element current_node)
    {
        return current_node.getAttribute(key);
    }
    
    public String getContentAsString(Element current_node)
    {
        return current_node.getTextContent();
    }
    
    public String getNodeAsPath(String nodepath, Element current_node) 
            throws XPathExpressionException
    {
        return getNodeAsString(nodepath, current_node);
    }
    
    public boolean getNodeAsBoolean(String nodepath, Element current_node)
            throws XPathExpressionException
    {
        NodeList l = (NodeList) xpath.evaluate(nodepath, current_node, XPathConstants.NODESET);

        return l.getLength() > 0;
    }
    
    public int getNodeAsInt(String path, Element current_node) 
            throws XPathExpressionException
    {
       return ((Double) xpath.evaluate(path, current_node, XPathConstants.NUMBER)).intValue(); 
    }
    
    public double getNodeAsDouble(String path, Element current_node)
            throws XPathExpressionException
    {
       return ((Double) xpath.evaluate(path, current_node, XPathConstants.NUMBER)); 
    }
    
    public String getNodeAsString(String path, Element current_node) 
            throws XPathExpressionException
    {
        return (String) xpath.evaluate(path, current_node, XPathConstants.STRING);
    }
    
    public <U> U getNodeAsObject(String path, Element current_node, XMLLoader<U> loader, DataStore store) 
            throws XPathExpressionException, IOException
    {
        Node n = (Node) xpath.evaluate(path, current_node, XPathConstants.NODE);
        assert(n.getNodeType() == Node.ELEMENT_NODE);
        
        Element e = (Element) n;
        
        return loader.loadXML(e, store);
    }
    
    
    public <U extends Identifiable> U getNodeAsObjectFromStore(
            String path, Element current_node, DataStore store, Function<String, U> method) 
            throws XPathExpressionException
    {
        String identifier = getNodeAsString(path, current_node);
        
        return method.apply(identifier);
    }

    public <U> List<U> getNodesAsSimpleList(
            String path, Element current_node, String keyId, Function<String, U> loader)
            throws XPathExpressionException
    {
        List<U> retValue = new ArrayList<>();

        NodeList nodes = (NodeList) xpath.evaluate(".//" + path, current_node, XPathConstants.NODESET);
    
        for (int i = 0; i < nodes.getLength(); i++)
        {
            assert(nodes.item(i).getNodeType() == Node.ELEMENT_NODE);
            Element e = (Element) nodes.item(i);
            String attr = e.getAttribute(keyId);
            assert(attr != null);
            U u = loader.apply(attr);
            retValue.add(u);
        }
        
        return retValue;
    }
    
    public <U, V> Map<U, V> getNodesAsSimpleMap(
            String path, Element current_node, 
            String keyId, Function<String, U> keyLoader, 
            String valueId, Function<String, V> valueLoader)
            throws XPathExpressionException
    {
        Map<U, V> retValue = new HashMap<>();

        NodeList nodes = (NodeList) xpath.evaluate(".//" + path, current_node, XPathConstants.NODESET);
    
        for (int i = 0; i < nodes.getLength(); i++)
        {
            assert(nodes.item(i).getNodeType() == Node.ELEMENT_NODE);
            Element e = (Element) nodes.item(i);
            String keyString = e.getAttribute(keyId);
            String valueString = e.getAttribute(valueId);
            assert(keyString != null && valueString != null);
            U u = keyLoader.apply(keyString);
            V v = valueLoader.apply(valueString);
            retValue.put(u, v);
        }
        
        return retValue;        
    }
    
    public <U> List<U> getNodesAsObjectList(String path, Element current_node, XMLLoader<U> loader, DataStore store) 
            throws XPathExpressionException, IOException
    {
        List<U> retValue = new ArrayList<>();
        
        NodeList nodes = (NodeList) xpath.evaluate(".//" + path, current_node, XPathConstants.NODESET);
    
        for (int i = 0; i < nodes.getLength(); i++)
        {
            assert(nodes.item(i).getNodeType() == Node.ELEMENT_NODE);
            Element e = (Element) nodes.item(i);
            U u = loader.loadXML(e, store);
            retValue.add(u);
        }
        
        return retValue;
    }
    
    public <V, U extends LevelDependant<V>> 
    LevelDependantList<V> getNodesAsObjectLDList(
            String path, Element current_node, XMLLoader<U> loader, DataStore store)
            throws XPathExpressionException, IOException
    {
        LevelDependantList<V> retValue = new LevelDependantList<>();
        
        NodeList nodes = (NodeList) xpath.evaluate(".//" + path, current_node, XPathConstants.NODESET);
        
        for (int i = 0; i < nodes.getLength(); i++)
        {
            assert(nodes.item(i).getNodeType() == Node.ELEMENT_NODE);
            Element e = (Element) nodes.item(i);
            U u = loader.loadXML(e, store);
            retValue.add(u, getLevelCondition(e), getLevelConversion(e));
        }
        
        return retValue;
    }
    
    public <U extends Identifiable> 
    List<U> getNodesAsObjectListFromStore(
            String path, Element current_node, DataStore store, Function<String, U> method) 
            throws XPathExpressionException, IOException
    {
        return getNodesAsObjectList(path, current_node, store.getLoader(method), store);
    }
    
    public <V, U extends LevelDependant<V>> 
    LevelDependantList<V> getNodesAsObjectLDListFromStore(
            String path, Element current_node, DataStore store, Function<String, U> method)
            throws XPathExpressionException, IOException
    {
        return getNodesAsObjectLDList(path, current_node, store.getLoader(method), store);
    }
            
    protected <U extends Identifiable> List<U> getChildsAsObjectListFromStore(
            Element current_node, DataStore store, Function<String, U> method)
    {
        List<U> retValue = new ArrayList<>();
        
        NodeList nodes = current_node.getChildNodes();
        
        for (int i = 0; i < nodes.getLength(); i++)
        {
            if (nodes.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            
            Element e = (Element) nodes.item(i);
            U u = method.apply(e.getTagName());
            retValue.add(u);
        }
        
        return retValue;
    }
    
    protected <V, U extends LevelDependant<V>> 
    LevelDependantList<V> getChildsAsObjectLDListFromStore(
            Element current_node, DataStore store, Function<String, U> method)
            throws XPathExpressionException
    {
        LevelDependantList<V> retValue = new LevelDependantList<>();
        
        NodeList nodes = current_node.getChildNodes();
        
        for (int i = 0; i < nodes.getLength(); i++)
        {
            if (nodes.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            
            Element e = (Element) nodes.item(i);
            U u = method.apply(e.getTagName());            
            retValue.add(u, getLevelCondition(e), getLevelConversion(e));
        }
        
        return retValue;
    }
    
    protected ImageIcon loadNodeAsIcon(String path, Element current_node) 
            throws IOException, XPathExpressionException
    {
        String filepath = getNodeAsPath("portrait", current_node);
        File file = new File(filepath);
        BufferedImage img = ImageIO.read(file);
        
        return new ImageIcon(img);
    }
}
