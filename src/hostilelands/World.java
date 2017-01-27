/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.tools.CardinalMap;
import hostilelands.tools.Grid2x2;
import hostilelands.tools.Grid3x3;
import hostilelands.worldcreation.CreationForests;
import hostilelands.worldcreation.CreationLandmass;
import hostilelands.worldcreation.CreationTile;
import hostilelands.worldcreation.CreationTileDesc;
import hostilelands.worldcreation.IWorldSquare;
import hostilelands.worldcreation.OutOfBoundsSquare;


/**
 *
 * @author Elscouta
 */
public final class World 
{
    final Game game;
    final ShortTimeMgr shortTimeMgr;
    final WorldSquare[][] map;
    final int size;
    
    public World(Game game, ShortTimeMgr shortTimeMgr, int size)
    {
        this.game = game;
        this.shortTimeMgr = shortTimeMgr;
        this.map = new WorldSquare[size][size];
        this.size = size;        
    }
    
    public void generate()
    {
        hostilelands.worldcreation.WorldSquare generator =
                new hostilelands.worldcreation.WorldSquare(size*Settings.MAPSQUARE_SIZE);
        generator.addObject(new CreationLandmass(size*Settings.MAPSQUARE_SIZE));
        generator.addObject(new CreationForests(size*Settings.MAPSQUARE_SIZE, 2*Settings.MAPSQUARE_SIZE, 30));

        CreationTileDesc hometownDesc = game.getDataStore().getTownDesc("hometown").getCreationDesc();
        CreationTile hometownCreation = new CreationTile(hometownDesc);
        generator.addObject(hometownCreation);
        
        CreationTileDesc creationTomb = game.getDataStore().getLairDesc("tomb").getCreationDesc();
        generator.addObject(new CreationTile(creationTomb));
        generator.addObject(new CreationTile(creationTomb));
        generator.addObject(new CreationTile(creationTomb));
        generator.addObject(new CreationTile(creationTomb));
        generator.addObject(new CreationTile(creationTomb));
        generator.addObject(new CreationTile(creationTomb));
        generator.addObject(new CreationTile(creationTomb));

        generator.setNeighbors(new Grid3x3(generator, () -> new OutOfBoundsSquare(size*Settings.MAPSQUARE_SIZE)));
        generator.generate();
                
        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
            {
                map[i][j] = new WorldSquare(game, shortTimeMgr, i*Settings.MAPSQUARE_SIZE, j*Settings.MAPSQUARE_SIZE, generator);    
                shortTimeMgr.addEntity(map[i][j]);
            } 
        }        
    }
            
    public WorldSquare getSquare(int x, int y)
    {
        return map[x][y];
    }
    
    public void addEntity(MapEntity e, double x, double y)
            throws OutOfWorldBoundsError
    {
        int xSquare = (int) x / Settings.MAPSQUARE_SIZE;
        int ySquare = (int) y / Settings.MAPSQUARE_SIZE;
        
        if (xSquare < 0 || xSquare >= size)
            throw new OutOfWorldBoundsError();
        if (ySquare < 0 || ySquare >= size)
            throw new OutOfWorldBoundsError();
        
        double xLocal = x - xSquare*Settings.MAPSQUARE_SIZE;
        double yLocal = y - ySquare*Settings.MAPSQUARE_SIZE;
        
        map[xSquare][ySquare].addEntity(e, xLocal, yLocal);    
    }
    
    public void partyEnters(MainParty p)
    {
        map[0][0].partyEnters(p);
    }
    
    public static class OutOfWorldBoundsError extends Exception
    {
        
    }
    
    public static class GenerationFailure extends RuntimeException
    {
        String msg;
        
        public GenerationFailure(String s)
        {
            msg = s;
        }
        
        @Override
        public String toString()
        {
            return msg;
        }
    }
}
