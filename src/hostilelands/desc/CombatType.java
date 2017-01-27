/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.desc;

import hostilelands.DataStore;
import hostilelands.XMLStore;
import hostilelands.desc.Dice.Symbol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a combat type, or "combat ruleset".
 * 
 * TODO: Decides whether this should be loaded through XML or not.
 * 
 * @author Elscouta
 */
public class CombatType 
{
    DataStore store;
    List<Symbol> symbols;
    Map<Symbol, List<Rule>> rules;
    Map<Symbol, Integer> damageValues;
    Map<Symbol, Integer> moraleValues;
    
    public CombatType(DataStore mainstore)
    {
       this.store = mainstore;
       this.symbols = new ArrayList<>();
       this.rules = new HashMap<>();
       this.damageValues = new HashMap<>();
       this.moraleValues = new HashMap<>();

       final Symbol SWORD = store.getSymbol("sword");
       final Symbol BOW = store.getSymbol("bow");
       final Symbol SPEED = store.getSymbol("speed");
       final Symbol SHIELD = store.getSymbol("shield");
       final Symbol FLAG = store.getSymbol("flag");
       
       symbols.add(SWORD);
       symbols.add(BOW);
       symbols.add(SPEED);
       symbols.add(SHIELD);
       symbols.add(FLAG);
       
       rules.put(SWORD, new ArrayList<>());
       rules.put(BOW, new ArrayList<>());
       rules.put(SPEED, new ArrayList<>());
       rules.put(SHIELD, new ArrayList<>());
       rules.put(FLAG, new ArrayList<>());
       
       rules.get(SWORD).add(new SuppressRule(SWORD, 2, SWORD, 1));
       rules.get(SHIELD).add(new SuppressRule(SHIELD, 1, SWORD, 1));
       rules.get(SHIELD).add(new SuppressRule(SHIELD, 2, BOW, 1));
       rules.get(SPEED).add(new SuppressRule(SPEED, 1, BOW, 1));
       rules.get(FLAG).add(new SuppressRule(FLAG, 1, FLAG, 1));
       
       damageValues.put(BOW, 1);
       damageValues.put(SWORD, 1);
       moraleValues.put(BOW, 1);
       moraleValues.put(SWORD, 1);
       moraleValues.put(FLAG, 2);
    }
    
    public Iterable<Symbol> getRelevantSymbols()
    {
        return symbols;
    }
    
    public Map<Symbol, Integer> getLiveSymbols(Map<Symbol, Integer> mine, Map<Symbol, Integer> opponents)
    {
        Map<Symbol, Integer> retValue = new HashMap();
        retValue.putAll(mine);
        
        for (Map.Entry<Symbol, Integer> oppSymbol : opponents.entrySet())
        {
            Symbol s = oppSymbol.getKey();
            int i = oppSymbol.getValue();
            
            List<Rule> symbolRules = rules.get(s);
            
            for (Rule rule : symbolRules)
                i = rule.apply(retValue, i);
        }
        
        return retValue;
    }
    
    public int getDamageTaken(Map<Symbol, Integer> mine, Map<Symbol, Integer> opponents)
    {
        return damageValues.entrySet().stream()
                                      .mapToInt(e -> opponents.get(e.getKey()) * e.getValue())
                                      .reduce((x, y) -> x + y)
                                      .orElse(0);
    }
    
    public int getMoraleTaken(Map<Symbol, Integer> mine, Map<Symbol, Integer> opponents)
    {
        return moraleValues.entrySet().stream()
                                      .mapToInt(e -> opponents.get(e.getKey()) * e.getValue())
                                      .reduce((x, y) -> x + y)
                                      .orElse(0);        
    }
            
    /**
     * Represents a rule of the combat, aka how one side symbols will
     * affect the other side.
     */
    private interface Rule
    {
        /**
         * Applies a symbol rule. 
         * 
         * @param mine The set of symbols to be altered.
         * @param symbol The number of effects to be applied.
         * @return The number of effects that are still to be applied.
         */
        public int apply(Map<Symbol, Integer> mine, int number);
        
        /**
         * Returns the symbol that the rule refers to (the one initiating the
         * effect and not receiving it)
         * @return The symbol initiating the effect
         */
        public Symbol getSymbol();
    }

    /**
     * Represents a rule in which a symbol suppresses (disables) an
     * opposing symbol with a given ratio.
     */
    private class SuppressRule implements Rule
    {
        private final Symbol affectingSymbol;
        private final int numberEffectsConsumed;
        private final Symbol affectedSymbol;
        private final int suppressAffected;
        
        public SuppressRule(Symbol affectingSymbol, int numberEffectsConsumed, 
                            Symbol affectedSymbol, int suppressAffected)
        {
            this.affectingSymbol = affectingSymbol;
            this.numberEffectsConsumed = numberEffectsConsumed;
            this.affectedSymbol = affectedSymbol;
            this.suppressAffected = suppressAffected;
        }
        
        @Override
        public int apply(Map<Symbol, Integer> mine, int number)
        {
            while (number >= numberEffectsConsumed)
            {
                int affectedSymbolCount = mine.get(affectedSymbol);
                
                if (affectedSymbolCount == 0)
                    return number;
                
                affectedSymbolCount -= suppressAffected;
                number -= numberEffectsConsumed;
                
                if (affectedSymbolCount < 0)
                    affectedSymbolCount = 0;
                
                mine.put(affectedSymbol, affectedSymbolCount);
            }
            
            return number;
        }
        
        @Override
        public Symbol getSymbol()
        {
            return affectingSymbol;
        }        
    }
}
