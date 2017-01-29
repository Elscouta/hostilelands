/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.TerrainType;
import hostilelands.tools.Grid2x2;
import hostilelands.tools.Grid3x3;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.function.Function;

/**
 *
 * @author Elscouta
 */
public class CreationForests extends CreationArea
{
    /**
     * Defines a new forest to fit in the given size.
     * @param size the size of the global square
     * @param origX
     * @param origY
     * @param previousLayers
     * @param neighbors
     * @param threshold the average size of a forest
     * @param number an average number of forests to create
     */
    public CreationForests(int size, int origX, int origY,
                           PartialSquare previousLayers,
                           Grid3x3<SummarySource> neighbors,
                           int threshold, int number)
    {
        super(size, origX, origY, previousLayers, neighbors,
              new SpawnInterpreter(size, threshold, number), 
              new DottedSquare(size));
    }
    
    private CreationForests(int size, int origX, int origY,
                            PartialSquare previousLayers,
                            Grid3x3<SummarySource> neighbors,
                            DottedInterpreter interpreter, 
                            DottedSquare edgeConstraints)
    {
        super(size, origX, origY, previousLayers, neighbors, 
                interpreter, 
                edgeConstraints);
    }
    
    public static Factory<CreationForests> getFactory(int threshold, int number)
    {
        return (size, origX, origY, previousLayers, neighbors) -> 
                new CreationForests(size, origX, origY, previousLayers, neighbors,
                                    threshold, number);
    }
        
    @Override
    protected CreationForests createChild(int size, int origX, int origY,
                                          PartialSquare previousLayers,
                                          Grid3x3<SummarySource> neighbors,
                                          int quarter, DottedSquare childConstraints)
    {
        return new CreationForests(size, origX, origY, previousLayers, neighbors, 
                                   interpreter.clip(quarter), childConstraints);
    }
    
    @Override
    public PartialTileData getTileData()
    {
        assert(size == 0);
        
        PartialTileData data = new PartialTileData();
        data.setTerrain(TerrainType.FOREST, false);
        return data;
    }
    
    @Override
    public PartialSquareSummary getSummary()
    {
        PartialSquareSummary summary = new PartialSquareSummary(size);
        
        if (size > 0)
            summary.addTerrainGuess(TerrainType.FOREST, 
                    interpreter.getExpectedRatio(edgeConstraints.getRatio()), 
                    false);
        else
            summary.addTerrainGuess(TerrainType.FOREST, 1, false);
        
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
        private final int threshold;
        private int nSE;
        private int nSW;
        private int nNE;
        private int nNW;
        private final boolean spawning;
        
        public SpawnInterpreter(int size, int threshold, int number) 
        {
            super(size, 0.45f, 4.0f, 4.0f, 3.0f);
            this.threshold = threshold;

            if (number == 1)
                spawning = Math.random() < 1 - Math.pow(0.5, (double) threshold / (double) size);
            else
                spawning = false;
            
            if (spawning)
                number--;
            
            nSE = 0; nSW = 0; nNE = 0; nNW = 0;
            
            while (number > 0)
            {
                double rand = Math.random();
                
                if (rand < 0.25f)
                    nSE++;
                else if (rand < 0.50f)
                    nSW++;
                else if (rand < 0.75f)
                    nNE++;
                else
                    nNW++;
            
                number--;
            }
        }

        @Override
        public Function<Integer, Iterator<Point2D>> onMissingEdge(int edgeCount) 
        {
            if (spawning)
            {                
                return (Integer edge) -> {
                    return centeredDiamondSupplier(edge, size, 0.25f);
                };
            }
            else
            {
                return super.onMissingEdge(edgeCount);
            }
        }

        @Override
        public DottedInterpreter clip(int quarter) 
        {
            int n;
            
            switch (quarter)
            {
                case Grid2x2.SOUTHEAST:
                    n = nSE;
                    break;
                case Grid2x2.SOUTHWEST:
                    n = nSW;
                    break;
                case Grid2x2.NORTHEAST:
                    n = nNE;
                    break;
                case Grid2x2.NORTHWEST:
                    n = nNW;
                    break;
                default:
                    assert(false);
                    return null;
            }
            
            if (n > 0)
                return new SpawnInterpreter(size / 2, threshold, n);
            else
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
            if (size > threshold)
                return (((double) threshold / (double) size) + ratio) / 2d;
            else
                return (1d + ratio) / 2d;
        }
    }
}
