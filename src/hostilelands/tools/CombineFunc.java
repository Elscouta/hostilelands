/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.tools;

/**
 * Defines a ('a, 'a) -> 'a function whose semantics is to merge two objects
 * together. That merge can fail, in which case an exception is raised.
 * 
 * @param <T> the type that should be combined
 *
 * @author Elscouta
 */
@FunctionalInterface
public interface CombineFunc<T>
{
    public T combine(T a, T b) throws CombineFailure;
    
    public static <T> T combineNull(T a, T b) throws CombineFailure
    {
        if (a != null && b != null)
            throw new CombineFailure();
        
        if (a == null)
            return b;
        else
            return a;
    }
    
    public static <T> T combineNullOrEqual(T a, T b) throws CombineFailure
    {
        if (a != null && b != null && !a.equals(b))
            throw new CombineFailure();
        
        if (a == null)
            return b;
        else
            return a;        
    }
    
    /**
     * The merge failed.
     */
    public static class CombineFailure extends Exception
    {
        
    }
}
