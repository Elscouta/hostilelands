/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.views;

import hostilelands.dialogs.DialogDesc;
import java.util.Map;
import java.util.Iterator;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ListModel;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import hostilelands.Game;
import hostilelands.events.GameEvent;
import hostilelands.events.GameEventDialog;
import hostilelands.models.SingletonModel;




/**
 *
 * @author Elscouta
 */
public final class DialogLayer extends JPanel implements ChangeListener
{
    SingletonModel<DialogDesc> dialog;
    Game game;
    UIMgr uiMgr;
    
    private DialogLayer(Game game, UIMgr uiMgr)
    {
        this.uiMgr = uiMgr;
        this.dialog = uiMgr.getDialogModel();
        this.game = game;
        
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBackground(new Color(0, 0, 0, 0));
        setOpaque(false);

    }

    static DialogLayer create(Game game, UIMgr mgr)
    {
        DialogLayer obj = new DialogLayer(game, mgr);
        obj.dialog.addChangeListener(obj);
        return obj;
    }
    
    @Override
    public void stateChanged(ChangeEvent e)
    {
        removeAll();

        if (!dialog.exists())
            return;
            
        DialogDesc desc = dialog.get();
        Dialog d = createDialog(desc);
        add(Box.createHorizontalGlue());
        add(d);
        add(Box.createHorizontalGlue());
    
        revalidate();
        repaint();
    }
    
    final static private class DialogOptionDesc
    {
        public String human_text;
        public GameEvent action;
    }
    
    final private class DialogButtonPressed implements ActionListener 
    {
        GameEvent event;
        
        /**
         * 
         * @param event The event that should be fired when the button is pressed (can be null) 
         */
        public DialogButtonPressed(GameEvent event)
        {
            this.event = event;
        }
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            game.closeDialog(event);
        }
    }
            
    final private class Dialog extends JPanel
    {
        JLabel textArea;
        JPanel buttonArea;
        
        public Dialog(String title, String text) 
        {
            // --- Layout
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            
            // --- Internal
            textArea = new JLabel();
            textArea.setText(text);
            
            buttonArea = new JPanel();
            buttonArea.setLayout(new BoxLayout(buttonArea, BoxLayout.LINE_AXIS));
            
            add(textArea);       
            add(Box.createRigidArea(new Dimension(0, 15)));
            add(buttonArea);

            // --- Borders
            Border compound = uiMgr.getBorder();
            TitledBorder finalborder = BorderFactory.createTitledBorder(compound, title);
            finalborder.setTitleJustification(TitledBorder.CENTER);
            
            setBorder(finalborder);
        }
        

        private void addButton(String key, String human_text, GameEvent action)
        {
            JButton button = new JButton();
            button.setText(human_text);
            button.addActionListener(new DialogButtonPressed(action));
            
            buttonArea.add(button);
        }        
    }
    
    private Dialog createDialog(DialogDesc desc)
    {
        String title = desc.getTitle();
        String text = desc.getText();
        Map<String,DialogDesc.Option> options = desc.getOptions();
        
        Dialog obj = new Dialog(title, text);
            
        for (Map.Entry<String, DialogDesc.Option> e : options.entrySet())
            obj.addButton(e.getKey(), e.getValue().text, e.getValue().action);
            
        return obj;
    }
}
