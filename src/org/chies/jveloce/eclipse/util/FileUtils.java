package org.chies.jveloce.eclipse.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtils {

	public static List<File> listFiles(File folder) throws FileNotFoundException {
		if(!folder.isDirectory()) {
			return new ArrayList<File>();
			
		}
		List<File> result = new ArrayList<File>();
		File[] filesAndDirs = folder.listFiles();
		List<File> filesDirs = Arrays.asList(filesAndDirs);
		for(File file : filesDirs) {
			result.add(file);
			if (!file.isFile()) {
				List<File> deeperList = listFiles(file);
				result.addAll(deeperList);
			}
		}
		return result;
	}

} 

