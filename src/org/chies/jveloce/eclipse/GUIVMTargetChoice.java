package org.chies.jveloce.eclipse;

import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.chies.jveloce.core.JVMTarget;

public class GUIVMTargetChoice extends JFrame {

	private static final long serialVersionUID = -535581081616757234L;
	
	private JPanel mainPanel = null;
	private JLabel hostLabel = null;
	private JLabel portLabel = null;
	private JButton okButton = null;
	private JLabel titleLabel = null;
	private JButton cancelButton = null;
	private JTextField hostTextField = null;
	private JTextField portTextField = null;

	private UserEventListener eventListener;
	
	private static GUIVMTargetChoice singletonInstance;
	
	public static GUIVMTargetChoice getInstance(UserEventListener eventListener) {
		if (singletonInstance == null) {
			singletonInstance = new GUIVMTargetChoice(eventListener);
		}
		singletonInstance.eventListener = eventListener;
		return singletonInstance;
	}
	
	private GUIVMTargetChoice(UserEventListener eventListener) {
		super();
		initialize();
	}

	
	private void initialize() {
		this.setSize(209, 145);
		this.setContentPane(getJContentPane());
		this.setTitle("Choose JVM Target");
		this.setUndecorated(true);
		this.setLocationRelativeTo(null);
	}

	private JPanel getJContentPane() {
		if (mainPanel == null) {
			titleLabel = new JLabel();
			titleLabel.setBounds(new Rectangle(14, -1, 170, 24));
			titleLabel.setText("Choose JVM target:");
			portLabel = new JLabel();
			portLabel.setBounds(new Rectangle(27, 63, 42, 25));
			portLabel.setText("Port:");
			hostLabel = new JLabel();
			hostLabel.setBounds(new Rectangle(24, 32, 48, 26));
			hostLabel.setText("Host:");
			mainPanel = new JPanel();
			mainPanel.setLayout(null);
			mainPanel.add(hostLabel, null);
			mainPanel.add(portLabel, null);
			mainPanel.add(getJTextField(), null);
			mainPanel.add(getJTextField1(), null);
			mainPanel.add(getOkButton(), null);
			mainPanel.add(getCancelton(), null);
			mainPanel.add(titleLabel, null);
		}
		return mainPanel;
	}

	private JTextField getJTextField() {
		if (hostTextField == null) {
			hostTextField = new JTextField();
			hostTextField.setBounds(new Rectangle(69, 34, 113, 25));
		}
		return hostTextField;
	}

	private JTextField getJTextField1() {
		if (portTextField == null) {
			portTextField = new JTextField();
			portTextField.setBounds(new Rectangle(69, 64, 113, 25));
		}
		return portTextField;
	}

	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setBounds(new Rectangle(19, 91, 81, 24));
			okButton.setText("Inject");
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JVMTarget jvmTarget = new JVMTarget();
					jvmTarget.setHost(hostTextField.getText());
					jvmTarget.setPort(portTextField.getText());
					dispose();
					eventListener.onTargetChosen(jvmTarget);
				}
			});
		}
		return okButton;
	}

	private JButton getCancelton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setBounds(new Rectangle(111, 90, 81, 24));
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return cancelButton;
	}

}
