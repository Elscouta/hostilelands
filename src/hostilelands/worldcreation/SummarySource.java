/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.World;
import hostilelands.tools.Grid2x2;

/**
 * Interface for objects that are able to generate square summaries. 
 * 
 * @author Elscouta
 */
public interface SummarySource 
{
    /**
     * Returns the childs of the source when splitting the square into four
     * quarters.
     * 
     * @return a grid containing the childs
     */
    public Grid2x2<? extends SummarySource> getChilds()
            throws World.GenerationFailure;
        
    /**
     * Returns the size of the square. Size must be 0, 1 or a multiple of 2.
     * Please note that a square of size 0 corresponds to a single tile and not
     * an empty square.
     * 
     * @return the size
     */
    public int getSize();
    
    /**
     * Returns the summary associated to the object (a partial square, or the
     * void outside the world)
     * 
     * @return the summary
     */
    public PartialSquareSummary getSummary();
    
    /**
     * Returns the previous layer.
     * 
     * @return the previous layer
     */
    public SummarySource getPreviousLayers();
}
