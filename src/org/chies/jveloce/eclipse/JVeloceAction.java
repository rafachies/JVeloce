package org.chies.jveloce.eclipse;

import org.chies.jveloce.core.ClassInjector;
import org.chies.jveloce.eclipse.util.PluginConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class JVeloceAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	public JVeloceAction() {}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		try {
			String editingFilePath = window.getActivePage().getActiveEditor().getEditorInput().getToolTipText();
			if (notJavaSource(editingFilePath)) {
				MessageDialog.openInformation(window.getShell(), PluginConstants.PLUGIN_FRAME_TITLE, "The injection works only with java sources.");
				return;
			}
			ClassInjector classInjector = new ClassInjector();
			classInjector.injectClass(editingFilePath);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openInformation(window.getShell(), PluginConstants.PLUGIN_FRAME_TITLE, e.getMessage());
		}
	}
	
	public void selectionChanged(IAction action, ISelection selection) {}

	public void dispose() {}

	private boolean notJavaSource(String sourcePathStartingWithProjectName) {
		return !sourcePathStartingWithProjectName.endsWith(".java");
	}

}