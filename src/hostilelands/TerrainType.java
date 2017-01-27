/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

/**
 *
 * @author Elscouta
 */
public class TerrainType implements Identifiable
{
    private final String identifier;
    private final int priority;
    
    private TerrainType(String identifier, int priority)
    {
        this.identifier = identifier;
        this.priority = priority;
    }
    
    public boolean canOverwrite(TerrainType other)
    {
        return this.priority < other.priority;
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
    
    public static final TerrainType SEA = new TerrainType("sea", 0);
    public static final TerrainType GRASS = new TerrainType("grass", 200);
    public static final TerrainType FOREST = new TerrainType("forest", 100);
    public static final TerrainType MOUNTAIN = new TerrainType("mountain", 50);
    public static final TerrainType ROAD = new TerrainType("road", 10);
}
