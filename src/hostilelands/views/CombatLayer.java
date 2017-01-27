/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.views;

import java.util.Iterator;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import hostilelands.Combat;
import hostilelands.desc.Dice;
import hostilelands.Game;
import hostilelands.Unit;
import hostilelands.Team;
import hostilelands.dialogs.DialogEndCombat;
import hostilelands.models.DiceModel;
import hostilelands.models.SingletonModel;
import java.awt.ComponentOrientation;
import java.util.Map;
import javax.swing.border.LineBorder;

/**
 *
 * @author Elscouta
 */
public final class CombatLayer extends JPanel implements ChangeListener
{
    Game game;
    SingletonModel<Combat> combat;
    
    JPanel topArea;
    ControlArea controlArea;
    RulesArea rulesArea;
    JPanel mainArea;
    TeamArea friendTeamArea;
    TeamArea enemyTeamArea;
    ScoreArea friendScoreArea;
    JPanel bottomArea;
    ScoreArea enemyScoreArea;
    PredictionArea predictionArea;
    
    
    private CombatLayer(Game game, SingletonModel<Combat> combat)
    {
        this.game = game;
        this.combat = combat;
        
        assert (!combat.exists());
        makeTransparent();
    }
    
    public static CombatLayer create(Game g, SingletonModel<Combat> combat)
    {
        CombatLayer obj = new CombatLayer(g, combat);
        combat.addChangeListener(obj);
        
        return obj;
    }
    
    private void makeTransparent()
    {
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
    }
    
    private void showCombat()
    {
        setOpaque(true);
        setBackground(new Color(240, 240, 240, 255));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
                
        controlArea = new ControlArea();
        controlArea.setInitialState();
        
        rulesArea = new RulesArea();
        rulesArea.showRules(null);

        topArea = new JPanel();
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.LINE_AXIS));
        topArea.add(controlArea);
        topArea.add(rulesArea);
        
        friendTeamArea = new TeamArea(ComponentOrientation.LEFT_TO_RIGHT);
        friendTeamArea.setTeam(combat.get().getFriendTeam());
        
        enemyTeamArea = new TeamArea(ComponentOrientation.RIGHT_TO_LEFT);
        enemyTeamArea.setTeam(combat.get().getEnemyTeam());
                
        mainArea = new JPanel();
        mainArea.setLayout(new BoxLayout(mainArea, BoxLayout.LINE_AXIS));
        mainArea.setPreferredSize(new Dimension(1000, 500));
        mainArea.add(friendTeamArea);
        mainArea.add(Box.createHorizontalGlue());
        mainArea.add(enemyTeamArea);
        
        friendScoreArea = new ScoreArea();
        friendScoreArea.setTeam(combat.get().getFriendTeam());
        
        enemyScoreArea = new ScoreArea();
        enemyScoreArea.setTeam(combat.get().getEnemyTeam());
        
        predictionArea = new PredictionArea();

        bottomArea = new JPanel();
        bottomArea.setLayout(new BoxLayout(bottomArea, BoxLayout.LINE_AXIS));
        bottomArea.add(friendScoreArea);
        bottomArea.add(Box.createHorizontalGlue());
        bottomArea.add(predictionArea);
        bottomArea.add(Box.createHorizontalGlue());
        bottomArea.add(enemyScoreArea);
        
        add(topArea);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(mainArea);
        add(Box.createVerticalGlue());
        add(bottomArea);
    }
    
    @Override
    public void stateChanged(ChangeEvent e)
    {
        removeAll();
        
        if (combat.exists())
            showCombat();
        else
            makeTransparent();
        
        revalidate();
        repaint();
    }
    
    private class RollButtonPressed implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            combat.get().rollAllDices();
            
            controlArea.setPostRollState();
        }
    }
    
    private class LeaveButtonPressed implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            Combat.Summary summary = combat.get().resolveFight();
            DialogEndCombat dialog = new DialogEndCombat(summary);
            
            game.openDialog(dialog);
        }
    }
    
    private class ControlArea extends JPanel
    {
        public ControlArea()
        {
            
        }
        
        void setInitialState()
        {
            removeAll();
            
            JButton b = new JButton();
            b.setText("Roll!");
            add(b);
            b.addActionListener(new RollButtonPressed());
            
            revalidate();
            repaint();                    
        }
        
        void setPostRollState()
        {
            removeAll();
            
            JButton b = new JButton();
            b.setText("Leave fight.");
            add(b);
            b.addActionListener(new LeaveButtonPressed());
            
            revalidate();
            repaint();
        }        
    }
    
    private final class ScoreArea extends JPanel implements ChangeListener
    {
        Team team;
        
        public ScoreArea()
        {
            team = null;
        
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setBorder(new LineBorder(Color.BLACK));
        }
        
        public void setTeam(Team team)
        {
            this.team = team;
            
            combat.get().addTeamChangeListener(team, this);
            showScore();
        }
        
        public void showScore()
        {
            removeAll();
            
            Map<Dice.Symbol, Integer> symbolCount = combat.get().countSymbols(team);
            Map<Dice.Symbol, Integer> symbolCountFinal = combat.get().countFinalSymbols(team);

            for (Map.Entry<Dice.Symbol, Integer> e : symbolCount.entrySet())
            {
                JPanel line = new JPanel();
                line.setLayout(new BoxLayout(line, BoxLayout.PAGE_AXIS));
                line.setBorder(new LineBorder(Color.RED));
                
                JLabel symbolLabel = new JLabel();
                symbolLabel.setIcon(e.getKey().getIcon());
                symbolLabel.setBorder(new LineBorder(Color.GREEN));
                line.add(symbolLabel);
                
                JLabel countLabel = new JLabel();
                countLabel.setText(e.getValue().toString());
                countLabel.setBorder(new LineBorder(Color.BLUE));
                line.add(countLabel);
                
                JLabel countFinalLabel = new JLabel();
                countFinalLabel.setText(symbolCountFinal.get(e.getKey()).toString());
                countFinalLabel.setBorder(new LineBorder(Color.BLUE));
                line.add(countFinalLabel);
                
                add(line);
            }
            
            revalidate();
            repaint();
        }
        
        @Override
        public void stateChanged(ChangeEvent e)
        {
            showScore();
        }
    }
    
    private class PredictionArea extends JPanel implements ChangeListener
    {
        public PredictionArea()
        {
        }
        
        public void setup()
        {
            combat.get().addTeamChangeListener(combat.get().getFriendTeam(), this);
            combat.get().addTeamChangeListener(combat.get().getEnemyTeam(), this);
        }
        
        @Override
        public void stateChanged(ChangeEvent e)
        {
            
        }
    }
    
    private class TeamArea extends JPanel
    {
        ComponentOrientation align;
        
        public TeamArea(ComponentOrientation align)
        {
            this.align = align;
        }
            
        private void addUnit(Unit u)
        {
            JPanel p = new JPanel();
            p.setComponentOrientation(align);
            p.setLayout(new FlowLayout(FlowLayout.LEADING));
            
            JLabel l = new JLabel();
            l.setIcon(u.getPortrait());
            p.add(l);
            
            Iterator<DiceModel> dices = combat.get().getUnitDices(u);
            while (dices.hasNext())
            {
                DiceModel diceModel = dices.next();
                JPanel diceWidget = RollableDiceWidget.create(diceModel);
                p.add(diceWidget);
            }

            add(p);
        }
        
        public void setTeam(Team t)
        {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

            for (Unit u : t.getUnits())
                addUnit(u);
        }
                
    }
    
    private class RulesArea extends JPanel
    {
        public RulesArea()
        {
            
        }
        
        public void showRules(Object rules)
        {
            
        }
    }
}
