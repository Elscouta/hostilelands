/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.tools;

import hostilelands.worldcreation.DottedLine;

/**
 *
 * @author Elscouta
 */
public interface Intersectable 
{
    /**
     * Checks the intersection of the polygon with a horizontal segment
     * @param y the y coordinate of the horizontal segment
     * @param xmin the minimum coordinate of the horizontal segment
     * @param xmax the maximum coordinate of the horizontal segment
     * @return a dotted line representing the parts of the segment that are inside
     * the polygon.
     */
    DottedLine getHorizontalIntersections(int y, int xmin, int xmax);

    /**
     * Checks the intersection of the polygon with a vertical segment
     * @param x the x coordinate of the vertical segment
     * @param ymin the minimum coordinate of the vertical segment
     * @param ymax the maximum coordinate of the vertical segment
     * @return a dotted line representing the parts of the segment that are inside
     * the polygon.
     */
    DottedLine getVerticalIntersections(int x, int ymin, int ymax);
}
