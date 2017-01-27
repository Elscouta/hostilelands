/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.champion.Champion;
import hostilelands.models.ListModel;
import java.util.*;

/**
 *
 * @author Elscouta
 */
public interface ModifiableTeam extends Team
{
    public void addUnit(Unit c);

    public void removeUnit(Unit c);

    public boolean canAddUnit(Unit c) 
            throws FailedRequirementException, NoSuchElementException;

    public boolean canRemoveUnit(Unit c) 
            throws FailedRequirementException, NoSuchElementException;

    @Override
    public ListModel<Unit> getUnits();
}
