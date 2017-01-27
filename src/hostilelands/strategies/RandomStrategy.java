/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.strategies;

import hostilelands.MapHazard;

/**
 * A hazard moving randomly across the map.
 * 
 * @author Elscouta
 */
public class RandomStrategy implements MapHazard.Strategy 
{
    double vx;
    double vy;
    
    @Override
    public void computeStrategy(int minutes)
    {
        double alpha = 2 * Math.PI * Math.random();
        vx = 2 * Math.cos(alpha);
        vy = 2 * Math.sin(alpha);
    }
    
    @Override
    public double getDirectionX()
    {
        return vx;
    }
    
    @Override
    public double getDirectionY()
    {
        return vy;
    }
}
