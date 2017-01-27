/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

/**
 *
 * @author Elscouta
 */
public final class BasicMapObject extends MapObject
{
    final String identifier;
    
    public BasicMapObject(String identifier)
    {
        this.identifier = identifier;
    }
    
    @Override
    public String getIconIdentifier()
    {
        return identifier;
    }
    
}
