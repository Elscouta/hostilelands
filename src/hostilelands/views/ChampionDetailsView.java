/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.views;

import hostilelands.champion.Champion;
import hostilelands.champion.Slot;
import hostilelands.desc.Ability;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Elscouta
 */
public final class ChampionDetailsView extends JPanel 
{
    private JTabbedPane contentPane;
    
    private JPanel headerPane;
    private JLabel nameLabel;
    private JButton closeButton;
    
    private JPanel summaryTab;
    
    private JPanel abilitiesTab;
    private AbilitySlotListView abilitiesSlotsView;
    private AbilityListView knownAbilitiesView;
    
    private JPanel equipementTab;
    
    public ChampionDetailsView(UIMgr uiMgr, Champion champ)
    {
        // --- Headers

        nameLabel = new JLabel();
        nameLabel.setText(champ.getName());
        
        closeButton = new JButton();
        closeButton.setText("Close");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                uiMgr.getChampionDetailsModel().suppress();
            }
        });

        headerPane = new JPanel();
        headerPane.setLayout(new BoxLayout(headerPane, BoxLayout.LINE_AXIS));
        headerPane.add(nameLabel);
        headerPane.add(Box.createHorizontalGlue());
        headerPane.add(closeButton);
        
        // --- Summary tab
        summaryTab = new JPanel();

        // --- Abilities tab
        abilitiesSlotsView = new AbilitySlotListView();
        abilitiesSlotsView.setSlotList(champ, champ.getAbilities().getSlots());
        
        knownAbilitiesView = new AbilityListView();

        abilitiesTab = new JPanel();
        abilitiesTab.setLayout(new BoxLayout(abilitiesTab, BoxLayout.PAGE_AXIS));
        abilitiesTab.add(abilitiesSlotsView);
        abilitiesTab.add(knownAbilitiesView);
        
        // --- Equipement tab
        equipementTab = new JPanel();
        
        // --- Tabbed pane
        contentPane = new JTabbedPane();
        contentPane.addTab("Overview", summaryTab);
        contentPane.addTab("Abilities", abilitiesTab);
        contentPane.addTab("Equipement", equipementTab);
        
        // --- Ourselves
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setPreferredSize(new Dimension(400, 500));
        setMaximumSize(new Dimension(400, 500));
        setBorder(uiMgr.getBorder());
        
        add(headerPane);
        add(contentPane);
    }
    
    private class AbilitySlotListView extends JPanel
    {
        public AbilitySlotListView()
        {
            setLayout(new GridBagLayout());
        }
        
        public void setSlotList(Champion c, List<Slot<Ability>> slots)
        {
            GridBagConstraints labelConstraint = new GridBagConstraints();
            labelConstraint.anchor = GridBagConstraints.LINE_END;
            labelConstraint.ipadx = 4;
            labelConstraint.gridx = 0;
            labelConstraint.gridy = 0;
            labelConstraint.weightx = 0.5;
                                
            GridBagConstraints slotConstraint = new GridBagConstraints();
            slotConstraint.anchor = GridBagConstraints.LINE_START;
            slotConstraint.ipadx = 4;
            slotConstraint.gridx = 1;
            slotConstraint.gridy = 0;
            slotConstraint.weightx = 0.5;
            
            for (Slot slot : slots)
            {
                JLabel nameLabel = new JLabel();
                nameLabel.setText(slot.getName());
                add(nameLabel, labelConstraint);
              
                SlotView slotView = new SlotView(c.getKnownAbilities(), slot);
                add(slotView, slotConstraint);

                labelConstraint.gridy++;
                slotConstraint.gridy++;
            }
        }
    }
    
    private class AbilityListView extends JPanel
    {
        
    }
}
