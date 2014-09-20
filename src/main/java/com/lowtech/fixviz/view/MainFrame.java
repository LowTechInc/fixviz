package com.lowtech.fixviz.view;

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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import quickfix.ConfigError;

import com.lowtech.fixviz.controller.FixTools;
import com.lowtech.fixviz.model.FixString;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	// FIX message text area
	private JTextArea fixMsgTextArea;

	// Control Panel
	private JButton parseButton = new JButton("Parse");
	private JTextField separatorTxt;
	private JLabel dataDictionaryLabel = new JLabel("Data Dictionary="
			+ FixString.DEFAULT_DATA_DICTIONARY);
	private JButton dataDictionaryButton = new JButton("Change...");
	private JFileChooser dataDictionaryChooser = new JFileChooser();
	private ButtonGroup toggleFieldTag = new ButtonGroup();
	private JRadioButton fieldTagOnButton = new JRadioButton("Yes", true);
	private JRadioButton fieldTagOffButton = new JRadioButton("No", false);

	// FIX message visualization area
	private JScrollPane treePanel = new JScrollPane();

	// Model and view
	private FixTools controller;
	private FixString model;

	public MainFrame(FixTools controller, FixString model) throws ConfigError {
		super("FIX Visualization");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new GridBagLayout());

		separatorTxt = new JTextField(model.getSeparator(), 5);

		fixMsgTextArea = new JTextArea(model.getFixStr());
		fixMsgTextArea.setLineWrap(true);
		addComponentsToPane(getContentPane());

		this.controller = controller;
		this.model = model;
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
		c.insets = new Insets(20, 20, 20, 20);
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
		c.insets = new Insets(20, 20, 20, 20);
		pane.add(treePanel, c);
	}

	private JPanel createControlPanel() {
		JPanel controlPanel = new JPanel();

		parseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(fixMsgTextArea.getText().replace(
						separatorTxt.getText(), String.valueOf('\u0001')));

				JTree tree = controller.treeify(model);
				tree.expandRow(0);
				treePanel.getViewport().removeAll();
				treePanel.getViewport().add(tree, null);
			}
		});
		controlPanel.add(parseButton);

		controlPanel.add(new JLabel("Separator"));
		separatorTxt.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				model.setSeparator(separatorTxt.getText());
			}

			public void removeUpdate(DocumentEvent e) {
				model.setSeparator(separatorTxt.getText());
			}

			public void insertUpdate(DocumentEvent e) {
				model.setSeparator(separatorTxt.getText());
			}
		});
		controlPanel.add(separatorTxt);

		dataDictionaryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = dataDictionaryChooser
						.showOpenDialog(MainFrame.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					java.io.File file = dataDictionaryChooser.getSelectedFile();
					System.out.println("File Selected :"
							+ file.getAbsolutePath());

					try {
						model.setDictionary(file.getAbsolutePath());
						dataDictionaryLabel.setText("Data Dictionary="
								+ file.getName());
					} catch (ConfigError ce) {
						System.err.println(ce);
					}
				} else {
					System.out.println("Open command cancelled by user.");
				}
			}
		});
		controlPanel.add(dataDictionaryLabel);
		controlPanel.add(dataDictionaryButton);

		fieldTagOnButton.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				controller.setShowTag(true);
			}
		});
		fieldTagOffButton.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				controller.setShowTag(false);
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