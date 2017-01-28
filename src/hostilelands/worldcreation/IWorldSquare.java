/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.World;
import hostilelands.WorldSquare;
import hostilelands.tools.Grid2x2;
import hostilelands.tools.Grid3x3;

/**
 *
 * @author Elscouta
 */
public interface IWorldSquare 
{
    void generate() throws World.GenerationFailure;
    void setNeighbors(Grid3x3<IWorldSquare> neighbors);
    Grid2x2<IWorldSquare> getChilds();
    TileData getTile(int x, int y);
    TileDataSummary getSummary();
    void postProcess(int cx, int cy, WorldSquare ws, int x, int y);
    void addObject(CreationObject obj);
}
