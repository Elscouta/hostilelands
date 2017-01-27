/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.tools.Intersectable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Elscouta
 */
public class DeformationSet implements Intersectable 
{
    private Polygon main;
    private List<Deformation> positives;
    private List<Deformation> negatives;
    
    public DeformationSet(Polygon main)
    {
        this.main = main;
        positives = new ArrayList<>();
        negatives = new ArrayList<>();
    }
    
    /**
     * Adding null will be silently ignored
     * @param d a deformation (null allowed)
     */
    public void add(Deformation d)
    {
        if (d == null)
            return;
        
        if (d.isPositive())
            positives.add(d);
        else
            negatives.add(d);
    }
    
    public void add(Iterator<Deformation> ds)
    {
        while (ds.hasNext())
            add(ds.next());
    }
    
    @Override
    public DottedLine getHorizontalIntersections(int y, int xmin, int xmax)
    {
        DottedLine ret = main.getHorizontalIntersections(y, xmin, xmax);
        
        for (Deformation d : positives)
            ret = ret.binaryOr(d.getHorizontalIntersections(y, xmin, xmax));
        
        for (Deformation d : negatives)
            ret = ret.binaryMinus(d.getHorizontalIntersections(y, xmin, xmax));

        return ret;
    }        
    
    @Override
    public DottedLine getVerticalIntersections(int x, int ymin, int ymax)
    {
        DottedLine ret = main.getVerticalIntersections(x, ymin, ymax);
        
        for (Deformation d : positives)
            ret = ret.binaryOr(d.getVerticalIntersections(x, ymin, ymax));
        
        for (Deformation d : negatives)
            ret = ret.binaryMinus(d.getVerticalIntersections(x, ymin, ymax));

        return ret;
        
    }
    
    /**
     * Returns a multi-line string
     * @return Description of the deformation set
     */
    @Override
    public String toString()
    {
        String s = new String();
        s += "main polygon: \n";
        s += main.toString();
        s += "\n";
        
        s += "positives: \n";
        
        for (Deformation d : positives)
            s += d.toString() + "\n";
        
        s += "negatives: \n";
        
        for (Deformation d : negatives)
            s += d.toString() + "\n";
        
        return s;
    }
}
