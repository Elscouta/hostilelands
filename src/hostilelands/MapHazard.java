/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

/**
 *
 * @author Elscouta
 */
public abstract class MapHazard extends MapEntity
{
    /**
     * Represents the various strategies that a hazard can follow.
     * Implementations do not need to be thread-safe, as they will be
     * called within the lock of the hazard itself.
     */
    public static interface Strategy
    {
        /**
         * Informs the implementing class the need to compute the strategy.
         * 
         * @param minutes The time elapsed since the last call to computeStrategy 
         */
        void computeStrategy(int minutes);
        
        /**
         * Returns the current x-axis moving direction of the entity. 
         * The (x,y) speed vector is unitary.
         * 
         * @return The current x-axis direction of the entity.
         */
        double getDirectionX();
        
        /**
         * Returns the current y-axis moving direction of the entity. 
         * The (x,y) move vector is unitary.
         * 
         * @return The current y-axis direction of the entity.
         */
        double getDirectionY();
    }
        
    final Strategy strategy;
 
    protected MapHazard(String iconIdentifier, Strategy strategy)
    {
        super(iconIdentifier);
        this.strategy = strategy;
    }
    
    @Override
    public synchronized void simulateTime(int minutes)
    {
        final int granularity = 10;
        final double speed = 0.03f;
        
        double x = getX();
        double y = getY();

        
        int i = 0;
        while (i < minutes)
        {
            int d = granularity;
            if (i + granularity > minutes)
                d = i + granularity - minutes;

            strategy.computeStrategy(minutes);            
            x += strategy.getDirectionX() * speed;
            y += strategy.getDirectionY() * speed;
            i += d;
            
            setPosition(x, y);            
        }
    }

}
