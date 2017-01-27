/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.tools;

import hostilelands.Settings;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author Elscouta
 */
public class Grid2x2 <T>
{
    public T southeast;
    public T northeast;
    public T southwest;
    public T northwest;
    
    public static final int SOUTHEAST = 0;
    public static final int NORTHEAST = 1;
    public static final int NORTHWEST = 2;
    public static final int SOUTHWEST = 3;
    
    public Grid2x2()
    {
    }
    
    public Grid2x2(T southeast, T northeast, T northwest, T southwest)
    {
        this.southeast = southeast;
        this.northeast = northeast;
        this.northwest = northwest;
        this.southwest = southwest;
    }
    
    public <U extends T> Grid2x2(Grid2x2<U> o)
    {
        this.southeast = o.southeast;
        this.northeast = o.northeast;
        this.northwest = o.northwest;
        this.southwest = o.southwest;
    }
    
    public <U> Grid2x2<U> map(Function<T, U> f)
    {
        return mapMarked((type, t) -> f.apply(t));
    }
    
    public <U> Grid2x2<U> mapMarked(BiFunction<Integer, T, U> f)
    {
        return new Grid2x2<>(f.apply(SOUTHEAST, southeast), f.apply(NORTHEAST, northeast),
                             f.apply(NORTHWEST, northwest), f.apply(SOUTHWEST, southwest));        
    }
    
    public void iter(Consumer<T> f)
    {
        f.accept(southeast);
        f.accept(northeast);
        f.accept(northwest);
        f.accept(southwest);
    }
    
    public T reduce(T v, BiFunction<T, T, T> f)
    {
        v = f.apply(v, southeast);
        v = f.apply(v, northeast);
        v = f.apply(v, northwest);
        v = f.apply(v, southwest);
        
        return v;
    }
    
    public static <U> Grid2x2<U> generate(Supplier<U> f)
    {
        return new Grid2x2<>(f.get(), f.get(), f.get(), f.get());
    }
    
    public <U> Grid2x2<U> mapCardinal(BiFunction<T, CardinalMap<T>, U> f,
                                      CardinalMap<T> borders)
    {
        Grid2x2<U> ret = new Grid2x2<>();
        
        ret.southeast = f.apply(southeast, new CardinalMap<T>(borders.south,
                                                              borders.east,
                                                              this.northeast,
                                                              this.southwest));
        ret.northeast = f.apply(northeast, new CardinalMap<T>(this.southeast,
                                                              borders.east,
                                                              borders.north,
                                                              this.northwest));
        ret.northwest = f.apply(northwest, new CardinalMap<T>(this.southwest,
                                                              this.northeast,
                                                              borders.north,
                                                              borders.west));
        ret.southwest = f.apply(southwest, new CardinalMap<T>(borders.south,
                                                              this.southeast,
                                                              this.northeast,
                                                              borders.west));
        
        return ret;        
    }
    
    public <U> void iterCardinal(BiConsumer<T, CardinalMap<T>> f, 
                                 CardinalMap<T> borders)
    {
        class I
        {
            boolean g(T t, CardinalMap<T> b)
            {
                f.accept(t, b);
                return true;
            }
        }
        
        mapCardinal((new I())::g, borders);
    }
    
    public Grid2x2<Grid3x3<T>> propagateNeighbors(Grid3x3<T> neighbors)
    {
        return new Grid2x2(
                new Grid3x3(northwest, northeast, neighbors.o23,
                            southwest, southeast, neighbors.o23,
                            neighbors.o32, neighbors.o32, neighbors.o33),
                new Grid3x3(neighbors.o12, neighbors.o12, neighbors.o13,
                            northwest, northeast, neighbors.o23,
                            southwest, southeast, neighbors.o23),
                new Grid3x3(neighbors.o11, neighbors.o12, neighbors.o12,
                            neighbors.o21, northwest, northeast,
                            neighbors.o21, southwest, southeast),
                new Grid3x3(neighbors.o21, northwest, northeast,
                            neighbors.o21, southwest, southeast,
                            neighbors.o31, neighbors.o32, neighbors.o32)
        );
    }
    
    public <U> Grid2x2<U> mapRandom(Function<T, U> ifTrue, 
                                    Function<T, U> ifFalse,
                                    Grid2x2<Double> weights)
    {
        double total = weights.southeast + weights.northeast + 
                       weights.northwest + weights.southwest;
        
        assert(total > Settings.FLOAT_PRECISION);
        
        Grid2x2<Double> nWeight = weights.map(x -> x / total);
        nWeight.northeast += nWeight.southeast;
        nWeight.northwest += nWeight.northeast;
        nWeight.southwest += nWeight.northwest;
        
        Grid2x2<U> ret = map(x -> ifFalse.apply(x));
        double r = Math.random();
        
        if (r < nWeight.southeast)
            ret.southeast = ifTrue.apply(southeast);
        else if (r < nWeight.northeast)
            ret.northeast = ifTrue.apply(northeast);
        else if (r < nWeight.northwest)
            ret.northwest = ifTrue.apply(northwest);
        else
            ret.southwest = ifTrue.apply(southwest);
        
        return ret;
    }
    
    public static <U> U pickRandom(U u1, U u2, U u3, U u4)
    {
        double r = Math.random();
        
        if (r < 0.25d)
            return u1;
        else if (r < 0.5d)
            return u2;
        else if (r < 0.75d)
            return u3;
        else
            return u4;
    }
    
    public <U extends Combinable<U>> U funcAtPosition(Function<T, BiFunction<Integer, Integer, U>> f,
                                int size, int x, int y, int middleBehavior)
            throws Combinable.CombineFailure
    {
        assert(x >= 0 && y >= 0 && x <= size && y <= size);
        assert(size > 0);
        
        U u = null;
        
        if (2 * x > size || middleBehavior >= 0 && 2 * x == size)
        {
            if (2 * y > size || middleBehavior >= 0 && 2 * y == size)
                u = f.apply(southeast).apply(x - size/2, y - size/2).combine(u);                

            if (2 * y < size || middleBehavior <= 0 && 2 * y == size)
                u = f.apply(northeast).apply(x - size/2, y).combine(u);
        }

        if (2 * x < size || middleBehavior <= 0 && x == size / 2)
        {
            if (2 * y < size || middleBehavior <= 0 && 2 * y == size)
                u = f.apply(northwest).apply(x, y).combine(u);

            if (2 * y > size || middleBehavior >= 0 && 2 * y == size)
                u = f.apply(southwest).apply(x, y - size/2).combine(u);
        }
        
        assert(u != null);
        
        return u;
    }
}
