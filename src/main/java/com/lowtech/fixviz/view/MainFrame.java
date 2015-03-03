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

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;

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
	private JButton addEntryButton = new JButton("add Entry");
	private final String newEntryValue = "1776=new_entry";

	// FIX message visualization area
	private JScrollPane treePanel = new JScrollPane();

	// Model and view
	private FixTools controller;
	private FixString model;
	private JTree tree;
	private DefaultTreeModel treeModel;

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

				model = new FixString(fixMsgTextArea.getText());
				
				treeInit();
				treeModel = (DefaultTreeModel) tree.getModel();
				treeModel.addTreeModelListener(new TreeModelListener(){

					public void treeNodesChanged(TreeModelEvent e) {
						
						DefaultMutableTreeNode node;
			            node = (DefaultMutableTreeNode)
			            (e.getTreePath().getLastPathComponent());
			            
			            try {
			                int index = e.getChildIndices()[0];
			                node = (DefaultMutableTreeNode)
			                (node.getChildAt(index));
			                controller.valueChanged(node.toString(), model);
			                fixMsgTextArea.setText(model.getFixStr());
			                
			            } catch (NullPointerException exc) {}
						
					}

					public void treeNodesInserted(TreeModelEvent arg0) {
						// TODO working on this
						controller.addNewValue(newEntryValue, model);
						fixMsgTextArea.setText(model.getFixStr());
						//treeInit();
						
					}

					public void treeNodesRemoved(TreeModelEvent arg0) {
						// TODO need a choice
						
					}

					/** no function designed for this part*/
					public void treeStructureChanged(TreeModelEvent arg0) {}
				});
			}
		});
		controlPanel.add(parseButton);
		
		addEntryButton.addActionListener(new ActionListener(){
			//TODO add new sibling entry
			public void actionPerformed(ActionEvent e) {
				//get selected node
                DefaultMutableTreeNode selectedNode  
                    = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();  
                //if no node selected, quit
                if (selectedNode == null) return;  
                //get parent node of the selected
                DefaultMutableTreeNode parent  
                    = (DefaultMutableTreeNode)selectedNode.getParent();  
                //if no parent node, quit  
                if (parent == null) return;  
                //create a new node 
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newEntryValue);  
                //get the index number of the selected node
                int selectedIndex = parent.getIndex(selectedNode);  
                //insert the new node next to the selected one
                //treeModel = (DefaultTreeModel) tree.getModel();
                treeModel.insertNodeInto(newNode, parent, selectedIndex + 1);  
				
			}
			
		});
		controlPanel.add(addEntryButton);

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
	
	private void treeInit(){
		tree = controller.treeify(model);
		tree.expandRow(0);
		treePanel.getViewport().removeAll();
		treePanel.getViewport().add(tree, null);
		tree.setEditable(true);
		tree.addTreeSelectionListener(new TreeSelectionListener(){

			public void valueChanged(TreeSelectionEvent e) {
				// TODO link the origin value to controller
				DefaultMutableTreeNode node = 
						(DefaultMutableTreeNode) e.getPath().getLastPathComponent();
				//System.out.println(node.toString());
				controller.selected(node.toString());
			}
		});
	}
}