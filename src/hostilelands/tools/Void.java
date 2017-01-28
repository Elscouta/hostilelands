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
public class Void implements Combinable<Void>
{
    public static Void o = new Void();
    private Void() {}
    
    @Override public Void combine(Void other) { return o; }
}
