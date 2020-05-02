/*
 * Created by JFormDesigner on Sat May 02 17:15:10 CEST 2020
 */

package de.gravitex.bpm.executor.gui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import de.gravitex.bpm.executor.app.BpmDefinition;
import de.gravitex.bpm.executor.app.BpmExecutionSingleton;
import de.gravitex.bpm.executor.app.ProcessExecutor;
import de.gravitex.bpm.executor.checker.TaskT1BpmChecker;
import de.gravitex.bpm.executor.checker.TaskTxBpmChecker;
import de.gravitex.bpm.executor.exception.BpmExecutorException;
import de.gravitex.bpm.executor.handler.TaskT1Handler;
import de.gravitex.bpm.executor.handler.TaskTMHandler;

/**
 * @author Stefan Schulz
 */
public class BpmGui extends JFrame {

	private static final long serialVersionUID = -8277137714910541545L;

	public BpmGui() {
		initComponents();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fillDefinitions();
		btnStartProcess.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String identifier = ((BpmDefinition) bpmDefinitionList.getSelectedValue()).getProcessDefinitionKey();
				try {
					BpmExecutionSingleton.getInstance().startProcess(identifier);
				} catch (BpmExecutorException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fillInstances();
			}
		});
	}
	
	private void fillInstances() {
		DefaultTableModel model = new DefaultTableModel();
		model.setColumnIdentifiers(new Object[] {"Business Key", "Definition", "Status"});
		for (ProcessExecutor processExecutor : BpmExecutionSingleton.getInstance().getProcessExecutors()) {
			Object[] row = new Object[3];
			row[0] = processExecutor.getBusinessKey();
			row[1] = processExecutor.getBpmDefinition().getProcessDefinitionKey();
			row[2] = processExecutor.getProcessExecutorState();
			model.addRow(row);	
		}
		executionsTable.setModel(model);
	}

	private void fillDefinitions() {
		
	    final DefaultListModel model = new DefaultListModel();
	    for (BpmDefinition bpmDefinition : BpmExecutionSingleton.getInstance().getBpmDefinitions()) {
	      model.addElement(bpmDefinition);
	    }
		bpmDefinitionList.setModel(model);
	}

	public static void main(String[] args) {
		try {
			
			BpmExecutionSingleton.getInstance().registerProcessDefinition("SimpleTestProcess",
					new BpmDefinition(null, "SimpleTestProcess.bpmn", "SimpleTestProcess").withCustomHandler("TASK#T1", new TaskT1Handler())
							.withBpmStateChecker("TASK#T1", new TaskT1BpmChecker()));
			BpmExecutionSingleton.getInstance().registerProcessDefinition("AnotherProcess",
					new BpmDefinition(null, "AnotherProcess.bpmn", "AnotherProcess").withBpmStateChecker("TASK#TX", new TaskTxBpmChecker()));
			BpmExecutionSingleton.getInstance().registerProcessDefinition("LoopProcess",
					new BpmDefinition(null, "LoopProcess.bpmn", "LoopProcess").withCustomHandler("TASK#TM", new TaskTMHandler()));

			BpmGui bpmGui = new BpmGui();
			bpmGui.setSize(900, 600);
			bpmGui.setVisible(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Stefan Schulz
		scrollPane1 = new JScrollPane();
		bpmDefinitionList = new JList();
		scrollPane2 = new JScrollPane();
		executionsTable = new JTable();
		btnStartProcess = new JButton();
		btnRefresh = new JButton();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {199, 0, 0};
		((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 0, 0};
		((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
		((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(bpmDefinitionList);
		}
		contentPane.add(scrollPane1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));

		//======== scrollPane2 ========
		{
			scrollPane2.setViewportView(executionsTable);
		}
		contentPane.add(scrollPane2, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//---- btnStartProcess ----
		btnStartProcess.setText("Start process");
		contentPane.add(btnStartProcess, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 5), 0, 0));

		//---- btnRefresh ----
		btnRefresh.setText("Aktualisieren");
		contentPane.add(btnRefresh, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Stefan Schulz
	private JScrollPane scrollPane1;
	private JList bpmDefinitionList;
	private JScrollPane scrollPane2;
	private JTable executionsTable;
	private JButton btnStartProcess;
	private JButton btnRefresh;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
