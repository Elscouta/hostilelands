/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.champion;

import hostilelands.desc.SlotDesc;

/**
 *
 * @author Elscouta
 */
public class Slot <T extends SlottablePowerSource>
{
    private T obj;
    private final String name;
    private final String trait;
    
    public Slot(SlotDesc desc)
    {
        this.name = desc.getName();
        this.trait = desc.getTrait();
        this.obj = null;
    }
    
    public String getName()
    {
        return name;
    }
    
    public T get()
    {
        return obj;
    }
    
    public void set(T obj)
    {
        assert(obj == null || obj.hasTrait(trait));
        this.obj = obj;
    }
    
    public boolean empty()
    {
        return obj == null;
    }
    
}
