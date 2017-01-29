/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.MapLocation;
import hostilelands.Settings;
import hostilelands.TerrainType;
import hostilelands.tools.Distribution;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elscouta
 */
public class PartialSquareSummary 
{
    private Distribution<TerrainType.Transform> terrains;
    private final List<MapLocation> locations;
    private final int size;
    
    public PartialSquareSummary(int size)
    {
        terrains = new Distribution<>(t -> t);
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
    
    public Distribution<TerrainType> getTerrains()
    {
        return terrains.map(f -> f.apply(TerrainType.NULL));
    }
    
    public void overwrite(PartialSquareSummary other)
    {
        terrains = terrains.compose(other.terrains.map(f -> (g -> (x -> f.apply(g.apply(x))))));
        locations.addAll(other.locations);
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
        assert (ratio > - Settings.FLOAT_PRECISION && 
                ratio <= 1 + Settings.FLOAT_PRECISION);
        PartialSquareSummary other = new PartialSquareSummary(size);
        if (forceOverwrite)
        {
            other.terrains = Distribution.binaryChoice(ratio, 
                    t -> type,
                    t -> t 
            ); 
        }
        else
        {
            other.terrains = Distribution.binaryChoice(ratio,
                    t -> t != TerrainType.SEA ? type : TerrainType.SEA,
                    t -> t
            );
        }

        overwrite(other);
    }
    
    /**
     * Returns the terrain score of this square, which is the scalar
     * product of the terrain guess and the terrain score parameter
     * @param scores A mapping from the terrain types to their scores.
     * @return the terrain score of the square.
     */
    public double getTerrainScore(Map<TerrainType, Double> scores)
    {
        Distribution<TerrainType> flattened = getTerrains();
        
        return scores.entrySet()
                     .stream()
                     .mapToDouble(e -> e.getValue() * flattened.get(e.getKey()))
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
        Distribution<TerrainType> flattened = getTerrains();

        return flattened.entrySet()
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
     * 
     * @param trait
     * @return 
     */
    public boolean isLocationWithTraitPresent(String trait)
    {
        return locations.stream()
                        .map(x -> x.hasTrait(trait))
                        .reduce((x, y) -> x || y)
                        .orElse(false);
    }
}
