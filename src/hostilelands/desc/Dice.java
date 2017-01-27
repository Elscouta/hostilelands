/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.desc;

import hostilelands.DataStore;
import hostilelands.Identifiable;
import hostilelands.Settings;
import hostilelands.desc.XMLLoader;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.*;

/**
 *
 * @author Elscouta
 */
public class Dice
{
    public static class Symbol implements Identifiable
    {        
        @Override
        public String getIdentifier()
        {
            return identifier;
        }
        
        public ImageIcon getIcon()
        {
            return icon;
        }
        
        String identifier;
        ImageIcon icon;
        
        private Symbol(String identifier) throws IOException
        {
            this.identifier = identifier;
            
            String path = String.format(Settings.SYMBOLS_ICONS_PATH, identifier);
            File file = new File(path);            
            this.icon = new ImageIcon(ImageIO.read(file));
        }
        
        public static class Loader extends XMLLoader<Symbol>
        {
            @Override
            public Symbol loadXML(Element e, DataStore store) 
                    throws IOException
            {
                String identifier = getAttributeAsString("identifier", e);
                return new Symbol(identifier);
            }
        }
    }
    
    public static class Face
    {
        public Iterable<Symbol> getSymbols()
        {
            return symbols;
        }
        
        public BufferedImage getImage()
        {
            return image;
        }
        
        List<Symbol> symbols;
        BufferedImage image;

        private static class Layout 
        {
            ArrayList<Integer> xOffsets;
            ArrayList<Integer> yOffsets;
            
            public Layout()
            {
                xOffsets = new ArrayList<>();
                yOffsets = new ArrayList<>();
            }
            
            public void addOffset(int x, int y)
            {
                xOffsets.add(new Integer(x));
                yOffsets.add(new Integer(y));
            }
            
            public void drawSymbols(BufferedImage dest, List<Symbol> symbols)
            {
                assert(symbols.size() == xOffsets.size());
                assert(symbols.size() == yOffsets.size());
                
                Graphics2D g = dest.createGraphics();
                
                for (int i = 0; i < symbols.size(); i++)
                    g.drawImage(symbols.get(i).getIcon().getImage(), 
                                xOffsets.get(i) - 8, yOffsets.get(i) - 8,
                                xOffsets.get(i) + 8, yOffsets.get(i) + 8,
                                0, 0, 16, 16, null);
            }
        }

        static ArrayList<Layout> layouts;
        
        static {
            layouts = new ArrayList<>();
            
            Layout layout0 = new Layout();
            layouts.add(layout0);
            
            Layout layout1 = new Layout();
            layout1.addOffset(16, 16);
            layouts.add(layout1);
            
            Layout layout2 = new Layout();
            layout2.addOffset(8, 8);
            layout2.addOffset(24, 24);
            layouts.add(layout2);
            
            Layout layout3 = new Layout();
            layout3.addOffset(16, 8);
            layout3.addOffset(8, 24);
            layout3.addOffset(24, 24);
            layouts.add(layout3);
            
            Layout layout4 = new Layout();
            layout4.addOffset(8, 8);
            layout4.addOffset(24, 24);
            layout4.addOffset(8, 24);
            layout4.addOffset(24, 8);
            layouts.add(layout4);
        }
        
        Face(List<Symbol> symbols)
        {
            this.symbols = symbols;
            this.image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            
            Graphics2D g2d = image.createGraphics();
            g2d.setPaint(Color.BLUE);
            g2d.fillRect(0, 0, 32, 32);
            
            assert(symbols.size() <= layouts.size() + 1);
            Layout layout = layouts.get(symbols.size());
            
            layout.drawSymbols(image, symbols);
        }        
    }
    
    private final List<Face> faces;
    private final String name;
    
    Dice(String name, List<Face> faces)
    {
        this.name = name;
        this.faces = faces;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Face roll()
    {
        int i = (int) Math.floor(Math.random() * 6);
        
        return faces.get(i);
    }
    
    public Face getFavoredFace()
    {
        return faces.get(0);
    }
}
