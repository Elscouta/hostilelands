/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.TerrainType;
import hostilelands.tools.Grid3x3;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.function.Function;

/**
 *
 * @author Elscouta
 */
public class CreationLandmass extends CreationArea
{
    /**
     * Defines a new forest to fit in the given size.
     * @param size 
     * @param origX
     * @param origY
     * @param neighbors
     */
    public CreationLandmass(int size, int origX, int origY,
                            PartialSquare previousLayers,
                            Grid3x3<SummarySource> neighbors)
    {
        super(size, origX, origY, previousLayers, neighbors,
              new CreationLandmass.SpawnInterpreter(size), 
              new DottedSquare(size));
    }
    
    private CreationLandmass(int size, int origX, int origY,
                             PartialSquare previousLayers,
                             Grid3x3<SummarySource> neighbors,
                             DottedInterpreter interpreter, 
                             DottedSquare edgeConstraints)
    {
        super(size, origX, origY, previousLayers, neighbors, 
              interpreter, edgeConstraints);
    }
    
    public static Factory<CreationLandmass> getFactory()
    {
        return (size, origX, origY, previousLayers, neighbors) -> 
                new CreationLandmass(size, origX, origY, previousLayers, neighbors);
    }
    
    @Override
    protected CreationLandmass createChild(int size, int origX, int origY,
                                           PartialSquare previousLayers,
                                           Grid3x3<SummarySource> neighbors,
                                           int quarter, DottedSquare childConstraints)
    {
        return new CreationLandmass(size, origX, origY, previousLayers, neighbors, 
                                    interpreter.clip(quarter), childConstraints);
    }
    
    @Override
    public PartialTileData getTileData()
    {
        assert(size == 0);
        
        PartialTileData data = new PartialTileData();
        data.setTerrain(TerrainType.GRASS, true);
        return data;
    }
    
    @Override
    public PartialSquareSummary getSummary()
    {
        PartialSquareSummary summary = new PartialSquareSummary(size);
        
        if (size > 0)
            summary.addTerrainGuess(TerrainType.GRASS, 
                    interpreter.getExpectedRatio(edgeConstraints.getRatio()), 
                    true);
        else
            summary.addTerrainGuess(TerrainType.GRASS, 1d, true);
        
        return summary;
    }
    
    /**
     * Interpreter for most squares.
     * 
     * @author Elscouta
     */
    private static class NormalInterpreter extends AbstractDottedInterpreter 
    {
        public NormalInterpreter(int size) 
        {
            super(size, 0.45f, 4.0f, 4.0f, 3.0f);
        }

        @Override
        public DottedInterpreter clip(int quarter) 
        {
            return new NormalInterpreter(size / 2);
        }
    }

    /**
     * Interpreter for the original spawn
     * 
     * @author Elscouta
     */
    private static class SpawnInterpreter extends AbstractDottedInterpreter 
    {
        public SpawnInterpreter(int size) 
        {
            super(size, 0.65f, 3.0f, 4.0f, 3.0f);
        }

        @Override
        public Function<Integer, Iterator<Point2D>> onMissingEdge(int edgeCount) 
        {
            return (Integer edge) -> {
                return centeredDiamondSupplier(edge, size, 0.55f);
            };
        }

        @Override
        public DottedInterpreter clip(int quarter) 
        {
            return new NormalInterpreter(size / 2);
        }

        @Override
        public boolean deleteOnEmptyEdges() 
        {
            return false;
        }
        
        @Override
        public double getExpectedRatio(double ratio)
        {
            return (1d + ratio) / 2d;
        }
    }
}
