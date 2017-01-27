/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.TerrainType;
import hostilelands.tools.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
     */
    public CreationLandmass(int size)
    {
        super(size, new SpawnInterpreter(size), new DottedSquare(size));
    }
    
    private CreationLandmass(int size, DottedInterpreter interpreter, DottedSquare edgeConstraints)
    {
        super(size, interpreter, edgeConstraints);
    }
    
    @Override
    protected CreationLandmass createChild(int quarter, DottedSquare childConstraints)
    {
        return new CreationLandmass(size / 2, interpreter.clip(quarter), childConstraints);
    }
    
    @Override
    public TileData fillTileData(TileData data)
    {
        data.addTerrain(TerrainType.GRASS, true);
        return data;
    }
    
    @Override
    public TileDataSummary fillTileDataSummary(TileDataSummary summary)
    {
        summary.addTerrainGuess(TerrainType.GRASS, 
                interpreter.getExpectedRatio(edgeConstraints.getRatio()), true);
        
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
