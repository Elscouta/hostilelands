/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.views;

import hostilelands.Game;
import hostilelands.champion.Champion;
import hostilelands.models.SingletonModel;
import java.awt.Color;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Elscouta
 */
public final class UILayer extends JPanel implements ChangeListener
{
    private final UIMgr uiMgr;
    private final SingletonModel<Champion> championDetailsShown;
    private ChampionDetailsView championDetails;
    
    private UILayer(UIMgr uiMgr)
    {
        this.uiMgr = uiMgr;
        this.championDetailsShown = uiMgr.getChampionDetailsModel();
        
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBackground(new Color(0, 0, 0, 0));
        setOpaque(false);
    }
    
    static public UILayer create(UIMgr mgr)
    {
        UILayer obj = new UILayer(mgr);
        obj.championDetailsShown.addChangeListener(obj);
        return obj;
    }
    
    private void createChampionDetails(Champion champion)
    {
        add(Box.createVerticalGlue());
        championDetails = new ChampionDetailsView(uiMgr, champion);
        championDetails.setAlignmentX(Component.RIGHT_ALIGNMENT);
        add(championDetails);
    }
    
    @Override
    public void stateChanged(ChangeEvent e)
    {        
        removeAll();
        
        if (championDetailsShown.exists())
            createChampionDetails(championDetailsShown.get());
        
        revalidate();
        repaint();
    }
}
