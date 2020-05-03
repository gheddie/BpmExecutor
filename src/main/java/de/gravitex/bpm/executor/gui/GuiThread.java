package de.gravitex.bpm.executor.gui;

public class GuiThread extends Thread {

	private GuiThreadListener guiThreadListener;

	public GuiThread(GuiThreadListener guiThreadListener) {
		super();
		this.guiThreadListener = guiThreadListener;
	}
	
	public void run() {
		
		while (true) {
			try {
				Thread.sleep(5000);
				guiThreadListener.ping();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}