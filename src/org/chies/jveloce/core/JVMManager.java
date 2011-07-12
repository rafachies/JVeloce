package org.chies.jveloce.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

public class JVMManager {
	
	private static final String TRANSPORT_FIELD = "dt_socket";
	final int RUNNING_MODE = 1;
	
	private HotWire hotWire;
	private VirtualMachine targetVM;
	
	public void connectToVM(String host, String port) throws JVeloceException {
		try {
			hotWire = new HotWire();
			hotWire.connect(TRANSPORT_FIELD, host, port);
		} catch (IOException exception) {
			throw new JVeloceException("Failed to connect with JVM at " + host + "@" + port, exception);
		} catch (IllegalConnectorArgumentsException exception) {
			throw new JVeloceException("Illegal Connector Arguments Exception cought in the args passed to the listening connector", exception);
		}
		if (hotWire.isConnected()) {
			this.targetVM = hotWire.getTargetVM();
		}
	}

	public void redefineClass(String classPath) throws Exception {
		if (targetVM != null) {
            List<?> allClasses = targetVM.allClasses();
            Map<ReferenceType, byte[]> redefinitionMap = new HashMap<ReferenceType, byte[]>();
            File file = new File(classPath);
            byte[] bytes = new byte[(int)file.length()];
            FileInputStream stream = new FileInputStream(file);
            stream.read(bytes);
            ClassReader classReader = new ClassReader();
            String className = classReader.getClassName(bytes);
            ClassDataObject classDataObject = new ClassDataObject();
            classDataObject.setClassName(className);
            classDataObject.setClassData(bytes);
            String localName = classDataObject.getClassName();
            for (Object clazz : allClasses){
            	ReferenceType remoteReference = (ReferenceType) clazz;
            	String remoteName = remoteReference.name();
            	if(localName.equals(remoteName)){
            		redefinitionMap.put(remoteReference, classDataObject.getClassData());
            		break;
            	}
			}
            targetVM.redefineClasses(redefinitionMap);
		}
	}

	public void disconnectFromVM() {
	    if (hotWire != null) {
            hotWire.disconnect();
        }
	}

}
