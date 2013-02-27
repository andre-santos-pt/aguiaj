package pt.iscte.dcti.expressionsview;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

class ProjectClassLoader extends ClassLoader{
		private IProject project;
		
		ProjectClassLoader(ClassLoader parent, IProject project) {
			super(parent);
			this.project = project;
		}

		public Class<?> loadClass(String name) throws ClassNotFoundException {	
			if(name.startsWith("java.") || name.startsWith("sun."))
				return getParent().loadClass(name);

			//			IFolder folder = project.getFolder(root);
			//			IFile file = folder.getFile(name + ".java");
			byte[] classData = null;
			try {
				classData = getBytesFromFile(project.getLocation().append("bin").append(name+".class").toFile());
			} catch (IOException e) {					
				e.printStackTrace();
			}

			Class<?> clazz = defineClass(name, classData, 0, classData.length);
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