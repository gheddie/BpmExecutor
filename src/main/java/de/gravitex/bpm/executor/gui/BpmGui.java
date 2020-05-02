/*
 * Created by JFormDesigner on Sat May 02 17:15:10 CEST 2020
 */

package de.gravitex.bpm.executor.gui;

import java.awt.*;
import javax.swing.*;

import de.gravitex.bpm.executor.app.BpmExecutionSingleton;
import de.gravitex.bpm.executor.app.ProcessExecutor;
import de.gravitex.bpm.executor.checker.TaskT1BpmChecker;
import de.gravitex.bpm.executor.checker.TaskTxBpmChecker;
import de.gravitex.bpm.executor.handler.TaskT1Handler;
import de.gravitex.bpm.executor.handler.TaskTMHandler;

/**
 * @author Stefan Schulz
 */
public class BpmGui extends JFrame {

	private static final long serialVersionUID = -8277137714910541545L;

	public BpmGui() {
		initComponents();
	}

	public static void main(String[] args) {
		try {
			BpmExecutionSingleton.getInstance().registerProcessDefinition("123",
					ProcessExecutor.create("SimpleTestProcess.bpmn", "SimpleTestProcess").withCustomHandler("TASK#T1", new TaskT1Handler())
							.withBpmStateChecker("TASK#T1", new TaskT1BpmChecker()));
			BpmExecutionSingleton.getInstance().registerProcessDefinition("456",
					ProcessExecutor.create("AnotherProcess.bpmn", "AnotherProcess").withBpmStateChecker("TASK#TX", new TaskTxBpmChecker()));
			BpmExecutionSingleton.getInstance().registerProcessDefinition("789",
					ProcessExecutor.create("LoopProcess.bpmn", "LoopProcess").withCustomHandler("TASK#TM", new TaskTMHandler()));

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
		list1 = new JList();
		button1 = new JButton();

		// ======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		((GridBagLayout) contentPane.getLayout()).columnWidths = new int[] { 124, 0, 0 };
		((GridBagLayout) contentPane.getLayout()).rowHeights = new int[] { 0, 0, 0 };
		((GridBagLayout) contentPane.getLayout()).columnWeights = new double[] { 0.0, 1.0, 1.0E-4 };
		((GridBagLayout) contentPane.getLayout()).rowWeights = new double[] { 1.0, 0.0, 1.0E-4 };

		// ======== scrollPane1 ========
		{
			scrollPane1.setViewportView(list1);
		}
		contentPane.add(scrollPane1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 5), 0, 0));

		// ---- button1 ----
		button1.setText("Start process");
		contentPane.add(button1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 5), 0, 0));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Stefan Schulz
	private JScrollPane scrollPane1;
	private JList list1;
	private JButton button1;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
