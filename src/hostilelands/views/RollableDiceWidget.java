/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.views;

import hostilelands.desc.Dice;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import hostilelands.models.DiceModel;


/**
 *
 * @author Elscouta
 */
public final class RollableDiceWidget extends JPanel implements ChangeListener
{
    DiceView diceView;
    DiceModel diceModel;
    JLabel diceLabel;
    
    private RollableDiceWidget(DiceModel d)
    {
        Dice diceDesc = d.getDice();
        
        diceView = new DiceView(diceDesc);
        diceModel = d;
                
        diceLabel = new JLabel();
        diceLabel.setText(diceDesc.getName());
        
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(diceView);
        add(diceLabel);
    }
    
    static RollableDiceWidget create(DiceModel d)
    {
        RollableDiceWidget obj = new RollableDiceWidget(d);
        d.addChangeListener(obj);
        
        return obj;
    }
    
    @Override
    public void stateChanged(ChangeEvent e)
    {
        diceView.setFace(diceModel.getFace());
    }    
}
