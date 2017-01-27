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
public class Pair<T, U> 
{
    public T first;
    public U second;
    
    public Pair()
    {
        
    }
    
    public Pair(T f, U s)
    {
        this.first = f;
        this.second = s;
    }
    
    public String toString()
    {
        return String.format("<%s, %s>", first.toString(), second.toString());
    }
}
