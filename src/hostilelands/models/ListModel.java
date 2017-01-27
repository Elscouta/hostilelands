/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.models;

import javax.swing.DefaultListModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Elscouta
 */
public class ListModel<T> 
        extends java.util.AbstractList<T>
        implements javax.swing.ListModel<T>
                       
{
    private final DefaultListModel<T> me;
    
    public ListModel()
    {
        me = new DefaultListModel<>();
    }
    
    @Override
    public int getSize() 
    {
        return me.getSize();
    }

    @Override
    public T getElementAt(int index) 
    {
        return me.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) 
    {
        me.addListDataListener(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) 
    {
        me.removeListDataListener(l);
    }

    @Override
    public int size() 
    {
        return me.size();
    }

    @Override
    public T get(int index) 
    {
        return me.get(index);
    }

    @Override
    public T set(int index, T element) 
    {
        return me.set(index, element);
    }

    @Override
    public void add(int index, T element) 
    {
        me.add(index, element);
    }

    @Override
    public T remove(int index) 
    {
        return me.remove(index);
    }
}
