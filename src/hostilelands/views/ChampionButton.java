package hostilelands.views;

import hostilelands.Settings;
import hostilelands.champion.Champion;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * A simple button showing a champion. No binding or behaviour defined.
 *
 * @author Elscouta
 */
public final class ChampionButton extends ButtonPanel
{
    Champion champion;
    
    public ChampionButton(Champion c)
    { 
        this.champion = c;
        
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBackground(new java.awt.Color(255, 255, 255));
        setOpaque(true);

        JLabel portraitLabel = new JLabel(c.getName());
        portraitLabel.setPreferredSize(new Dimension(Settings.ICON_PERSON_WIDTH, Settings.ICON_PERSON_HEIGHT));
        portraitLabel.setIcon(c.getPortrait());

        JLabel nameLabel = new JLabel (c.getName());
        nameLabel.setText(c.getName());
            
        add(portraitLabel);
        add(nameLabel);        
    }
}
