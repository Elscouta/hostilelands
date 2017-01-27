/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.World;
import hostilelands.tools.CardinalMap;
import hostilelands.tools.Grid2x2;
import hostilelands.tools.Grid3x3;

/**
 *
 * @author Elscouta
 */
public interface CreationObject 
{
    public Grid2x2<CreationObject> split(Grid2x2<IWorldSquare> childs,
                                         Grid3x3<TileDataSummary> neighbors)
            throws World.GenerationFailure;
    public TileData fillTileData(TileData data);
    public TileDataSummary fillTileDataSummary(TileDataSummary data);
    public boolean isAlive();
}
