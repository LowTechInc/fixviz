package com.lowtech.fixviz;

import quickfix.ConfigError;

import com.lowtech.fixviz.controller.FixTools;
import com.lowtech.fixviz.model.FixString;
import com.lowtech.fixviz.view.MainFrame;

public class Bootstrap {

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() throws ConfigError {
    	// Create model, view and controller
    	String fixStr = "8=FIXT.1.1|9=464|35=8|49=BOOKING|52=20140807-04:59:00.349|56=STAR|1128=9|11=20191623|14=3000000|15=AUD|17=199e41e9-d2b8-4ca5-bd0c-e1046ca3db99|37=20191623|38=3000000|39=2|44=7.212971535|54=1|55=AUD/HKD|60=20140807-04:58:06.000|63=0|64=20140806|75=20140804|150=F|151=0|198=SY1070814-9020191623-m1|461=RCSXXX|631=0|10108=0|10109=MUREX|10110=OBO|11080=0.00|78=1|79=5154113|736=HKD|737=21638914.605|80=3000000|453=2|448=5154113|452=3|448=JWEST|452=12|711=1|311=AUD/HKD|1045=7.21297154|10=120|";
    	FixString fixMsg = new FixString(fixStr);
    	FixTools fixTools = new FixTools();
        MainFrame frame = new MainFrame(fixTools, fixMsg);

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
                	e.printStackTrace();
                    System.err.println(e);
                }
            }
        });
    }
}
