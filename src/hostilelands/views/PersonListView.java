/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.views;

import hostilelands.Game;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import hostilelands.Settings;
import hostilelands.champion.Champion;
import javax.swing.border.LineBorder;
import hostilelands.ModifiableTeam;
import hostilelands.Unit;

/**
 *
 * @author Elscouta
 */
public class PersonListView extends JPanel
                            implements ListDataListener {
    ModifiableTeam controller;
    UIMgr uiMgr;
    
    private PersonListView(UIMgr uiMgr, ModifiableTeam c) 
    {
        controller = c;
        this.uiMgr = uiMgr;
        
        GridLayout layout = new GridLayout(1, 0);
        setLayout(layout);
        
        setupComponents();
    }
    
    /**
     * Creates the buttons in the panel.
     * 
     * FIXME: Make it so that it allows unit buttons too.
     */
    private void setupComponents()
    {
        ListModel<Unit> model = controller.getUnits();
        for (int i = 0; i < model.getSize(); i++) 
        {
            Champion p = (Champion) model.getElementAt(i);

            ButtonPanel button = new ChampionButton(p);
            uiMgr.getChampionDetailsModel().bindButton(button, p);
            
            this.add(button);
        }
    }
    
    /**
     * Refreshes completely the panel, including redraw.
     */
    private void refresh()
    {
        removeAll();
        setupComponents();
        revalidate();
        repaint();
    }
    
    public static PersonListView createViewOf(ModifiableTeam controller, UIMgr uiMgr) 
    {
        PersonListView view = new PersonListView(uiMgr, controller);
        controller.getUnits().addListDataListener(view);
        
        return view;
    }
    
    @Override public void contentsChanged(ListDataEvent e) {
        refresh();
    }
    
    @Override public void intervalAdded(ListDataEvent e) {
        refresh();
    }
    
    @Override public void intervalRemoved(ListDataEvent e) {
        refresh();
    }
}
