/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.tools;

/**
 *
 * @author Elscouta
 */
public interface Combinable<T>
{
    T combine(T o) throws CombineFailure;
    
    public static class CombineFailure extends Exception
    {
        
    }
}
