/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.views;

import java.util.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.event.*;

import hostilelands.MapPainter;
import hostilelands.MapDrawable;
import hostilelands.MapObject;
import hostilelands.MapEntity;
import hostilelands.MainParty;
import hostilelands.World;
import hostilelands.WorldSquare;
import hostilelands.Settings;

import hostilelands.models.ChangeEventAggregate;
import java.awt.Dimension;

/**
 * A subset of the whole map picture.
 * This class is not thread-safe. Any object drawn must have been previously 
 * locked.
 * 
 * @author Elscouta
 */
class WorldSquarePicture extends ChangeEventAggregate
{
    private final BufferedImage cleanimg;
    private BufferedImage decoratedimg;
    private final WorldSquare ws;
    private final MapPainter map_painter;
    
    private final int squareOrigX;
    private final int squareOrigY;
    
    private final int width;
    private final int height;
    
    public WorldSquarePicture(WorldSquare ws, MapPainter mp, int squareOrigX, int squareOrigY)
    {
        this.ws = ws;
        assert(ws.getWidth() == Settings.MAPSQUARE_SIZE);
        assert(ws.getHeight() == Settings.MAPSQUARE_SIZE);
        this.width = ws.getWidth() * Settings.TILE_WIDTH;
        this.height = ws.getHeight() * Settings.TILE_HEIGHT;
        this.squareOrigX = squareOrigX;
        this.squareOrigY = squareOrigY;
        this.map_painter = mp;
        
        this.cleanimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        for (int x = 0; x < ws.getWidth(); x++)
            for (int y = 0; y < ws.getHeight(); y++)
                this.map_painter.drawTile(this.cleanimg, x, y, ws.getTile(x, y));
        
        resetCache();
        
        ws.addChangeListener(listener);
    }
    
    /**
     * Frees ressources allocated to the picture and unregister listeners.
     */
    public final void dispose()
    {
        ws.removeChangeListener(listener);
    }
    
    public final void resetCache()
    {
        this.decoratedimg = new BufferedImage(Settings.TILE_WIDTH * ws.getWidth(),
                                              Settings.TILE_HEIGHT * ws.getHeight(),
                                              BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = decoratedimg.createGraphics();
        g2d.drawImage(cleanimg, 0, 0, null);
        g2d.dispose();
    }
    
    public final void drawObject(MapDrawable d, Graphics g, double origMapX, double origMapY)
    {
        map_painter.drawMapElement(d, g, origMapX, origMapY);
    }
        
    public final void drawMapEntities(Graphics g, double origMapX, double origMapY)
    {
        List<MapEntity> hazards = ws.getMapEntities();
        for (int i = 0; i < hazards.size(); i++)
            drawObject(hazards.get(i), g, origMapX, origMapY);            
    }
            
    public final void drawBackgroundMap(Graphics g, double origMapX, double origMapY)
    {
        final int mapPixelX = squareOrigX * Settings.MAPSQUARE_SIZE * Settings.TILE_WIDTH - Settings.TILE_WIDTH / 2;
        final int mapPixelY = squareOrigY * Settings.MAPSQUARE_SIZE * Settings.TILE_HEIGHT - Settings.TILE_HEIGHT / 2;
        final int mapOffsetX = (int) (origMapX * Settings.TILE_WIDTH);
        final int mapOffsetY = (int) (origMapY * Settings.TILE_HEIGHT);

        g.drawImage(cleanimg, 
                    mapPixelX - mapOffsetX, mapPixelY - mapOffsetY,
                    mapPixelX - mapOffsetX + width, mapPixelY - mapOffsetY + height,
                    0, 0, width, height,
                    null);
    }
    
    /**
     * @return The X map coordinate corresponding to the topleft corner of the square.
     */
    public final int getSquareOrigX()
    {
        return squareOrigX;
    }
    
    /**
     * @return The Y map coordinate corresponding to the topleft corner of the square.
     */
    public final int getSquareOrigY()
    {
        return squareOrigY;
    }
}

/**
 *
 * @author Elscouta
 */
public class MapView extends JPanel 
{
    private final World world;
    private final List<WorldSquarePicture> map_squares;
    private final MapPainter map_painter;
    private final MainParty party;
    private final ObjectsChangeListener listener;

    private double origMapX;
    private double origMapY;
    
    private double viewWidth;
    private double viewHeight;

   
    private MapView(World w, MainParty p) throws java.io.IOException
    {
        this.world = w;
        this.map_painter = new MapPainter();
        this.map_squares = new ArrayList<>();
        this.party = p;
        this.listener = new ObjectsChangeListener();
        
        this.origMapX = 5f;
        this.origMapY = 5f;
    }
    
    private void setupListeners()
    {
        MouseManager mgr = new MouseManager();
        addMouseListener(mgr);
        addMouseMotionListener(mgr);
        
        party.addPositionListener(listener);
        party.getDestination().addPositionListener(listener);
    }
    
    private void offloadMapSquare(WorldSquarePicture wsp)
    {
        wsp.dispose();
    }
       
    private WorldSquarePicture loadMapSquare(int x, int y)
    {
        WorldSquare ws = world.getSquare(x, y);
        WorldSquarePicture wsp = new WorldSquarePicture(ws, map_painter, x, y);
        wsp.addChangeListener(listener);
        
        return wsp;
    }
    
    private void requestLoadMapSquare(int x, int y)
    {
        for (WorldSquarePicture wsp : map_squares)
        {
            if (wsp.getSquareOrigX() == x && wsp.getSquareOrigY() == y)
                return;
        }
        
        map_squares.add(loadMapSquare(x, y));
    }

    private void updateLoadedSquares()
    {
        int minSquareX = (int) origMapX / Settings.MAPSQUARE_SIZE;
        int maxSquareX = (int) (origMapX + viewWidth) / Settings.MAPSQUARE_SIZE;
        int minSquareY = (int) origMapY / Settings.MAPSQUARE_SIZE;
        int maxSquareY = (int) (origMapY + viewHeight) / Settings.MAPSQUARE_SIZE;

        
        for (int x = minSquareX; x <= maxSquareX; x++)
            for (int y = minSquareY; y <= maxSquareY; y++)
                requestLoadMapSquare(x, y);
    }
    
    public static MapView create(World w, MainParty p) throws java.io.IOException
    {
        MapView v = new MapView(w, p);
        v.setupListeners();
        v.updateLoadedSquares();
        return v;
    }
    
    @Override
    public void setSize(Dimension d)
    {
        super.setSize(d);
        
        viewWidth = ((double) d.width) / Settings.TILE_WIDTH;
        viewHeight = ((double) d.height) / Settings.TILE_HEIGHT;
        
        updateLoadedSquares();
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
              
        for (WorldSquarePicture wsp : map_squares)
            wsp.drawBackgroundMap(g, origMapX, origMapY);
        
        for (WorldSquarePicture wsp : map_squares)
            wsp.drawMapEntities(g, origMapX, origMapY);
        
        synchronized(party)
        {
            map_painter.drawMapElement(party, g, origMapX, origMapY);
        }
            
        MapObject destination = party.getDestination();
        synchronized(destination)
        {
            if (destination.exists())
                map_painter.drawMapElement(destination, g, origMapX, origMapY);
        }        
    }
    
    public double toMapX(int x)
    {
        return ((double) x / Settings.TILE_WIDTH) + origMapX;
    }
    
    public double toMapY(int y)
    {
        return ((double) y / Settings.TILE_HEIGHT) + origMapY;
    }
    
    class MouseManager extends MouseAdapter
    {
        double frozenMapX;
        double frozenMapY;
        
        @Override
        public void mousePressed(MouseEvent e)
        {
            if ((e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK)
                party.getDestination().setPosition(toMapX(e.getX()), toMapY(e.getY()));

            if ((e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) == InputEvent.BUTTON1_DOWN_MASK)
            {
                frozenMapX = toMapX(e.getX());
                frozenMapY = toMapY(e.getY());
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e)
        {
            if ((e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) == 0)
                party.getDestination().suppress();
        }
        
        @Override
        public void mouseExited(MouseEvent e)
        {
            party.getDestination().suppress();
        }
        
        @Override
        public void mouseMoved(MouseEvent e)
        {
            if ((e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) == InputEvent.BUTTON1_DOWN_MASK)
            {
                origMapX -= toMapX(e.getX()) - frozenMapX;
                origMapY -= toMapY(e.getY()) - frozenMapY;
                updateLoadedSquares();
                repaint();
            }            
        }
        
        @Override
        public void mouseDragged(MouseEvent e)
        {
            if ((e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK)
                party.getDestination().setPosition(toMapX(e.getX()), toMapY(e.getY()));

            if ((e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) == InputEvent.BUTTON1_DOWN_MASK)
            {
                origMapX -= toMapX(e.getX()) - frozenMapX;
                origMapY -= toMapY(e.getY()) - frozenMapY;
                updateLoadedSquares();
                repaint();
            }
        }
    }    
    
    class ObjectsChangeListener implements ChangeListener
    {
        @Override
        public void stateChanged(ChangeEvent e)
        {
            repaint();
        }
    }
}
