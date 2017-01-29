/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.tools;

import hostilelands.Settings;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @param <T>
 * @author Elscouta
 */
public class Distribution<T> 
{
    private final Map<T, Double> map;

    private Distribution()
    {
        map = new HashMap<>();
    }
    
    public Distribution(T t)
    {
        map = new HashMap<>();
        map.put(t, 1.0d);
    }
    
    public static <T> Distribution<T> binaryChoice(double ratio, T ifTrue, T ifFalse)
    {
        Distribution<T> d = new Distribution<>();
        d.map.put(ifTrue, ratio);
        d.map.put(ifFalse, 1 - ratio);
        return d;
    }
    
    public synchronized void replaceValue(T key, Distribution<T> value)
    {
        Double oldKeyValue = map.remove(key);
        if (oldKeyValue == null)
            return;
        
        for (Map.Entry<T, Double> e : value.map.entrySet())
        {
            double oldValue = map.getOrDefault(e.getKey(), 0d);
            map.put(e.getKey(), oldValue + oldKeyValue * e.getValue());
        }
    }
    
    public synchronized <U> Distribution<U> map(Function<T, U> f)
    {
        Distribution<U> ret = new Distribution<>();
        
        for (Map.Entry<T, Double> myEntry : map.entrySet())
        {
            U newKey = f.apply(myEntry.getKey());
            ret.map.merge(f.apply(myEntry.getKey()), 
                          myEntry.getValue(), 
                          (x, y) -> x+y);
            assert( ret.get(newKey) > -1);
        }
        
        return ret;
    }
    
    public synchronized <U> Distribution<U> compose(Distribution<? extends Function<T, U>> f)
    {
        Distribution<U> ret = new Distribution<>();
        
        for (Map.Entry<T, Double> oldEntry : map.entrySet())
        {
            T oldKey = oldEntry.getKey();
            double oldKey_Value = oldEntry.getValue();
            
            for (Map.Entry<? extends Function<T, U>, Double> newEntry : f.map.entrySet())
            {
                double ratio = newEntry.getValue();
                Function<T, U> transform = newEntry.getKey();
                U newKey = transform.apply(oldKey);

                ret.map.merge(newKey, oldKey_Value * ratio, (x,y) -> x+y);
                assert( ret.get(newKey) > -1);
            }
        }
        
        return ret;
    }    
    
    public Set<Map.Entry<T, Double>> entrySet()
    {
        return map.entrySet();
    }
    
    public double get(T t)
    {
        double v = map.getOrDefault(t, 0d);
        
        assert (v > - Settings.FLOAT_PRECISION && v < 1 + Settings.FLOAT_PRECISION);

        return v;
    }
}
