/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import java.util.function.Function;

/**
 *
 * @author Elscouta
 */
public class TerrainType implements Identifiable
{
    private final String identifier;
    
    private TerrainType(String identifier)
    {
        this.identifier = identifier;
    }
    
    @Override
    public String getIdentifier()
    {
        return identifier;
    }
    
    @Override
    public String toString()
    {
        return identifier;
    }
    
    @Override
    public boolean equals(Object other)
    {
        if (other instanceof TerrainType == false)
            return false;
        
        TerrainType o = (TerrainType) other;
        return this.identifier.equals(o.identifier);
    }
    
    @Override
    public int hashCode()
    {
        return identifier.hashCode();
    }
    
    @FunctionalInterface
    public interface Transform extends Function<TerrainType, TerrainType>
    {
        @Override public TerrainType apply(TerrainType orig);
    }
    
    public static final TerrainType NULL = new TerrainType("null");
    public static final TerrainType SEA = new TerrainType("sea");
    public static final TerrainType GRASS = new TerrainType("grass");
    public static final TerrainType FOREST = new TerrainType("forest");
    public static final TerrainType MOUNTAIN = new TerrainType("mountain");
    public static final TerrainType ROAD = new TerrainType("road");
}
