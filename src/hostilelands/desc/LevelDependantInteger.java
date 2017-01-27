/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.desc;

import hostilelands.DataStore;
import hostilelands.Settings;
import java.io.IOException;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;

/**
 *
 * @author Elscouta
 */
public class LevelDependantInteger implements LevelDependant<Integer>
{
    private final double base;
    private final double ratio;

    public LevelDependantInteger(double base, double ratio)
    {
        this.base = base;
        this.ratio = ratio;
    }
    
    @Override
    public Integer atLevel(int level)
    {
        return (int) (base + level * ratio + Settings.FLOAT_PRECISION);
    }
    
    public static class Identity extends LevelDependantInteger
    {
        public Identity()
        {
            super(0.0f, 1.0f);
        }
        
        @Override
        public Integer atLevel(int level)
        {
            return level;
        }
    }
    
    public static class Loader extends XMLLoader<LevelDependantInteger>
    {

        @Override
        public LevelDependantInteger loadXML(Element e, DataStore store) 
                throws XPathExpressionException, IOException 
        {
            double base = getNodeAsDouble("base", e);
            double ratio = getNodeAsDouble("per-level", e);
            return new LevelDependantInteger(base, ratio);
        }        
    }
}
