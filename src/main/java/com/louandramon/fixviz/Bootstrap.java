package com.louandramon.fixviz;

import com.louandramon.fixviz.ui.MainFrame;

import quickfix.ConfigError;

public class Bootstrap {

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() throws ConfigError {
        //Create and set up the window.
        MainFrame frame = new MainFrame("FIX Visualization");

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    createAndShowGUI();
                }
                catch (Exception e) {
                    System.err.println(e);
                }
            }
        });
    }
}
