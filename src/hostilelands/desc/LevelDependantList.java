/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.desc;

import hostilelands.tools.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A list that can vary depending on the level of the enclosing class.
 * 
 * @param <T> The elements that must be instantiated based on level
 *
 * @author Elscouta
 */
public class LevelDependantList<T> implements LevelDependant<List<T>>
{   
    private class Entry
    {
        LevelDependant<T> element;
        Predicate<Integer> condition;
        LevelDependant<Integer> conversion;
    }
    private final ArrayList<Entry> list; 

    LevelDependantList()
    {
        list = new ArrayList<>();
    }

    @Override
    public List<T> atLevel(int level)
    {
        return list.stream()
                   .filter(e -> e.condition.test(level))
                   .map(e -> e.element.atLevel(e.conversion.atLevel(level)))
                   .collect(Collectors.toList());
    }
    
    public void add(LevelDependant<T> element, 
                    Predicate<Integer> condition,
                    LevelDependant<Integer> conversion)
    {
        Entry e = new Entry();
        e.element = element;
        e.condition = condition;
        e.conversion = conversion;
        
        list.add(e);
    }
    
    public void add(T element, Predicate<Integer> condition)
    {
        add(new LevelConstant<>(element), 
            condition, 
            new LevelDependantInteger.Identity());
    }
    
    public void add(LevelDependant<T> element, Predicate<Integer> condition)
    {
        add(element,
            condition,
            new LevelDependantInteger.Identity());
    }
    
    public void add(LevelDependant<T> element)
    {
        add(element, e -> true);
    }
    
    public void add(T element)
    {
        add(element, e -> true);
    }
}
