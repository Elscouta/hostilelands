/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.desc;

/**
 * A simple wrapper class for an element that actually doesn't depend on
 * level.
 * 
 * @param <T> A type that is being made "dependant" on level.
 * 
 * @author Elscouta
 */
public class LevelConstant<T> implements LevelDependant<T>
{
    private final T obj;
    
    public LevelConstant(T obj)
    {
        this.obj = obj;
    }
    
    @Override
    public T atLevel(int level)
    {
        return obj;
    }
}
