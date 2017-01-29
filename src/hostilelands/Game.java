/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.champion.Champion;
import hostilelands.desc.LairDesc;
import hostilelands.events.GameEvent;
import hostilelands.dialogs.DialogDesc;
import java.util.*;

import hostilelands.models.IntegerModel;
import hostilelands.models.SingletonModel;
import hostilelands.views.UIMgr;
import javax.swing.SwingUtilities;
import javax.swing.ListModel;
import javax.swing.DefaultListModel;

/**
 * Created in the event dispatch thread. Safe to access in any thread, as any
 * calls will be redirected to the appropriate thread.
 *
 * @author Elscouta
 */
public final class Game 
{
    World world;
    MainParty party;
    
    final ShortTimeMgr shortTimeMgr;
    final UIMgr uiMgr;
    final DataStore dataStore;
    
    final SingletonModel<DialogDesc> dialogEvent;
    final SingletonModel<Combat> combatEvent;
    final List<GameEvent> eventQueue;
    boolean paused;
    
    final IntegerModel time = new IntegerModel();
    
    /**
     * Constructs an empty game. The world and the player party must be
     * created independently afterwards.
     */
    public Game()
    {
        this.shortTimeMgr = ShortTimeMgr.create();
        this.world = null;
        this.party = null;
        this.uiMgr = new UIMgr();
        this.dataStore = DataStore.create();
        this.dialogEvent = uiMgr.getDialogModel();
        this.combatEvent = new SingletonModel<>();
        this.eventQueue = new ArrayList<>();
        
        shortTimeMgr.addEntity(new Clock());
    }
    
    /**
     * Generates the world.
     */
    public void createWorld()
    {
        int attempt = 0;
        while (true)
        {
            attempt++;
            
            try {
                world = new World(this, shortTimeMgr, 4);
                world.generate();
                return;
            } catch (World.GenerationFailure e) {
                System.err.printf("World generation failed (%s). Retrying... (attempt %d)\n", e, attempt);
            }
        }
    }
    
    /**
     * Initializes the party, and places it into the world.
     * 
     * World must have been created before.
     */
    public void createParty()
    {
        assert(this.world != null);
        this.party = new MainParty();
                
        party.addUnit(new Champion(dataStore.getChampionDesc("sintel")));
        party.addUnit(new Champion(dataStore.getChampionDesc("chaman")));

        world.partyEnters(party);

        shortTimeMgr.addEntity(party);        
    }
    
    /**
     * Creates the UI and sets it visible.
     */
    public void createUI()
    {
        uiMgr.createUI(this);
    }
    
    /**
     * Party represents the current party of heroes that the player is controlling
     * It is safe to keep references or subscribe to it from any thread.
     * 
     * @return The current party. 
     */
    public MainParty getParty()
    {
        return party;
    }
    
    /**
     * Returns the UIMgr. This modules handles most of the UI creation and updates.
     * 
     * @return the UI manager
     */
    public UIMgr getUIMgr()
    {
        return uiMgr;
    }
    
    /**
     * World is the main data class of the game. It is safe to keep references
     * or subscribe to the world from any thread.
     * 
     * @return The current world.
     */
    public World getWorld()
    {
        return world;
    }
    
    /**
     * Returns the main data store for the game.
     * 
     * @return The main data store
     */
    public DataStore getDataStore()
    {
        return dataStore;
    }
    
    /**
     * It is safe to keep references or subscribe to the time model from
     * any thread.
     * 
     * @return The current world time. 
     */
    public IntegerModel getTime()
    {
        return time;
    }
    

    /**
     * This should only be called by UI elements that wish to subscribe to 
     * this model. 
     * 
     * @return The combat event. If non-null, it should be displayed by the
     * combat layer.
     */
    public SingletonModel<Combat> getCombatModel()
    {
        return combatEvent;
    }
    
    
    /**
     * Fires an event. If an event is currently being processed, it will be
     * added to the queue for future handling.
     * 
     * Events that wish to alter the UI significantly should pause the game
     * so that the player has time to react. While the game is paused, no
     * other events can be processed. 
     * 
     * This function can be called from any thread. If you wish to make an
     * event as a followup of a current pausing event, and you are in the event
     * thread, use fireImmediateEvent.
     * 
     * FIXME: Should queue events in case of multiples happening at the same 
     * time.
     * 
     * @param e The event to be fired. 
     */
    public synchronized void fireEvent(GameEvent e)
    {
        final Game g = this;
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                pause();
                fireImmediateEvent(e);
            }
        });
    }
    
    /**
     * Fires an event immediately, as a followup of a pausing event.
     * This should only be called from the event dispatch thread itself,
     * and when the game is already paused to process an event.
     * 
     * @param e The event to be fired. 
     */
    private synchronized void fireImmediateEvent(GameEvent e)
    {
        assert(SwingUtilities.isEventDispatchThread());
        assert(isPaused());
        
        e.run(this);
    }
    
    /**
     * Adds a dialog to the current dialog singleton, to be displayed at the 
     * earliest possible opportunity.
     * 
     * Should only be called by GameEvents or from the UI, while the game
     * is paused, and if no dialog is currently being displayed.
     * 
     * This is combatible with being in combat, but in that case,
     * be aware that the dialog must be closed before the combat is.
     * 
     * @param d The dialog to be displayed.
     */
    public synchronized void openDialog(DialogDesc d)
    {
        assert(isPaused());
        assert(dialogEvent.exists() == false);
        dialogEvent.set(d);
    }
    
    /**
     * Closes the current dialog. 
     * 
     * This should only be called by the UI itself, while the game is paused
     * in order to process a dialog.
     * 
     * @param after The event to be processed as a result of dialog closing, or
     * null if the game can be resumed. Don't use null if we are in combat!
     */
    public synchronized void closeDialog(GameEvent after)
    {
        assert(isPaused());
        dialogEvent.suppress();
        
        if (after != null)
        {
            fireImmediateEvent(after);
        }
        else
        {
            assert (!combatEvent.exists());
            resume();
        }
    }
    
    /**
     * Opens a combat panel.
     * 
     * Should only be called by GameEvents or from the UI, while the game
     * is paused, and if no dialog/combat is currently being displayed.
     * 
     * @param c The combat to do.
     */
    public synchronized void startCombat(Combat c)
    {
        assert(isPaused());
        assert(!dialogEvent.exists() && !combatEvent.exists());
                
        combatEvent.set(c);
    }
    
    /**
     * Closes the combat panel.
     * 
     * This should only be called by the UI itself, while the game is paused
     * in order to process a combat. No dialog must be open at that stage.
     * 
     * @param after The event to be processed as a result of combat ending, or
     * null if the game can be resumed.
     */
    public synchronized void endCombat(GameEvent after)
    {
        assert(isPaused());
        assert(!dialogEvent.exists() && combatEvent.exists());
        combatEvent.suppress();
        
        if (after != null)
            fireImmediateEvent(after);
        else
            resume();
    }
    
    /**
     * 
     * @return Whether the game is currently being paused 
     */
    private synchronized boolean isPaused()
    {
        return paused;
    }
    
    /**
     * Pauses the game. This should only be called if the game is not 
     * already paused, in response to a pausing event.
     */
    private synchronized void pause()
    {
        assert(!isPaused());
    
        shortTimeMgr.pause();
        paused = true;
    }
    
    /**
     * Resumes the game. This should only be called if the game is currently
     * paused in response to an event.
     * 
     * This is done automatically by the game thanks to ResumeEvent class that
     * is automatically appended to the root event.
     */
    private synchronized void resume()
    {
        assert(isPaused());
        
        shortTimeMgr.unpause();
        paused = false;
    }
    
    /**
     * A simple clock, that just observes the passing of time. 
     */
    private class Clock implements ShortTimeEntity
    {
        @Override
        public void simulateTime(int minutes)
        {
            time.add(minutes);
        }
    }
}
