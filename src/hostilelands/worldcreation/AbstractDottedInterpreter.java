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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * A base class to construct interpreters for area creations.
 *
 * @author Elscouta
 */
public abstract class AbstractDottedInterpreter implements DottedInterpreter
{
    int size;
    double growingFactor;
    double positiveSpikeCreation;
    double negativeSpikeCreation;
    double spikeReinforcement;
    
    public AbstractDottedInterpreter(int size, double growingFactor,
            double positiveSpikeCreation, double negativeSpikeCreation,
            double spikeReinforcement) 
    {
        super();
        this.size = size;
        this.growingFactor = growingFactor;
        this.positiveSpikeCreation = positiveSpikeCreation;
        this.negativeSpikeCreation = negativeSpikeCreation;
        this.spikeReinforcement = spikeReinforcement;
    }

    @Override
    public Function<Integer, Iterator<Point2D>> onMissingEdge(int edgeCount) 
    {
        if (edgeCount < 4 && Math.random() < growingFactor) {
            return (Integer edge) -> {
                return centeredDiamondSupplier(edge, size, 0.15f);
            };
        } else {
            return AbstractDottedInterpreter::emptySupplier;
        }
    }

    @Override
    public Iterator<Deformation> getInternalDeformations(Point2D orig, Point2D end) 
    {
        return generateInternalDeformations(orig, end, 
                0.5F, 
                0.5F, positiveSpikeCreation, 
                0.5F, negativeSpikeCreation, 
                Rectangle.makeSquare(size).shrink(0.1F));
    }

    @Override
    public Deformation getPositiveEntry(Point2D orig, Point2D end) {
        Point2D p = getPointInNeighborhood(orig, end, 
                0.0F, spikeReinforcement, 
                Rectangle.makeSquare(size));
            
        if (p == null) {
            return null;
        } else {
            return new Deformation.Positive(orig, p, end);
        }
    }

    @Override
    public Deformation getNegativeEntry(Point2D orig, Point2D end) {
        Point2D p = getPointInNeighborhood(orig, end, 
                0.0F, 4.0F, 
                Rectangle.makeSquare(size));
           
        if (p == null) {
            return null;
        } else {
            return new Deformation.Negative(orig, p, end);
        }
    }

    @Override
    public boolean deleteOnEmptyEdges() 
    {
        return true;
    }
    
    /**
     * Basic implementation for classes that expect the inside matter
     * to be equal to the ratio on the edges
     * @param ratio the ratio on the edges
     * @return the guessed ratio inside the square
     */
    @Override
    public double getExpectedRatio(double ratio)
    {
        assert (ratio > - Settings.FLOAT_PRECISION &&
                ratio < 1 + Settings.FLOAT_PRECISION);
        return ratio;
    }


    /**
     * A method that spawns points in a diamond around the center.
     * @param edgeid the identifier of the edge being replaced
     * @param size the size of the square being computer
     * @param minratio the minimum distance to the center.
     * @return an iterator over the (single) point replacing the edge
     */
    protected static Iterator<Point2D> centeredDiamondSupplier(
            int edgeid, int size, double minratio)
    {
        int center = size / 2;
        double radius = (minratio + (1 - minratio) * Math.random()) * (size / 2 - 1);
        Point2D p;
        
        switch (edgeid)
        {
            case DottedSquare.SOUTH: 
                p = new Point2D.Double(center, center - radius);
                break;
            case DottedSquare.EAST:
                p = new Point2D.Double(center + radius, center);
                break;
            case DottedSquare.NORTH:
                p = new Point2D.Double(center, center + radius);
                break;
            case DottedSquare.WEST:
                p = new Point2D.Double(center - radius, center);
                break;
            default:
                assert(false);
                return null;
        }
        
        
        List<Point2D> ret = new ArrayList<>();
        ret.add(p);
        return ret.iterator();
    }
        
    
    /**
     * A method that returns no points
     * @param edgeid ignored
     * @return an empty iterator
     */
    protected static Iterator<Point2D> emptySupplier(int edgeid)
    {
        return Collections.emptyIterator();
    }
    
    /**
     * Returns a list of deformations including:
     * - one main deformation using main_ratio
     * - one positive spike, with odds positive_odds and positive_ratio.
     * - one negative spike, with odds negative_odds and negative_ratio.
     * 
     * Positive spike is incompatible with negative spike.
     * 
     * @param orig the origin of the segment
     * @param end the end of the segment
     * @param main_ratio the ratio associated to the main deformation
     * @param positive_odds the odds of a positive spike being generated
     * @param positive_ratio the ratio associated to the positive spike
     * @param negative_odds the odds of a negative spike being generated
     * @param negative_ratio the ratio associated to the negative spike
     * @param clip a rectangle limiting the bounds of the deformation
     * @return an iterator on the generated deformations
     */
    protected static Iterator<Deformation> generateInternalDeformations(
            Point2D orig, Point2D end, double main_ratio,
            double positive_odds, double positive_ratio,
            double negative_odds, double negative_ratio,
            Rectangle clip)
    {
        List<Deformation> ret = new ArrayList<>();

        Line2D seg = new Line2D(orig, end);
        seg = clip.clip(seg);
        if (seg == null)
            return ret.iterator();
        
        if (Math.random() < 0.5)
        {
            Point2D np = getPointOnBisector(seg.getP1(), 
                                            seg.getP2(), 
                                            main_ratio, 
                                            clip);
            
            assert(np == null || clip.contains(np));
            if (np != null)
                ret.add(new Deformation.Positive(seg.getP1(), 
                                                 seg.getP2(),
                                                 np));
        }
        else
        {
            Point2D np = getPointOnBisector(seg.getP2(), 
                                            seg.getP1(),
                                            main_ratio, 
                                            clip);
            assert(np == null || clip.contains(np));
            if (np != null)
                ret.add(new Deformation.Negative(seg.getP1(), 
                                                 seg.getP2(),
                                                 np));
        }
        

        
        Point2D p1 = seg.pointAt(0.35f);
        Point2D p2 = seg.pointAt(0.65f);
        double r = Math.random();
        
        if (r < positive_odds)
        {
            Point2D np = getPointOnBisector(p1, p2, 
                                            main_ratio, 
                                            clip);

            assert(np == null || clip.contains(np));
            if (np != null)
                ret.add(new Deformation.Positive(p1, p2, np));
        }
        else if (r < positive_odds + negative_odds)
        {
            Point2D np = getPointOnBisector(p2, p1,
                                            main_ratio, 
                                            clip);
            assert(np == null || clip.contains(np));
            if (np != null)
                ret.add(new Deformation.Negative(p1, p2, np));
        }
        
        return ret.iterator();
    }
    
    /**
     * Returns a point of the bisector such that [orig-return-end] is
     * a direct triangle.
     * 
     * Distances are expressed as multiples of the segment.
     * 
     * @param orig The origin point of the segment
     * @param end The end point of the segment
     * @param ratio The maximum distance to the segment.
     * @param clip A square restricting the possible point positions.
     * @return A point on the bisector, or null if constraints can't be
     * fulfilled.
     */
    protected static Point2D getPointOnBisector(Point2D orig, Point2D end, 
                                             double ratio, Rectangle clip)
    {
        Vector2D segVec = new Vector2D(orig, end);
        Point2D middlePoint = segVec.half().apply(orig);
        
        Vector2D orthoVec = segVec.rotateQuarterClockwise().multiply(ratio);
        Line2D lineCandidate = new Line2D(middlePoint, orthoVec);
        Line2D lineCandidateClipped = clip.clip(lineCandidate);
                
        if (lineCandidateClipped == null)
            return null;
        
        assert(clip.contains(lineCandidateClipped.getP1()));
        assert(clip.contains(lineCandidateClipped.getP2()));
        return lineCandidateClipped.randomPoint();
        
    }
    
    /**
     * Returns a point in a rectangular neightborhood of a segment.
     * 
     * Distances are expressed as multiples of the segment length. If the
     * segment is not horizontal or vertical, the clipping will be imperfect
     * (but safe)
     * 
     * @param orig The origin point of the segment
     * @param end The end point of the segment
     * @param parallel_ratio The maximum distance to the segment extremes,
     * in the segment direction (CURRENTLY IGNORED)
     * @param perp_ratio The maximum distance to the [orig-end] line
     * @param clip A square restricting the possible point positions.
     * @return A point in the neighborhood, or null if constraints can't be
     * fulfilled.
     */
    protected static Point2D getPointInNeighborhood(Point2D orig, Point2D end,
                                                 double parallel_ratio,
                                                 double perp_ratio,
                                                 Rectangle clip)
    {
        Vector2D segVec = new Vector2D(orig, end);
        Vector2D orthoVec = segVec.rotateQuarterClockwise();
               
        Point2D middlePoint = segVec.half().apply(orig);
        Line2D bisector = new Line2D(orthoVec.reverse().apply(middlePoint),
                                     orthoVec.apply(middlePoint));
        
        bisector = clip.clip(bisector);
        
        if (bisector == null)
            return null;
        
        Point2D bisectorPoint = bisector.randomPoint();
        
        Line2D parallelLine = new Line2D(segVec.reverse().apply(bisectorPoint),
                                         segVec.apply(bisectorPoint));
        
        parallelLine = clip.clip(parallelLine);

        if (parallelLine == null)
            return null;
        
        Point2D ret = parallelLine.randomPoint();
        assert(clip.contains(ret));
        
        return ret;
    }
    
    public static DeformationSet getDeformedPolygon(DottedInterpreter me, DottedSquare square)
    {
        Polygon polygon = new Polygon();
        DeformationSet ret = new DeformationSet(polygon);
        
        int edgeCount = 0;

        int missingEdges = square.countEmptyEdges();
        DottedSquare.PointIterator it = square.getPointIterator(me.onMissingEdge(missingEdges));
        
        if (!it.hasNext())
            return ret;
        
        int firstPointEdge, previousPointEdge, currentPointEdge;
        Point2D firstPoint, previousPoint, currentPoint;
        
        assert(!it.isNextAHole());
        firstPointEdge = it.getNextPointType();
        firstPoint = it.next();
        currentPointEdge = firstPointEdge;
        currentPoint = firstPoint;

        polygon.addPoint(firstPoint);
        edgeCount++;        

        assert(it.hasNext());
        
        while (it.hasNext())
        {
            previousPointEdge = currentPointEdge;
            previousPoint = currentPoint;            
        
            boolean isHole = it.isNextAHole();
            currentPointEdge = it.getNextPointType();
            currentPoint = it.next();
            
            edgeCount++;
            
            // Add any point if it is far enough from previous one.
            if (currentPoint.distance(previousPoint) > Settings.PRECISION_DISTANCE)
                polygon.addPoint(currentPoint);
            else
                continue;
                        
            // Currently going inside
            if (previousPointEdge != currentPointEdge || 
                currentPointEdge == DottedSquare.INTERNAL ||
                previousPointEdge == DottedSquare.INTERNAL)
            {
                ret.add(me.getInternalDeformations(previousPoint, currentPoint));
            }
        }
        
        if (firstPointEdge == currentPointEdge)
            ret.add(me.getPositiveEntry(firstPoint, currentPoint));

        
        Iterator<Line2D> holeIterator = square.getHoleIterator();
        while (holeIterator.hasNext())
        {
            Line2D hole = holeIterator.next();
            ret.add(me.getNegativeEntry(hole.getP1(), hole.getP2()));            
        }
        
        return ret;
    }    
}
