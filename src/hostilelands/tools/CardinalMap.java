/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.tools;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author Elscouta
 */
public class CardinalMap <T>
{
    public T south;
    public T east;
    public T north;
    public T west;
    
    public CardinalMap()
    {
        
    }
    
    public CardinalMap(Supplier<T> f)
    {
        south = f.get();
        east = f.get();
        north = f.get();
        west = f.get();
    }
    
    public CardinalMap(T south, T east, T north, T west)
    {
        this.south = south;
        this.east = east;
        this.north = north;
        this.west = west;        
    }
    
    public <U extends T> CardinalMap(CardinalMap<U> o)
    {
        this.south = o.south;
        this.east = o.east;
        this.north = o.north;
        this.west = o.west;
    }
    public <U> CardinalMap<U> map(Function<T, U> f)
    {
        return new CardinalMap<>(f.apply(south), 
                                 f.apply(east), 
                                 f.apply(north), 
                                 f.apply(west));
    }
}
