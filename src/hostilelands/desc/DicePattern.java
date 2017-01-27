/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.desc;

import hostilelands.DataStore;
import hostilelands.Identifiable;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;

/**
 *
 * @author Elscouta
 */
public class DicePattern implements Identifiable, LevelDependant<Dice>
{
    private static class FacePattern implements LevelDependant<Dice.Face>
    {
        private final LevelDependantList<Dice.Symbol> symbols;
        
        private FacePattern(LevelDependantList<Dice.Symbol> symbols)
        {
            this.symbols = symbols;
        }

        public static class Loader extends XMLLoader<FacePattern>
        {
            @Override
            public FacePattern loadXML(Element e, DataStore store) 
                    throws IOException, XPathExpressionException
            {
                LevelDependantList<Dice.Symbol> symbols = 
                        getChildsAsObjectLDListFromStore(e, store, s -> new LevelConstant(store.getSymbol(s)));
                return new FacePattern(symbols);
            }
        }
        
        @Override
        public Dice.Face atLevel(int level)
        {
            return new Dice.Face(symbols.atLevel(level));
        }
    }

    private final List<FacePattern> faces;
    private final String identifier;
    
    private DicePattern(String identifier, List<FacePattern> faces)
    {
        this.identifier = identifier;
        this.faces = faces;
    }
    
    @Override
    public String getIdentifier()
    {
        return identifier;
    }
    
    @Override
    public Dice atLevel(int level)
    {
        List<Dice.Face> ifaces = 
                faces.stream().map(f -> f.atLevel(level)).collect(Collectors.toList());
        return new Dice(String.format("%s (%d)", identifier, level), ifaces);
    }
    
    public static class Loader extends XMLLoader<DicePattern>
    {
        @Override
        public DicePattern loadXML(Element e, DataStore store) 
                throws IOException, XPathExpressionException
        {
            String identifier = getAttributeAsString("identifier", e);
            List<FacePattern> faces = 
                    getNodesAsObjectList("face", e, new FacePattern.Loader(), store);
            return new DicePattern(identifier, faces);
        }        
    }
    
}
