/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hostilelands;

import hostilelands.views.MainWindow;

/**
 *
 * @author Elscouta
 */
public class HostileLands {
    Game game;
    
    private HostileLands()
    {
        game = new Game();
    }
    
    private void run()
    {
        game.createWorld();
        game.createParty();
        game.createUI();
    }
    
    static HostileLands app;
       
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        app = new HostileLands();
        app.run();
    }
    
}
