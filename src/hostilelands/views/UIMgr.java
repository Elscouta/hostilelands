/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.views;

import hostilelands.Game;
import hostilelands.champion.Champion;
import hostilelands.dialogs.DialogDesc;
import hostilelands.models.SingletonModel;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author Elscouta
 */
public class UIMgr 
{
    MainWindow mainWindow;
    UILayer uiLayer;
    Game game;
    
    SingletonModel<DialogDesc> dialogShown;
    SelectionModel<Champion, ButtonPanel> championDetailsShown;
    Border windowBorder;
    
    public UIMgr()
    {
        Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        windowBorder = BorderFactory.createCompoundBorder(raisedetched, loweredetched);
        
        dialogShown = new SingletonModel();
        championDetailsShown = new SelectionModel();
    }
    
    public void createUI(Game game)
    {
        this.game = game;
        MainWindow.create(game, this);
    }
    
    public MainWindow getMainWindow()
    {
        return mainWindow;
    }
    
    public UILayer getUILayer()
    {
        return uiLayer;
    }
    
    public void onMainWindowLoad(MainWindow w)
    {
        this.mainWindow = w;
        this.uiLayer = w.getUILayer();
    }
    
    public Border getBorder()
    {
        return windowBorder;
    }
    
    /**
     * This should only be called by UI elements that wish to subscribe to 
     * this model. 
     * 
     * @return The dialog event. If non-null, it should be displayed by the
     * dialog layer.
     */
    public SingletonModel<DialogDesc> getDialogModel()
    {
        return dialogShown;
    }
    
    /**
     * This should only be called by UI elements that wish to subscribe to this
     * model, or by buttons that wish to announce they are selecting this 
     * element.
     * 
     * @return the selection model. If non-null, it should be displayed by
     * the ui layer.
     */
    public SelectionModel<Champion, ButtonPanel> getChampionDetailsModel()
    {
        return championDetailsShown;
    }
}
