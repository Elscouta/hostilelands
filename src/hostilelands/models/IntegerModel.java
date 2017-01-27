/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.models;

/**
 * Model storing a single Integer
 * Fully synchronized.
 * 
 * @author Elscouta
 */
public class IntegerModel extends ChangeEventSource
{
    int value = 0;
    
    public synchronized void setValue(int v)
    {
        value = v;
        fireStateChanged();
    }
    
    public synchronized int getValue()
    {
        return value;
    }
    
    public synchronized void add(int i)
    {
        value = value + i;
        fireStateChanged();
    }
}
