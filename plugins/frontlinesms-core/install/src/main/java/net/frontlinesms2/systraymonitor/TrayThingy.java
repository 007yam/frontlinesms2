package net.frontlinesms2.systraymonitor;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JOptionPane;

import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.LifeCycle.Listener;

import static net.frontlinesms2.systraymonitor.Utils.*;

public class TrayThingy implements Listener {
	private final Monitor m;
	private TrayIcon t;
	private PopupMenu popup;
	private MenuItem open;

//> INITIALISATION
	public TrayThingy(Monitor m) {
		this.m = m;
		m.addListener(this);
	}

	private void init() {
		popup = createPopupMenu();
		t = new TrayIcon(getIcon("stopped"), "FrontlineSMS2", popup);
		t.setImageAutoSize(true);
		t.addActionListener(createActionListener());
		d(m.getServer());
	}
	
//> ACCESSORS
	public TrayIcon getTrayIcon() {
		if(t == null) init();
		return t;
	}

//> LifeCycle.Listener METHODS
	private void updateStatus(String name) {
		t.setToolTip("FrontlineSMS2 :: " + name + "...");
		t.setImage(getIcon(name));
	}

	private Image getIcon(String name) {
		return Toolkit.getDefaultToolkit().getImage(getClass().getResource("/tray/" + name + ".png"));
	}

	public void lifeCycleFailure(LifeCycle event, Throwable cause) {
		d(event);
		cause.printStackTrace();
	}
	public void lifeCycleStarted(LifeCycle event) { d(event); launchBrowser(); removeSplashScreen();  }
	public void lifeCycleStarting(LifeCycle event) { d(event); }
	public void lifeCycleStopped(LifeCycle event) { d(event); }
	public void lifeCycleStopping(LifeCycle event) { d(event); }
	private void d(LifeCycle e) {
		o("Lifecyle event: " + e);
		o("__isFailed(): " + e.isFailed());
		o("__isRunning(): " + e.isRunning());
		o("__isStarted(): " + e.isStarted());
		o("__isStarting(): " + e.isStarting());
		o("__isStopped(): " + e.isStopped());
		o("__isStopping(): " + e.isStopping());

		if(e.isStarting()) updateStatus("starting");
		else if(e.isStopping()) updateStatus("stopping");
		else if(e.isStopped()) updateStatus("stopped");
		else if(e.isRunning()) updateStatus("running");
		else o("Not sure what to set the icon to in this state.");

		open.setEnabled(e.isRunning() && !e.isStarting() && !e.isStopping());
	}
	
//> MENU BUILDING
	private PopupMenu createPopupMenu() {
		PopupMenu popup = new PopupMenu();

		popup.add(open = new ClickMenuItem("Open FrontlineSMS") {
			void click() { launchBrowser(); }});

		popup.add(new ClickMenuItem("Shutdown FrontlineSMS") {
			void click() { System.exit(0); }});

		return popup;
	}

	private ActionListener createActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchBrowser();
			}
		};
	}
	
	private void launchBrowser() {
		openWebBrowser(m.getUrl());
	}

	private void removeSplashScreen() {
		try {
			com.install4j.api.launcher.SplashScreen.hide();
		} catch(com.install4j.api.launcher.SplashScreen.ConnectionException ex) {
			// No idea what to do here - presumably there's now a zombie splash screen
		}
	}
}

abstract class ClickMenuItem extends MenuItem {
	public ClickMenuItem(String text) {
		super(text);
		this.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					click();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}
	abstract void click() throws Exception;
}

