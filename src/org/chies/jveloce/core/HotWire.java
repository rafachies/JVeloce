package org.chies.jveloce.core;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.chies.jveloce.eclipse.util.PluginConstants;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

public class HotWire {

	private boolean isConnected;
    private VirtualMachine targetVM;
    private List<AttachingConnector> connectorList;

    public void connect(String transport, String host, String port) throws IllegalConnectorArgumentsException, IOException {
        VirtualMachineManager virtualMachineManager = Bootstrap.virtualMachineManager();
        connectorList = virtualMachineManager.attachingConnectors();
        for (AttachingConnector attachingConnector : connectorList) {
            if (attachingConnector.transport().name().equals("dt_socket")) {
                Map args = attachingConnector.defaultArguments();
                Argument arg = (Connector.Argument) args.get("port");
                Argument arg1 = (Connector.Argument) args.get("hostname");
                arg.setValue(port);
                arg1.setValue(host);
                targetVM = attachingConnector.attach(args);
                isConnected = true;
            }
        }
    }

    public void disconnect() {
        if (this.isConnected) {
            try {
                targetVM.dispose();
                isConnected = false;
            } catch (VMDisconnectedException e) {
                JOptionPane.showMessageDialog(null, "Connection with Target Virtual Machine has been lost. \n", PluginConstants.PLUGIN_FRAME_TITLE, JOptionPane.ERROR_MESSAGE);
                isConnected = false;
            }
        }
    }

    public VirtualMachine getTargetVM() {
        return targetVM;
    }

    public boolean isConnected() {
        return isConnected;
    }

}
