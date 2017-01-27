/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.views;

import hostilelands.champion.Slot;
import hostilelands.desc.Ability;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JComboBox;

/**
 *
 * @author Elscouta
 */
public final class SlotView extends JComboBox
{
    final Map<String, Ability> options;
    final Slot<Ability> slot;
    
    /**
     * FIXME: This is using ability names as identifiers.
     * 
     * @param opts
     * @param slot 
     */
    SlotView(List<Ability> opts, Slot<Ability> slot)
    {
        this.slot = slot;

        this.options = opts.stream()
                  .collect(Collectors.toMap(x -> x.getName(), x -> x));
        this.options.put("-- no ability --", null);
        options.keySet().stream().sorted().forEach(s -> addItem(s));
               
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = (String) getSelectedItem();
                Ability ability = options.get(id);
                
                slot.set(options.get(id));
            }
        });
        
        if (slot.empty() == false)
            setSelectedItem(slot.get().getName());
        else
            setSelectedItem("-- no ability --");
    }
}
