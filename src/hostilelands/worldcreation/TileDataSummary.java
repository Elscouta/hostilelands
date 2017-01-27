/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.MapLocation;
import hostilelands.Settings;
import hostilelands.TerrainType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elscouta
 */
public class TileDataSummary 
{
    private final Map<TerrainType, Double> terrains;
    private final List<MapLocation> locations;
    private final int size;
    
    public TileDataSummary(int size)
    {
        terrains = new HashMap<>();
        terrains.put(TerrainType.SEA, 1d);
        locations = new ArrayList<>();
        this.size = size;
    }
    
    /**
     * Returns the size of the square associated to the summary
     * @return the size
     */
    public int getSize()
    {
        assert(size > 0);
        return size;
    }
    
    /**
     * Adds an expected ratio of terrain to the current summary.
     * @param type The type of the terrain added.
     * @param ratio The ratio of this terrain that is expected to be added to
     * the square.
     * @param forceOverwrite Whether this terrain will be added in force mode,
     * or if it will respect terrain priority.
     */
    public void addTerrainGuess(TerrainType type, double ratio, boolean forceOverwrite)
    {
        double addratio = 0;
        for (Map.Entry<TerrainType, Double> e : terrains.entrySet())
        {
            if (e.getKey().equals(type))
                continue;
            
            if (!forceOverwrite && !type.canOverwrite(e.getKey()))
                continue;
            
            double value = e.getValue();
            terrains.put(e.getKey(), value * (1 - ratio));
            addratio += ratio * value;
        }

        if (terrains.containsKey(type))
        {
            double oldratio = terrains.get(type);
            terrains.put(type, oldratio + addratio);
        }
        else
        {
            terrains.put(type, addratio);
        }        
    }
    
    /**
     * Returns the terrain score of this square, which is the scalar
     * product of the terrain guess and the terrain score parameter
     * @param scores A mapping from the terrain types to their scores.
     * @return the terrain score of the square.
     */
    public double getTerrainScore(Map<TerrainType, Double> scores)
    {
        return scores.entrySet()
                     .stream()
                     .mapToDouble(e -> e.getValue() * terrains.getOrDefault(e.getKey(), 0d))
                     .reduce((x, y) -> x + y)
                     .orElse(0);
    }
    
    /**
     * Returns whether a terrain is at least a bit present in the square
     * @param terrain the terrain that is asked to be present.
     * @return true if there is at least some hope of that terrain.
     */
    public boolean isTerrainPresent(TerrainType terrain)
    {
        return terrains.entrySet()
                       .stream()
                       .map(e -> e.getKey().equals(terrain) && e.getValue() > Settings.FLOAT_PRECISION)
                       .reduce(false, (x, y) -> x || y);
    }
    
    /**
     * Adds a location to the summary
     * @param location The location to be added
     */
    public void addLocation(MapLocation location)
    {
        locations.add(location);
    }
    
    /**
     * Returns whether a location with the given key is present
     */
    public boolean isLocationWithTraitPresent(String trait)
    {
        return locations.stream()
                        .map(x -> x.hasTrait(trait))
                        .reduce((x, y) -> x || y)
                        .orElse(false);
    }
}
