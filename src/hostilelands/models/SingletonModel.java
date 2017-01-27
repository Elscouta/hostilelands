/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.models;

/**
 *
 * @author Elscouta
 */
public class SingletonModel<T> extends ChangeEventSource
{
    T obj;
    
    public SingletonModel()
    {
    }
    
    public synchronized void set(T obj)
    {
        assert(obj != null);
        this.obj = obj;
        
        fireStateChanged();
    }
    
    public synchronized void suppress()
    {
        assert(exists());
        this.obj = null;
        
        fireStateChanged();
    }
    
    public synchronized boolean exists()
    {
        return obj != null;
    }
    
    public synchronized T get()
    {
        return obj;
    }
}
