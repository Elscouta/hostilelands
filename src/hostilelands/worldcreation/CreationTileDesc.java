/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.DataStore;
import hostilelands.MapLocation;
import hostilelands.TerrainType;
import hostilelands.desc.XMLBasicLoader;
import hostilelands.tools.CardinalMap;
import hostilelands.tools.Grid3x3;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;

/**
 *
 * @author Elscouta
 */
public class CreationTileDesc 
{
    private final Supplier<MapLocation> location;
    private boolean strictCreation;
    private Map<TerrainType, Double> preferedTerrains;
    private List<TerrainType> allowedTerrains;
    private final String repulsionCategory;
    private int repulsionDistance;
    
    public CreationTileDesc(Supplier<MapLocation> location, String category)
    {
        this.location = location;
        this.repulsionCategory = category;
    }
    
    public void loadXML(Element e, DataStore store)
            throws XPathExpressionException
    {
        XMLBasicLoader l = new XMLBasicLoader();
        
        strictCreation = l.getNodeAsBoolean("strict", e);
        preferedTerrains = l.getNodesAsSimpleMap("terrain[@value]", e,
                                                 "id", s -> store.getTerrainType(s), 
                                                 "value", s -> Double.parseDouble(s));
        allowedTerrains = l.getNodesAsSimpleList("terrain", e,
                                                 "id", s -> store.getTerrainType(s));
        repulsionDistance = l.getNodeAsInt("repulsion", e);
    }
    
    public boolean hasStrictCreation()
    {
        return strictCreation;
    }
    
    /**
     * Temporary returns the icon identifier of the location.
     * Could be improved...
     * 
     * @return An identifier for the description
     */
    public String getIdentifier()
    {
        return location.get().getIconIdentifier();
    }
    
    /**
     * Returns the score of the summary, depending on the terrain preferences
     * and distance to similar entities. Score is:
     * + 100 base
     * + preferedTerrains[terrain] for each percentile of terrain
     * - 100 * (1/2) ^ ((distance / repulsionDistance)^2) for each similar item
     *   at distance distance.
     * 
     * If the score is negative, or if there is no allowedTerrain in the square,
     * 0 is returned and the tile should not be placed in the associated square.
     * 
     * @param summary A summary of the content of the child square.
     * @param neighbors Summaries of the square bordering that square.
     * @return The score of the square.
     */
    public double computeScore(TileDataSummary summary, 
                               Grid3x3<TileDataSummary> neighbors)
    {
        assert(summary == neighbors.o22);
        double score = 100;
        
        if (!allowedTerrains.stream()
                            .map(t -> summary.isTerrainPresent(t))
                            .reduce(false, (x, y) -> x || y))
            return 0;
        
        score += summary.getTerrainScore(preferedTerrains);
        
        Grid3x3<Double> neighborsConflicts = neighbors.map(n ->
                n.isLocationWithTraitPresent(repulsionCategory) ?
                100 * Math.pow(0.5d, Math.pow(0.25d * n.getSize() / repulsionDistance, 2)) :
                0
        );
                
        score -= neighborsConflicts.reduce(0d, (x, y) -> x+y);
        
        if (score < 0)
            return 0;
        else
            return score;
    }
    
    /**
     * Returns the location associated to this creation.
     * @return the location being created
     */
    public MapLocation getLocation()
    {
        return location.get();
    }
    
}
