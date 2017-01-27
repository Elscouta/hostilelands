/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.desc.XMLLoader;
import hostilelands.desc.XMLBasicLoader;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;
import org.xml.sax.*;

class TileDesc extends ImageStore.DrawableDesc
{
    public static class Loader extends XMLLoader<TileDesc>
    {
        @Override 
        public TileDesc loadXML(Element e, DataStore store) 
                throws XPathExpressionException, IOException
        {
            TileDesc desc = new TileDesc();

            desc.loadDrawableDesc(e, this);
                    
            return desc;
        }
    }    
}

class MapIconDesc extends ImageStore.DrawableDesc
{
    int width;
    int height;
    int offset_x;
    int offset_y;
    
    public static class Loader extends XMLLoader<MapIconDesc>
    {
        @Override
        public MapIconDesc loadXML(Element e, DataStore store)
                throws XPathExpressionException, IOException
        {
            MapIconDesc desc = new MapIconDesc();
            
            desc.loadDrawableDesc(e, this);            
            desc.width = getNodeAsInt("ancestor-or-self::*/position/w", e);
            desc.height = getNodeAsInt("ancestor-or-self::*/position/h", e);
            desc.offset_x = getNodeAsInt("ancestor-or-self::*/offset/x", e);
            desc.offset_y = getNodeAsInt("ancestor-or-self::*/offset/y", e);
                        
            return desc;
        }
    }

}

class ImageStore<T extends ImageStore.DrawableDesc> extends XMLStore<T>
{
    private Map<String, BufferedImage> imgstore;
    
    public <L extends XMLLoader<T>>
    ImageStore(String imagepath, String description_path, L loader) throws IOException
    {
        super(loader);
        imgstore = new HashMap<>();

        load(description_path, "item", null);        
    }
    
    private void loadImage(String path)
            throws IOException
    {
        if (imgstore.containsKey(path))
            return;
        else
            imgstore.put(path, ImageIO.read(new File(path)));
    }
    
    public BufferedImage getImage(DrawableDesc d)
    {
        try {
            loadImage(Settings.ROOT_PATH + d.path);
        } catch (IOException e) {
            System.err.printf("Unable to read file: %s\n", d.path);
            return null;
        }
        return imgstore.get(Settings.ROOT_PATH + d.path);
    }
            
    public static class DrawableDesc implements Identifiable
    {
        private String identifier;
        private String path;
        
        public int x;
        public int y;
        
        public void loadDrawableDesc(Element e, XMLBasicLoader l)
                throws XPathExpressionException, IOException
        {
            identifier = l.getAttributeAsString("identifier", e);
            x = l.getNodeAsInt("position/x", e);
            y = l.getNodeAsInt("position/y", e);
            
            path = l.getNodeAsString("ancestor-or-self::*/path", e);
        }
            
        @Override
        public String toString()
        {
            return String.format("(%d, %d)", x, y);
        }

        @Override
        public String getIdentifier()
        {
            return identifier;
        }
    }
}

/**
 * A class able to draw various MapDrawable (usually MapObjects).
 * This class is not thread-safe. Objects drawn must have been previously 
 * locked.
 *
 * @author Elscouta
 */
public class MapPainter 
{
    ImageStore<TileDesc> tiles;
    ImageStore<MapIconDesc> mapicons;
    
    public MapPainter() throws IOException
    {
        tiles = new ImageStore<>(Settings.TILESET_PATH, Settings.TILESET_DESC_PATH, new TileDesc.Loader());
        mapicons = new ImageStore<>(Settings.MAPDRAWABLE_PATH, Settings.MAPDRAWABLE_DESC_PATH, new MapIconDesc.Loader());
    }
    
    public void drawTile(BufferedImage img, int mapX, int mapY, TerrainType t)
    {
        Graphics2D g2d = img.createGraphics();
        
        TileDesc desc = tiles.get(t.getIdentifier());
        int x = (mapX * Settings.TILE_WIDTH);
        int y = (mapY * Settings.TILE_HEIGHT);
        
        g2d.drawImage(tiles.getImage(desc), x, y, x + Settings.TILE_WIDTH, y + Settings.TILE_HEIGHT,
                      desc.x, desc.y, desc.x + Settings.TILE_WIDTH, desc.y + Settings.TILE_HEIGHT,
                      null);
        g2d.dispose();
    }
    
    private void drawMapElement(double mapX, double mapY, String id, Graphics g2d)
    {
        MapIconDesc desc = mapicons.get(id);
        int x = (int) (mapX * Settings.TILE_WIDTH) + desc.offset_x;
        int y = (int) (mapY * Settings.TILE_HEIGHT) + desc.offset_y;
        
        g2d.drawImage(mapicons.getImage(desc), x, y, x + desc.width, y + desc.height,
                                             desc.x, desc.y, desc.x + desc.width, desc.y + desc.height,
                                             null);               
    }
    
    /**
     * Draws a map element to a graphics interface
     * @param d the map element to draw
     * @param g the target graphics
     * @param mapOrigX the X map coordinate associated to the top left corner of the graphics
     * @param mapOrigY the Y map coordinate associated to the top left corner of the graphics
     */
    public void drawMapElement(MapDrawable d, Graphics g, double mapOrigX, double mapOrigY)
    {
        drawMapElement(d.getX() - mapOrigX, d.getY() - mapOrigY, d.getIconIdentifier(), g);
    }
    
}
