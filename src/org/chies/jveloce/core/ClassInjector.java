package org.chies.jveloce.core;

import java.util.Collection;

import javax.swing.JOptionPane;

import org.chies.jveloce.eclipse.GUIVMTargetChoice;
import org.chies.jveloce.eclipse.UserEventListener;
import org.chies.jveloce.eclipse.util.EclipseClasspathManager;
import org.chies.jveloce.eclipse.util.PluginConstants;

public class ClassInjector implements UserEventListener{
	
	private Collection<String> classesToBeDefined;
	private GUIVMTargetChoice guivmTargetChoice;
	
	public void injectClass(String sourcePath) throws Exception {
		EclipseClasspathManager eclipseClasspathManager = new EclipseClasspathManager();
		classesToBeDefined = eclipseClasspathManager.getClassAbsolutePath(sourcePath);
		openJVMTargetDialog();
	}

	public void onTargetChosen(Object object) {
		JVMManager jvmManager = null;
		JVMTarget jvmTarget = (JVMTarget) object;
		try {
			jvmManager = new JVMManager();
			jvmManager.connectToVM(jvmTarget.getHost(), jvmTarget.getPort());
			for (String classToBeDefined: classesToBeDefined) {
				jvmManager.redefineClass(classToBeDefined);
				JOptionPane.showMessageDialog(null, "Class redefined with success:\n" + classToBeDefined , PluginConstants.PLUGIN_FRAME_TITLE, JOptionPane.OK_OPTION);
			}
		} catch (UnsupportedOperationException e) {
			JOptionPane.showMessageDialog(null, "Class not redefined. This kind of change is not supported yet: " + e.getMessage(), PluginConstants.PLUGIN_FRAME_TITLE, JOptionPane.ERROR_MESSAGE);
		} catch (JVeloceException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), PluginConstants.PLUGIN_FRAME_TITLE, JOptionPane.ERROR_MESSAGE);
		} catch (Throwable e) {
			JOptionPane.showMessageDialog(null, "Class not redefined." + e.getClass() + ": " + e.getMessage(), PluginConstants.PLUGIN_FRAME_TITLE, JOptionPane.ERROR_MESSAGE);
		} finally {
			if (jvmManager != null) {
				jvmManager.disconnectFromVM();
			}
		}
	}
	
	private void openJVMTargetDialog() {
		guivmTargetChoice = GUIVMTargetChoice.getInstance(this);
		guivmTargetChoice.setVisible(true);
	}
}
