/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.desc;

import hostilelands.DataStore;
import hostilelands.Identifiable;
import hostilelands.XMLStore;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
 * An interface for factories able to construct objects from xml files. This 
 * class includes various useful functions to help implementing classes.
 * 
 * @param T the class type that is loaded
 * @author Elscouta
 */
public abstract class XMLLoader<T> extends XMLBasicLoader
{
    public abstract T loadXML(Element e, DataStore store) throws XPathExpressionException, IOException;
}
