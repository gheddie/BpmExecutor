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
import java.util.List;
import javax.swing.*;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import de.gravitex.bpm.executor.app.BpmDefinition;
import de.gravitex.bpm.executor.app.BpmExecutionSingleton;
import de.gravitex.bpm.executor.app.ProcessExecutor;
import de.gravitex.bpm.executor.checker.TaskT1BpmChecker;
import de.gravitex.bpm.executor.checker.TaskTxBpmChecker;
import de.gravitex.bpm.executor.exception.BpmExecutorException;
import de.gravitex.bpm.executor.handler.TaskT1Handler;
import de.gravitex.bpm.executor.handler.TaskTMHandler;
import de.gravitex.bpm.executor.handler.start.CollaborationTestProcessStartHandler;
import de.gravitex.bpm.executor.settings.ProcessExecutorSettings;
import de.gravitex.bpm.executor.util.StringUtil;

/**
 * @author Stefan Schulz
 */
public class BpmGui extends JFrame {

	private static final Logger logger = Logger.getLogger(BpmGui.class);

	private static final long serialVersionUID = -8277137714910541545L;

	public BpmGui() {
		initComponents();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		bpmDefinitionList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		executionsTable.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		executionsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (executionsTable.getSelectedRow() < 0) {
					return;
				}
				String processInstanceId = (String) executionsTable.getValueAt(executionsTable.getSelectedRow(), 0);
				logger.info("changed to process instance id '" + processInstanceId + "'...");
				fillMessages(processInstanceId);
				scrollMessagesToBottom();
			}

			private void scrollMessagesToBottom() {
				JScrollBar verticalScrollBar = scExecutionsTable.getVerticalScrollBar();
				verticalScrollBar.setValue(verticalScrollBar.getMaximum());
			}
		});
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
				fillInstances();
			}
		});
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
	}

	private void refresh() {
		fillInstances();
		fillCommonMessages();
	}

	private void fillMessages(String processInstanceId) {
		DefaultTableModel model = new DefaultTableModel();
		model.setColumnIdentifiers(new Object[] { "Text" });
		List<String> messages = BpmExecutionSingleton.getInstance().getMessages(processInstanceId);
		if (messages != null) {
			for (String message : messages) {
				Object[] row = new Object[1];
				row[0] = message;
				model.addRow(row);
			}
		}
		lblProcessPath.setText(StringUtil.formatList(BpmExecutionSingleton.getInstance().getProcessPath(processInstanceId)));
		messagesTable.setModel(model);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void fillCommonMessages() {
		// fill model
		final DefaultListModel model = new DefaultListModel();
		for (String message : BpmExecutionSingleton.getInstance().getCommonMessages()) {
			model.addElement(message);
		}
		commonMessagesList.setModel(model);
	}

	private void fillInstances() {
		DefaultTableModel model = new DefaultTableModel();
		model.setColumnIdentifiers(new Object[] { "ID", "Startdatum", "Business Key", "Definition", "Status", "Activity" });
		for (ProcessExecutor processExecutor : BpmExecutionSingleton.getInstance().getProcessExecutors(false)) {
			Object[] row = new Object[6];
			row[0] = processExecutor.getProcessInstance().getId();
			row[1] = processExecutor.getStartDate();
			row[2] = processExecutor.getBusinessKey();
			row[3] = processExecutor.getBpmDefinition().getProcessDefinitionKey();
			row[4] = processExecutor.getProcessExecutorState();
			row[5] = processExecutor.getActivity();
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
					new BpmDefinition(ProcessExecutorSettings.fromValues(1000, false, true), "SimpleTestProcess.bpmn", "SimpleTestProcess")
							.withCustomHandler("TASK#T1", new TaskT1Handler()).withBpmStateChecker("TASK#T1", new TaskT1BpmChecker()));
			BpmExecutionSingleton.getInstance().registerProcessDefinition("AnotherProcess",
					new BpmDefinition(null, "AnotherProcess.bpmn", "AnotherProcess").withBpmStateChecker("TASK#TX",
							new TaskTxBpmChecker()));
			BpmExecutionSingleton.getInstance().registerProcessDefinition("LoopProcess",
					new BpmDefinition(null, "LoopProcess.bpmn", "LoopProcess").withCustomHandler("TASK#TM", new TaskTMHandler()));
			BpmExecutionSingleton.getInstance().registerProcessDefinition("DEF_MAIN_PROCESS",
					new BpmDefinition(null, "collaborationTest.bpmn", "DEF_MAIN_PROCESS")
							.withStartHandler(new CollaborationTestProcessStartHandler()));
			BpmExecutionSingleton.getInstance().registerProcessDefinition("AsynchronProcess",
					new BpmDefinition(null, "AsynchronProcess.bpmn", "AsynchronProcess"));

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
		scExecutionsTable = new JScrollPane();
		executionsTable = new JTable();
		lblProcessPath = new JLabel();
		tabbedPane1 = new JTabbedPane();
		panel1 = new JPanel();
		scrollPane3 = new JScrollPane();
		messagesTable = new JTable();
		panel2 = new JPanel();
		scrollPane4 = new JScrollPane();
		commonMessagesList = new JList();
		btnStartProcess = new JButton();
		btnRefresh = new JButton();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {199, 266, 0};
		((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {263, 0, 92, 0, 0};
		((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
		((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 0.0, 1.0E-4};

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(bpmDefinitionList);
		}
		contentPane.add(scrollPane1, new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));

		//======== scExecutionsTable ========
		{
			scExecutionsTable.setViewportView(executionsTable);
		}
		contentPane.add(scExecutionsTable, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));
		contentPane.add(lblProcessPath, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//======== tabbedPane1 ========
		{

			//======== panel1 ========
			{
				panel1.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new
				javax.swing.border.EmptyBorder(0,0,0,0), "JF\u006frmDes\u0069gner \u0045valua\u0074ion",javax
				.swing.border.TitledBorder.CENTER,javax.swing.border.TitledBorder.BOTTOM,new java
				.awt.Font("D\u0069alog",java.awt.Font.BOLD,12),java.awt
				.Color.red),panel1. getBorder()));panel1. addPropertyChangeListener(new java.beans.
				PropertyChangeListener(){@Override public void propertyChange(java.beans.PropertyChangeEvent e){if("\u0062order".
				equals(e.getPropertyName()))throw new RuntimeException();}});
				panel1.setLayout(new GridBagLayout());
				((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {112, 0};
				((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {87, 0};
				((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
				((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

				//======== scrollPane3 ========
				{
					scrollPane3.setViewportView(messagesTable);
				}
				panel1.add(scrollPane3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			tabbedPane1.addTab("Prozesse", panel1);

			//======== panel2 ========
			{
				panel2.setLayout(new GridBagLayout());
				((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0};
				((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0};
				((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
				((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

				//======== scrollPane4 ========
				{
					scrollPane4.setViewportView(commonMessagesList);
				}
				panel2.add(scrollPane4, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			tabbedPane1.addTab("Allgemein", panel2);
		}
		contentPane.add(tabbedPane1, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//---- btnStartProcess ----
		btnStartProcess.setText("Start process");
		contentPane.add(btnStartProcess, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 5), 0, 0));

		//---- btnRefresh ----
		btnRefresh.setText("Aktualisieren");
		contentPane.add(btnRefresh, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
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
	private JScrollPane scExecutionsTable;
	private JTable executionsTable;
	private JLabel lblProcessPath;
	private JTabbedPane tabbedPane1;
	private JPanel panel1;
	private JScrollPane scrollPane3;
	private JTable messagesTable;
	private JPanel panel2;
	private JScrollPane scrollPane4;
	private JList commonMessagesList;
	private JButton btnStartProcess;
	private JButton btnRefresh;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	public void ping() {
		// fillInstances();
	}
}
