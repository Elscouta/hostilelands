/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.Settings;
import hostilelands.tools.Line2D;
import hostilelands.tools.Rectangle;
import hostilelands.tools.Vector2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author Elscouta
 */
public interface DottedInterpreter 
{
    /**
     * This function provides a way to determine what internal points should 
     * be added when there is no edge constraint. It is assumed that the 
     * point will be added on a center edge to generate a kind of diamond
     * shape.
     * 
     * Note that grow() and shrink() operations will still be called on the
     * center edges after this function, so they don't need to include much
     * randomness. In most cases, the iterator should only include one point.
     * 
     * @param missingCount the number of missing edges
     * @return a function that takes an edge identifier and returns an iterator
     * on points to replace that edge
     */
    public Function<Integer, Iterator<Point2D>> onMissingEdge(int missingCount);
    
    public Iterator<Deformation> getInternalDeformations(Point2D orig, Point2D end);
    public Deformation getPositiveEntry(Point2D orig, Point2D end);
    public Deformation getNegativeEntry(Point2D orig, Point2D end);

    /**
     * Returns the expected amount of matter inside the square, based on the 
     * ratio of positive constraints on the edges.
     * 
     * @param edgeRatio the ratio of positive constraints on the edges.
     * @return the ratio of matter inside the square (guess)
     */
    public double getExpectedRatio(double edgeRatio);
    
    /**
     * Clips the interpreter to a sub square
     * @param quarter The quarter that is considered, as a constant
     * inside Grid2x2
     * @return A newly created interpreter for the sub square.
     */
    public DottedInterpreter clip(int quarter);
    
    /**
     * Returns whether an area with no external constraints should be discarded
     * @return true if the object should be discarded if it has no external 
     * constraints.
     */
    public boolean deleteOnEmptyEdges();
    

}
