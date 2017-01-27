/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands.worldcreation;

import hostilelands.tools.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A class representing a dotted line, i.e a succession of positive sections
 * and negative sections.
 * 
 * @author Elscouta
 */
public class DottedLine 
{
    List<Integer> toggles;
    int length;
    
    /**
     * Constructs an empty dotted line. Private use only.
     * Elements must be added in increasing order.
     */
    protected DottedLine(int length)
    {
        this.toggles = new ArrayList<>();
        this.length = length;
    }
    
    /**
     * Adds an element into the toggles array.
     * Performs sanity checks.
     */
    private void add(int i)
    {
        assert (i >= 0);
        assert (i <= length);
        
        assert (toggles.isEmpty() || toggles.get(toggles.size() - 1) <= i);
        
        toggles.add(i);
    }
        
    /**
     * Adds a positive section to the dotted line.
     * Assumes the array is sorted.
     * @param a1 the lower bound of the section
     * @param a2 the upper bound of the section
     */
    private void addPositiveSection(int a1, int a2)
    {
        assert(a1 <= a2);
        
        int index = 0;
        boolean was_inside = false;
        while (index < toggles.size() && toggles.get(index) < a1)
        {
            index++;
            was_inside = !was_inside;
        }
        
        if (!was_inside)
        {
            toggles.add(index, a1);
            index++;
        }
        
        while (index < toggles.size() && toggles.get(index) < a2)
        {
            toggles.remove(index);
            was_inside = !was_inside;
        }
            
        if (!was_inside)
            toggles.add(index, a2);
    }
    
    /**
     * Removes redundant toggles
     */
    private void removeRedundants()
    {
        assert(toggles.size() % 2 == 0);
        
        int index = 1;
        while (index+1 < toggles.size())
        {
            assert(toggles.get(index) <= toggles.get(index+1));
            if (toggles.get(index) >= toggles.get(index+1) - 1)
            {
                toggles.remove(index+1);
                toggles.remove(index);
            }
            else
               index += 2;
        }        
    }
    
    /**
     * Constructs a dotted line from a given list of toggles.
     * @param length the length of the line
     * @param toggles a list of point where the line changes color. the toggles
     * are considered positive points. It does not need to be sorted, but it
     * will be altered by the construction process.
     */
    public DottedLine(int length, List<Integer> toggles)
    {
        assert(toggles.size() % 2 == 0);

        this.toggles = toggles;
        this.length = length;
        
        Collections.sort(toggles);
        for (Integer i : toggles)
            assert (0 <= i && i <= length);
        
        removeRedundants();
        
        assert(toggles.size() % 2 == 0);
    }
    
    /**
     * Constructs a dotted line from a given list of toggles.
     * @param length the length of the line
     * @param toggles a list of point where the line changes color. the toggles
     * are considered positive points. It does not need to be sorted, but it
     * will be altered by the construction process.
     * @param exact_matches a dotted line under an exact match representation
     * that must be merged with the current line.
     */
    public DottedLine(int length, List<Integer> toggles, List<Pair<Integer, Integer>> exact_matches)
    {
        assert(toggles.size() % 2 == 0);

        this.toggles = toggles;
        this.length = length;
        
        Collections.sort(toggles);
        for (Integer i : toggles)
            assert (0 <= i && i <= length);
                
        for (Pair<Integer, Integer> p : exact_matches)
            addPositiveSection(p.first, p.second);

        assert(toggles.size() % 2 == 0);
        for (Integer i : toggles)
            assert (0 <= i && i <= length);

        removeRedundants();
                    
        assert(toggles.size() % 2 == 0);
        for (Integer i : toggles)
            assert (0 <= i && i <= length);
    }
        
    /**
     * Returns the length of the line.
     * @return The length of the line. This should be an even number.
     */
    public int getLength()
    {
        return length;
    }
    
    /**
     * Returns whether the edge is completely empty. An empty edge must
     * have no toggles.
     * @return true if there is no positive section on the line.
     */
    public boolean isEmpty()
    {
        return toggles.isEmpty();
    }
    
    /**
     * Returns whether a pixel is positive or negative. Expensive.
     * @param pixel The id of the pixel
     * @return true if the pixel is inside the dotted line, false otherwise.
     */
    public boolean getPixel(int pixel)
    {
        assert(toggles.size() % 2 == 0);
        
        int index = 0;
        while (index < toggles.size() && toggles.get(index) < pixel)
            index++;
                
        if (index < toggles.size() && toggles.get(index) == pixel)
            return true;
        
        return index % 2 == 1;
    }
    
    /**
     * Sets the value of a pixel. Very expensive. 
     * This function assumes there is no redundancies in the description
     * (which should always be the case)
     * 
     * It doesn't create redundancies.
     * 
     * @param pixel the id of the pixel
     * @param value the new value of the pixel.
     */
    public void setPixel(int pixel, boolean value)
    {
        assert(toggles.size() % 2 == 0);
        
        int index = 0;
        while (index < toggles.size() && toggles.get(index) < pixel)
            index++;
            
        if (value == true && index % 2 == 0)
        {
            // The pixel is an exact match
            if (index < toggles.size() && toggles.get(index) == pixel)
                return;
            
            // We must merge the two sections
            if (index < toggles.size() && toggles.get(index) == pixel+1 &&
                index > 0 && toggles.get(index-1) == pixel-1)
            {
                toggles.remove(index);
                toggles.remove(index-1);
                return;
            }
            
            // We need to enlarge the next section.
            if (index < toggles.size() && toggles.get(index) == pixel+1)
            {
                toggles.set(index, pixel);
                return;
            }
            
            // We need to enlarge the previous section.
            if (index > 0 && toggles.get(index-1) == pixel-1)
            {
                toggles.set(index-1, pixel);
                return;
            }
            
            // We need to create a singleton pixel.
            toggles.add(index, pixel);
            toggles.add(index, pixel);
        }
        
        // We are finished
        if (value == false && index == toggles.size())
            return;
        
        // Exact match
        if (value == false && toggles.get(index) == pixel)
        {
            if (index % 2 == 0)
            {
                // We are starting a new section. Reduce it, and
                // make it disappear if singleton.
                if (toggles.get(index+1) == pixel)
                {
                    toggles.remove(index);
                    toggles.remove(index);
                }
                else
                    toggles.set(index, pixel+1);
            }
            else
            {
                // We are finishing a section. Reduce it, and
                // make it disappear if singleton.
                if (toggles.get(index-1) == pixel)
                {
                    toggles.remove(index);
                    toggles.remove(index-1);
                }
                else
                    toggles.set(index, pixel-1);
            }
            
            return;
        }
        
        // We are inside a section
        if (value == false && index % 2 == 1)
        {
            assert(pixel > 0 && pixel < length);
            toggles.add(index, pixel+1);
            toggles.add(index, pixel-1);
            return;
        }
    }
            
    
    /**
     * Returns an iterator over the toggling points.
     * @return an iterator over the toggling points.
     */
    public Iterator<Integer> getToggleIterator()
    {
        return toggles.iterator();
    }
    
    
    /**
     * Splits the dotted line into two parts. The last pixel of the first part
     * is guaranteed to correspond to the first pixel of the second part.
     * @return A pair (first part, second part) of dotted lines.
     */
    public Pair<DottedLine, DottedLine> split()
    {
        assert(length % 2 == 0);
        
        Pair<DottedLine, DottedLine> p = new Pair<>();
        p.first = new DottedLine(length / 2);
        p.second = new DottedLine(length / 2);
        
        int index = 0;
        while (index < toggles.size() && toggles.get(index) < length / 2)
        {
            p.first.add(toggles.get(index));
            index++;
        }

        if (index % 2 == 1)
            p.second.add(0);

        while (index < toggles.size() && toggles.get(index) == length / 2)
        {
            p.first.add(toggles.get(index));
            p.second.add(toggles.get(index) - length / 2);
            index++;
        }
        
        if (index % 2 == 1)
            p.first.add(length / 2);

        while (index < toggles.size())
        {
            p.second.add(toggles.get(index) - length / 2);
            index++;
        }
        
        return p;
    }
    
    /**
     * Reverses the dotted line. 
     * @return A new object, representing the reversed dotted line.
     */
    public DottedLine reverse()
    {
        DottedLine reversedLine = new DottedLine(length);
        int i = toggles.size() - 1;
        while (i >= 0)
        {
            reversedLine.add(length - toggles.get(i));
            i--;
        }
        
        return reversedLine;
    }
    
    /**
     * Adds (merge) another dotted line. Both lines must have the same length.
     * @param other The dotted line to merge
     * @return new dotted line 
     */
    public DottedLine binaryOr(DottedLine other)
    {
        assert (other.getLength() == length);
        DottedLine ret = new DottedLine(length);
        
        int myPos = 0;
        int otherPos = 0;
        int retPos = 0;
        while (myPos < toggles.size() || otherPos < other.toggles.size())
        {
            int newToggle;
            if (otherPos == other.toggles.size() || 
                (myPos < toggles.size() && toggles.get(myPos) < other.toggles.get(otherPos)))
            {
                newToggle = toggles.get(myPos);
                myPos++;
            }
            else
            {
                newToggle = other.toggles.get(otherPos);
                otherPos++;
            }
            
            boolean currentState = retPos % 2 != 0;
            boolean newState = otherPos % 2 != 0 || myPos % 2 != 0;
            
            if (newState != currentState)
            {
                retPos++;
                ret.toggles.add(newToggle);
            }
        }
   
        ret.removeRedundants();
        return ret;
    }
    
    /**
     * Filter with another dotted line. Both lines must have the same length.
     * @param other Another dotted line.
     * @return new dotted line 
     */
    public DottedLine binaryAnd(DottedLine other)
    {
        assert (other.getLength() == length);
        DottedLine ret = new DottedLine(length);
        
        int myPos = 0;
        int otherPos = 0;
        int retPos = 0;
        while (myPos < toggles.size() || otherPos < other.toggles.size())
        {
            int newToggle;
            if (otherPos == other.toggles.size() || 
                (myPos < toggles.size() && toggles.get(myPos) < other.toggles.get(otherPos)))
            {
                newToggle = toggles.get(myPos);
                myPos++;
            }
            else
            {
                newToggle = other.toggles.get(otherPos);
                otherPos++;
            }
            
            boolean currentState = retPos % 2 != 0;
            boolean newState = otherPos % 2 != 0 && myPos % 2 != 0;
            
            if (newState != currentState)
            {
                retPos++;
                ret.toggles.add(newToggle);
            }
        }
   
        ret.removeRedundants();
        return ret;
    }
    
    /**
     * 
     */
    public DottedLine binaryMinus(DottedLine other)
    {
        return binaryAnd(other.negate());
    }
    
    /**
     * Returns the negation of this current dotted line.
     */
    public DottedLine negate()
    {
        DottedLine ret = new DottedLine(length);
        
        if (toggles.size() == 0)
        {
            ret.toggles.add(0);
            ret.toggles.add(length);
            return ret;
        }
        
        if (toggles.get(0) != 0)
        {
            ret.toggles.add(0);
            ret.toggles.add(toggles.get(0)-1);
        }
        
        int i = 1;
        while (i < toggles.size()-1)
        {
            // No redundancies assertion.
            assert(toggles.get(i) != 0 && toggles.get(i) != length);

            if (i % 2 == 0)
                ret.toggles.add(toggles.get(i)-1);
            else
                ret.toggles.add(toggles.get(i)+1);
        
            i++;
        }
        
        if (toggles.get(i) != length)
        {
            ret.toggles.add(toggles.get(i)+1);
            ret.toggles.add(length);
        }
        
        return ret;
    }
    
    @Override
    public String toString()
    {
        String s = new String();

        s += "[";
        for (int i = 0; i < toggles.size(); i++)
        {
            s += toggles.get(i);
            if (i < toggles.size()-1)
                s += ",";
        }
        s += "]";

        return s;
    }
    
    /**
     * Returns the ratio of positive pixels over the total
     * 
     * @return the ratio of positive pixels
     */
    public double getRatio()
    {
        int positives = 0;
        
        for (int i = 0; i < toggles.size(); i += 2)
            positives += (toggles.get(i+1) - toggles.get(i) + 1);
            
        return (double) positives / (double) length;
    }
}
