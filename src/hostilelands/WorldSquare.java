/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import java.util.ArrayList;
import java.util.List;

import hostilelands.models.ChangeEventAggregate;
import hostilelands.worldcreation.PartialSquareRoot;
import hostilelands.worldcreation.PartialTileData;

/**
 * Represents the accurate simulation of a world square.
 * 
 * It takes cares of the simulation of entity collision, but registers every
 * non-party entity to the shortTimeMgr for their own simulations.
 *
 * The party is not managed by this class, except for collision purposes.
 * 
 * @author Elscouta
 */
public class WorldSquare extends ChangeEventAggregate implements ShortTimeEntity
{
    final TerrainType[][] tiles;
    final Game game;
    final List<MapEntity> entities;
    final List<MapEntity> colliding_entities;
    final ShortTimeMgr shortTimeMgr;
    final int mapOrigX;
    final int mapOrigY;
    
    MainParty party;
    
    public WorldSquare(Game game, ShortTimeMgr shortTimeMgr, int mapOrigX, int mapOrigY, 
                       PartialSquareRoot generator)
    {
        this.tiles = new TerrainType[Settings.MAPSQUARE_SIZE][Settings.MAPSQUARE_SIZE];
        this.shortTimeMgr = shortTimeMgr;
        this.game = game;
        this.party = null;
        this.mapOrigX = mapOrigX;
        this.mapOrigY = mapOrigY;
        
        this.entities = new ArrayList<>();
        this.colliding_entities = new ArrayList<>();        
        
        for (int x = 0; x < Settings.MAPSQUARE_SIZE; x++)
        {
            for (int y = 0; y < Settings.MAPSQUARE_SIZE; y++)
            {
                PartialTileData data = generator.getTileData(mapOrigX + x, mapOrigY + y);
                tiles[x][y] = data.getTerrainType();
                if (data.getLocation() != null)
                    addEntity(data.getLocation(), x, y);
            }
        }               
    }
    
    public int getWidth()
    {
        return Settings.MAPSQUARE_SIZE;
    }
    
    public int getHeight()
    {
        return Settings.MAPSQUARE_SIZE;
    }
    
    public int toWorldX(int squareX)
    {
        return mapOrigX + squareX;
    }
    
    public int toWorldY(int squareY)
    {
        return mapOrigY + squareY;
    }
    
    public TerrainType getTile(int x, int y)
    {
        return tiles[x][y];
    }
    
    public List<MapEntity> getMapEntities()
    {
        return entities;
    }

    public final void addEntity(MapEntity e, double x, double y)
    {
        entities.add(e);
        e.init(shortTimeMgr, game.getWorld(), mapOrigX + x, mapOrigY + y);
        e.getPosition().addChangeListener(listener);
    }
    
    public final void removeEntity(MapEntity e)
    {
        assert(entities.contains(e));
        
        if (colliding_entities.contains(e))
        {
            colliding_entities.remove(e);
            game.fireEvent(e.onPartyLeave(party));
        }

        e.getPosition().removeChangeListener(listener);
        e.suppress();
        entities.remove(e);        
    }
    
    public final void partyEnters(MainParty p)
    {
        party = p;
        p.getPosition().addChangeListener(listener);
    }
    
    public final void partyLeaves(MainParty p)
    {
        party = null;
        p.getPosition().addChangeListener(listener);
    }
    
    private void testPartyCollision(MapEntity e)
    {
        if (colliding_entities.contains(e))
        {
            if (e.getPosition().distanceTo(party.getPosition()) > 2 * Settings.COLLISION_DISTANCE)
            {
                colliding_entities.remove(e);
                game.fireEvent(e.onPartyLeave(party));
            }
        }
        else
        {
            if (e.getPosition().distanceTo(party.getPosition()) < Settings.COLLISION_DISTANCE)
            {
                colliding_entities.add(e);
                game.fireEvent(e.onPartyEncounter(party));
            }
        }
    }
    
    @Override 
    public void simulateTime(int minutes)
    {
        if (party == null)
            return;
        
        for (int i = 0; i < entities.size(); i++)
            testPartyCollision(entities.get(i));
    }
}
