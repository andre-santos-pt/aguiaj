package pt.iscte.dcti.expressionsview;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

class ProjectClassLoader extends ClassLoader{
	private IProject project;

	private Map<String, Class<?>> loaded;
	
	ProjectClassLoader(ClassLoader parent, IProject project) {
		super(parent);
		this.project = project;
		loaded = new HashMap<String, Class<?>>();
	}

	public static boolean existsInLibrary(IProject project, String name) {
		try {
			IJavaProject javaProj = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
			IClasspathEntry[] classpath = javaProj.getRawClasspath();
			for(IClasspathEntry entry : classpath) {
				if(entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
					IPath p = entry.getPath();
					File f = p.append(name.replace('.', '/')+".class").toFile();
					if(f.exists())
						return true;
				}
			}
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		return false;
	}
	
	
	public Class<?> loadClass(String name) throws ClassNotFoundException {	
		if(name.startsWith("java.") || name.startsWith("sun."))
			return getParent().loadClass(name);

		byte[] classData = null;
		try {
			File file = project.getLocation().append("bin").append(name.replace('.', '/')+".class").toFile();
			if(file.exists())
				classData = getBytesFromFile(file);
			else {

				IJavaProject javaProj = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
				try {
					IClasspathEntry[] classpath = javaProj.getRawClasspath();
					for(IClasspathEntry entry : classpath) {
						if(entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
							IPath p = entry.getPath();
							File f = p.append(name.replace('.', '/')+".class").toFile();
							if(f.exists()) {
								classData = getBytesFromFile(f);
							}
						}
					}
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			
			}
		} catch (IOException e) {					
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(classData == null)
			throw new ClassNotFoundException(name);

		Class<?> clazz = defineClass(name, classData, 0, classData.length);
	
		loaded.put(name, clazz);
		return clazz;
	}

	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int)length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "+file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}
}