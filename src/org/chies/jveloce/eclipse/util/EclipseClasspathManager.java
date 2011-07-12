package org.chies.jveloce.eclipse.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;

public class EclipseClasspathManager {

	public Collection<String> getClassAbsolutePath(String javaSourcePath) throws Exception {
		String[] editingSourceSplittedBySlash = javaSourcePath.split("/");
		String projectName = editingSourceSplittedBySlash[0];
		String javaName = editingSourceSplittedBySlash[editingSourceSplittedBySlash.length - 1];
		String classNameToFind = javaName.replaceAll(".java", ".class");
		IJavaProject classProject = getEditingProject(projectName);
		return findPath(classProject, classNameToFind);
	}
	
	private IJavaProject getEditingProject(String projectName) throws CoreException, ProjectUnavailableException {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for(IProject project: projects){
			IJavaProject javaProject = JavaCore.create(project);
			if (projectName.matches(project.getName())) {
				return javaProject;
			}
		}
		throw new ProjectUnavailableException("Unable to access editing project: " + projectName);
	}
	
	//TODO: try to find the wright class from editing source instead of all with close name.
	private Collection<String> findPath(IJavaProject javaProject, String classNameToFind) throws Exception {
		Collection<String> classesFound = new ArrayList<String>();
		String[] projectClasspaths = getProjectClasspaths(javaProject);
		for (String classpath : projectClasspaths) {
			IPath path = new Path(classpath);
			File classpathFolder = path.toFile();
			List<File> classpathFiles = FileUtils.listFiles(classpathFolder);
			for (File file : classpathFiles) {
				String[] pathSplittedBySlash = file.getAbsolutePath().split("/");
				String className = pathSplittedBySlash[pathSplittedBySlash.length - 1];
				if (classNameToFind.endsWith(className)) {
					classesFound.add(file.getAbsolutePath());
				}
			}
		}
		return classesFound;
	}

	private String[] getProjectClasspaths(IJavaProject javaProject)	throws CoreException {
		Collection<String> classpathEntries = new ArrayList<String>();
		String[] classpathEntriesAsArray = JavaRuntime.computeDefaultRuntimeClassPath(javaProject);
		Collections.addAll(classpathEntries, classpathEntriesAsArray);
		return classpathEntriesAsArray;
	}
}
