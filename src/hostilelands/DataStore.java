/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.desc.Ability;
import hostilelands.desc.BasicUnitDesc;
import hostilelands.desc.BasicUnitPattern;
import hostilelands.desc.ChampionDesc;
import hostilelands.desc.Dice;
import hostilelands.desc.DicePattern;
import hostilelands.desc.LairDesc;
import hostilelands.desc.LevelDependant;
import hostilelands.desc.School;
import hostilelands.desc.TownDesc;
import hostilelands.desc.TroopDesc;
import hostilelands.desc.XMLLoader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;

/**
 * The main data stores. Stores all the gamedata associated to their keys.
 * 
 * @author Elscouta
 */
public class DataStore 
{
    Map<String, TerrainType> terrain_types;
    XMLStore<Dice.Symbol> symbols;
    XMLStore<DicePattern> dices;
    XMLStore<BasicUnitPattern> unit_descs;
    XMLStore<TroopDesc> troop_descs;
    XMLStore<LairDesc> lair_descs;
    XMLStore<TownDesc> town_descs;
    XMLStore<Ability> abilities;
    XMLStore<School> schools;
    XMLStore<ChampionDesc> champion_descs;

    private DataStore()
    {
        try
        {
            terrain_types = new HashMap<>();
            terrain_types.put("sea", TerrainType.SEA);
            terrain_types.put("grass", TerrainType.GRASS);
            terrain_types.put("forest", TerrainType.FOREST);
            terrain_types.put("mountain", TerrainType.MOUNTAIN);
            
            symbols = new XMLStore<Dice.Symbol>(new Dice.Symbol.Loader());
            symbols.load(Settings.SYMBOLS_PATH, "symbol", this);
        
            dices = new XMLStore<DicePattern>(new DicePattern.Loader());
            dices.load(Settings.DICES_PATH, "dice", this);
            
            unit_descs = new XMLStore<BasicUnitPattern>(new BasicUnitPattern.Loader());
            unit_descs.load(Settings.UNITS_DESC_PATH, "unit", this);
            
            troop_descs = new XMLStore<TroopDesc>(new TroopDesc.Loader());
            troop_descs.load(Settings.TROOPS_DESC_PATH, "troop", this);
            
            lair_descs = new XMLStore<LairDesc>(new LairDesc.Loader());
            lair_descs.load(Settings.LAIRS_DESC_PATH, "lair", this);
            
            town_descs = new XMLStore<TownDesc>(new TownDesc.Loader());
            town_descs.load(Settings.TOWNS_DESC_PATH, "town", this);
            
            abilities = new XMLStore<Ability>(new Ability.Loader());
            abilities.load(Settings.ABILITIES_DESC_PATH, "ability", this);

            schools = new XMLStore<School>(new School.Loader());
            schools.load(Settings.SCHOOLS_DESC_PATH, "school", this);
            
            champion_descs = new XMLStore<ChampionDesc>(new ChampionDesc.Loader());
            champion_descs.load(Settings.CHAMPIONS_DESC_PATH, "champion", this);
            
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Instantiates a new data store
     * @return the newly created store.
     */
    static public DataStore create()
    {
        return new DataStore();
    }
    
    public TerrainType getTerrainType(String identifier)
    {
        return terrain_types.get(identifier);
    }
    
    /**
     * Returns a dice, based on its identifier and the level asked.
     * @param identifier A string identifier of a dice pattern
     * @param level The level of the dice required
     * @return A dice object
     */
    public Dice getDice(String identifier, int level)
    {
        return dices.get(identifier).atLevel(level);
    }
    
    /**
     * Returns a dice pattern, based on its identifier.
     * @param identifier A string identifier of a dice pattern
     * @return A dice object
     */
    public DicePattern getDicePattern(String identifier)
    {
        return dices.get(identifier);
    }
    
    /**
     * Returns a symbol, based on its identifier
     * @param identifier A string identifier of a symbol
     * @return A symbol.
     */
    public Dice.Symbol getSymbol(String identifier)
    {
        return symbols.get(identifier);
    }
    
    /**
     * The description of a unit, that doesn't know which level the unit is
     * currently.
     * @param identifier A unit identifier
     * @return A unit description
     */
    public BasicUnitPattern getUnitPattern(String identifier)
    {
        return unit_descs.get(identifier);
    }
    
    /**
     * The description of a troop, that doesn't know which level the troop is
     * currently.
     * @param identifier A troop identifier
     * @return A troop description
     */
    public TroopDesc getTroopDesc(String identifier)
    {
        return troop_descs.get(identifier);
    }
    
    /**
     * The description of a lair, that doesn't know which level the lair is
     * currently.
     * @param identifier A lair identifier
     * @return A lair description
     */
    public LairDesc getLairDesc(String identifier)
    {
        return lair_descs.get(identifier);
    }
    
    public TownDesc getTownDesc(String identifier)
    {
        return town_descs.get(identifier);
    }
    
    /**
     * The description of an ability
     * @param identifier The identifier of an ability
     * @return An ability
     */
    public Ability getAbility(String identifier)
    {
        return abilities.get(identifier);
    }
    
    /**
     * The description of a school
     * @param identifier The identifier of a school
     * @return A school
     */
    public School getSchool(String identifier)
    {
        return schools.get(identifier);
    }
    
    /**
     * The description of a champion
     * @param identifier The identifier of a champion
     * @return A champion description
     */
    public ChampionDesc getChampionDesc(String identifier)
    {
        return champion_descs.get(identifier);
    }
    
    public <T> XMLLoader<T> getLoader(Function<String, T> method)
    {     
        class Loader extends XMLLoader<T>
        {
            @Override
            public T loadXML(Element e, DataStore unused)
            {
                String identifier = getContentAsString(e);
                return method.apply(identifier);
            }
        }
        
        return new Loader();
    }
    
    public <U, T extends LevelDependant<U>> XMLLoader<U> getLDLoader(Function<String, T> method)
    {
        class Loader extends XMLLoader<U>
        {
            @Override
            public U loadXML(Element e, DataStore unused)
            {
                String identifier = getContentAsString(e);
                int level;
                try {
                    level = getNodeAsInt("level", e);
                } catch (XPathExpressionException ex) {
                    level = 1;
                }
                return method.apply(identifier).atLevel(level);
            }
        }
        
        return new Loader();
    }
}
