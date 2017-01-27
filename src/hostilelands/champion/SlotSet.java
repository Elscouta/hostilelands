/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.champion;

import hostilelands.desc.SlotDesc;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Elscouta
 */
public class SlotSet <T extends SlottablePowerSource> extends PowerSourceAggregate
{
    List<Slot<T>> slots;
    
    public SlotSet(List<SlotDesc> descs)
    {
        slots = descs.stream()
                     .map(d -> new Slot<T>(d))
                     .collect(Collectors.toList());
    }
    
    @Override
    protected List<PowerSource> getPowerSources()        
    {
        return slots.stream()
                    .filter(slot -> slot.empty() == false)
                    .map(slot -> slot.get())
                    .collect(Collectors.toList());
    }
    
    public List<Slot<T>> getSlots()
    {
        return slots;
    }
}
