package org.chies.jveloce.console;

import org.chies.jveloce.core.JVMManager;

public class MainConsole {

	public static void main(String[] args) {
		try{
			if (args.length < 3) {
				System.out.println("Usage java -jar classghost <host> <port> <class file path>*");
			} else {
				String host = args[0];
				String port = args[1];
				JVMManager freeVMManager = new JVMManager();
				freeVMManager.connectToVM(host, port);
				System.out.println("Connected on JVM");
				for (int i = 2; i < args.length; i++) {
					System.out.println("Redefining class:" + args[i]);
					try {
						freeVMManager.redefineClass(args[i]);
						System.out.println("Class redefined:" + args[i]);
					} catch (Exception e) {
						System.out.println("Error redefining class:" + args[i]);
						System.out.println(e.getMessage());
					}
				}
				freeVMManager.disconnectFromVM();
			}
		} catch (Exception e) {
			System.out.println("Error while connecting/disconnecting from VM \n" + e.getMessage());
		}
	}
}
