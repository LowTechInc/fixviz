package com.lowtech.fixviz.ui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;

import com.lowtech.fixviz.FixTools;

import quickfix.ConfigError;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
    // FIX message text area
    private JTextArea fixMsgTextArea = new JTextArea("8=FIXT.1.1|9=464|35=8|49=BOOKING|52=20140807-04:59:00.349|56=STAR|1128=9|11=20191623|14=3000000|15=AUD|17=199e41e9-d2b8-4ca5-bd0c-e1046ca3db99|37=20191623|38=3000000|39=2|44=7.212971535|54=1|55=AUD/HKD|60=20140807-04:58:06.000|63=0|64=20140806|75=20140804|150=F|151=0|198=SY1070814-9020191623-m1|461=RCSXXX|631=0|10108=0|10109=MUREX|10110=OBO|11080=0.00|78=1|79=5154113|736=HKD|737=21638914.605|80=3000000|453=2|448=5154113|452=3|448=JWEST|452=12|711=1|311=AUD/HKD|1045=7.21297154|10=120|");

    // Control Panel
    private JButton parseButton = new JButton("Parse");
    private JTextField separatorTxt = new JTextField("|", 5); // ("\\u0001", 5);
    private JLabel dataDictionaryLabel = new JLabel("Data Dictionary=" + FixTools.DEFAULT_DATA_DICTIONARY);
    private JButton dataDictionaryButton = new JButton("Change...");
    private JFileChooser dataDictionaryChooser = new JFileChooser();
    private ButtonGroup toggleFieldTag = new ButtonGroup();
    private JRadioButton fieldTagOnButton   = new JRadioButton("Yes", true);
    private JRadioButton fieldTagOffButton    = new JRadioButton("No", false);

    // FIX message visualization area
    private JScrollPane treePanel = new JScrollPane();

    FixTools fixTools = new FixTools();

    public MainFrame(String title) throws ConfigError {
        super(title);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new GridBagLayout());

        fixMsgTextArea.setLineWrap(true);
        addComponentsToPane(getContentPane());
    }

    private void addComponentsToPane(Container pane) {
        GridBagConstraints c = new GridBagConstraints();

        final JScrollPane textPane = new JScrollPane();
        textPane.getViewport().add(fixMsgTextArea);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.ipadx = 300;
        c.ipady = 100;
        c.insets = new Insets(20,20,20,20);
        pane.add(textPane, c);

        c = new GridBagConstraints();
        JPanel controlPanel = createControlPanel();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.ipadx = 300;
        pane.add(controlPanel, c);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        c.ipadx = 300;
        c.ipady = 500;
        c.insets = new Insets(20,20,20,20);
        pane.add(treePanel, c);
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();

        parseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println(fixMsgTextArea.getText().replace(separatorTxt.getText(), String.valueOf('\u0001')));

                JTree tree = fixTools.treeify(fixMsgTextArea.getText().replace(separatorTxt.getText(), String.valueOf('\u0001')));
                tree.expandRow(0);
                treePanel.getViewport().removeAll();
                treePanel.getViewport().add(tree, null);
            }
        });
        controlPanel.add(parseButton);

        controlPanel.add(new JLabel("Separator"));
        controlPanel.add(separatorTxt);

        dataDictionaryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int returnVal =  dataDictionaryChooser.showOpenDialog(MainFrame.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    java.io.File file = dataDictionaryChooser.getSelectedFile();
                    System.out.println("File Selected :" + file.getAbsolutePath());

                    try {
                        fixTools.setDataDictionary(file.getAbsolutePath());
                        dataDictionaryLabel.setText("Data Dictionary=" + file.getName());
                    }
                    catch (ConfigError ce) {
                        System.err.println(ce);
                    }
                }
                else{
                    System.out.println("Open command cancelled by user.");
                }
            }
        });
        controlPanel.add(dataDictionaryLabel);
        controlPanel.add(dataDictionaryButton);

        fieldTagOnButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                fixTools.setShowTag(true);
            }
        });
        fieldTagOffButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                fixTools.setShowTag(false);
            }
        });
        toggleFieldTag.add(fieldTagOnButton);
        toggleFieldTag.add(fieldTagOffButton);
        controlPanel.add(new JLabel("Show Field Tag"));
        controlPanel.add(fieldTagOnButton);
        controlPanel.add(fieldTagOffButton);


        return controlPanel;
    }
}
