/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.worldcreation.CreationForests;
import hostilelands.worldcreation.CreationLandmass;
import hostilelands.worldcreation.LocationPlacement;
import hostilelands.worldcreation.CreationTileDesc;


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
        hostilelands.worldcreation.PartialSquareRoot generator =
                new hostilelands.worldcreation.PartialSquareRoot(size*Settings.MAPSQUARE_SIZE);
        generator.addObject(CreationLandmass.getFactory());
        generator.addObject(CreationForests.getFactory(2*Settings.MAPSQUARE_SIZE, 30));

        CreationTileDesc hometownDesc = game.getDataStore().getTownDesc("hometown").getCreationDesc();
        generator.addObject(LocationPlacement.getFactory(hometownDesc, null));
        
        CreationTileDesc creationTomb = game.getDataStore().getLairDesc("tomb").getCreationDesc();
        generator.addObject(LocationPlacement.getFactory(creationTomb, null));
        generator.addObject(LocationPlacement.getFactory(creationTomb, null));
        generator.addObject(LocationPlacement.getFactory(creationTomb, null));
        generator.addObject(LocationPlacement.getFactory(creationTomb, null));
        generator.addObject(LocationPlacement.getFactory(creationTomb, null));
        generator.addObject(LocationPlacement.getFactory(creationTomb, null));
        generator.addObject(LocationPlacement.getFactory(creationTomb, null));

        generator.generate();
                
        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
            {
                map[i][j] = new WorldSquare(game, shortTimeMgr, i*Settings.MAPSQUARE_SIZE, j*Settings.MAPSQUARE_SIZE, generator);    
                shortTimeMgr.addEntity(map[i][j]);
            } 
        }
        
        generator.postProcess();
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
