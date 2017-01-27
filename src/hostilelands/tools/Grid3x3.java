/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.tools;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author Elscouta
 */
public class Grid3x3<T> 
{
    public final T o11;
    public final T o12;
    public final T o13;
    public final T o21;
    public final T o22;
    public final T o23;
    public final T o31;
    public final T o32;
    public final T o33;
    
    public Grid3x3(T o11, T o12, T o13,
                   T o21, T o22, T o23,
                   T o31, T o32, T o33)
    {
        this.o11 = o11; this.o12 = o12; this.o13 = o13;
        this.o21 = o21; this.o22 = o22; this.o23 = o23;
        this.o31 = o31; this.o32 = o32; this.o33 = o33;
    }
    
    public Grid3x3(T center, Supplier<T> others)
    {
        o11 = others.get(); o12 = others.get(); o13 = others.get();
        o21 = others.get(); o22 = center;       o23 = others.get();
        o31 = others.get(); o32 = others.get(); o33 = others.get();
    }
    
    public Grid2x2<Grid2x2<T>> split2x2()
    {
        return new Grid2x2(
                new Grid2x2(o33, o23, o22, o32),
                new Grid2x2(o23, o13, o12, o22),
                new Grid2x2(o22, o12, o11, o21),
                new Grid2x2(o32, o22, o21, o31)
        );
    }
    
    public <U> Grid3x3<U> map(Function<T, U> f)
    {
        return new Grid3x3(f.apply(o11), f.apply(o12), f.apply(o13),
                           f.apply(o21), f.apply(o22), f.apply(o23),
                           f.apply(o31), f.apply(o32), f.apply(o33));
    }
    
    public void iter(Consumer<T> f)
    {
        f.accept(o11); f.accept(o12); f.accept(o13);
        f.accept(o21); f.accept(o22); f.accept(o23);
        f.accept(o31); f.accept(o32); f.accept(o33);
    }
    
    public T reduce(T v, BiFunction<T, T, T> f)
    {
        v = f.apply(v, o11);
        v = f.apply(v, o21);
        v = f.apply(v, o31);
        v = f.apply(v, o21);
        v = f.apply(v, o22);
        v = f.apply(v, o23);
        v = f.apply(v, o31);
        v = f.apply(v, o32);
        v = f.apply(v, o33);
        
        return v;
    }
    
    public Grid2x2<Grid3x3<T>> split3x3(Function<T, Grid2x2<T>> split)
    {
        Grid3x3<Grid2x2<T>> sg = map(split);
        
        return new Grid2x2(
                new Grid3x3(sg.o22.northwest, sg.o22.northeast, sg.o23.northwest,
                            sg.o22.southwest, sg.o22.southeast, sg.o23.southwest,
                            sg.o23.northwest, sg.o23.northeast, sg.o23.northwest),
                new Grid3x3(sg.o12.southwest, sg.o12.southeast, sg.o13.southwest,
                            sg.o22.northwest, sg.o22.northeast, sg.o23.northwest,
                            sg.o22.southwest, sg.o22.southeast, sg.o23.southwest),
                new Grid3x3(sg.o11.southeast, sg.o12.southwest, sg.o12.southeast,
                            sg.o21.northeast, sg.o22.northwest, sg.o22.northeast,
                            sg.o21.southeast, sg.o22.southwest, sg.o22.southeast),
                new Grid3x3(sg.o21.northeast, sg.o22.northwest, sg.o22.northeast,
                            sg.o21.southeast, sg.o22.southwest, sg.o22.southeast,
                            sg.o31.northeast, sg.o32.northwest, sg.o32.northeast)
        );
    }
}
