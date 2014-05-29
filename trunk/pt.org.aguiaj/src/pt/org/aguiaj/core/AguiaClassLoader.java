/*******************************************************************************
 * Copyright (c) 2014 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L Santos - developer
 ******************************************************************************/
package pt.org.aguiaj.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//import aguiaj.console.Console;
import pt.org.aguiaj.classes.ClassModel;


public class AguiaClassLoader extends ClassLoader{
	private Map<String, File> classFiles;
	private Map<String, Class<?>> loaded;
	private Set<String> packages;

	private static AguiaClassLoader instance;

	private AguiaClassLoader(ClassLoader parent) {
		super(parent);
		classFiles = new HashMap<String, File>();
		classFiles.putAll(AguiaJActivator.getInstance().getPluginClassFiles());
		loaded = new HashMap<String, Class<?>>();
		packages = new HashSet<String>();
	}

	public static AguiaClassLoader getInstance() {
		if(instance == null)
			instance = new AguiaClassLoader(AguiaClassLoader.class.getClassLoader());
		return instance;
	}

	public static void newClassLoader() {
		instance = new AguiaClassLoader(AguiaClassLoader.class.getClassLoader());
	}

	public static AguiaClassLoader getInstance(Map<String, File> classFiles) {		
		AguiaClassLoader loader = getInstance();		
		loader.addClassFiles(classFiles);
		return loader;
	}

	public void addClassFiles(Map<String, File> classFiles) {
		this.classFiles.putAll(classFiles);
	}


	
	public Class<?> loadClass(String name) throws ClassNotFoundException {	
		Class<?> clazz = ClassModel.getInstance().getPluginClass(name);

		if(clazz == null) {
			try {
				if(name.startsWith("java.") || name.startsWith("sun.reflect") || name.startsWith("org.aspectj")) {
					clazz = getParent().loadClass(name);
				}
//				else if(name.equals(Console.class.getName())) {
//					return Console.class;
//				}
				else {		
					File classFile = classFiles.get(name);
					// TODO: error handling (class not found)
					if(classFile == null) {
						System.err.println("ERROR Class Loader: " + name);
						throw new ClassNotFoundException(name);
					}
					byte[] classData = null;
					try {
						classData = getBytesFromFile(classFile);
					} catch (IOException e) {					
						e.printStackTrace();
					}

					if(!loaded.containsKey(name)) {
						try {
							clazz = defineClass(name, classData, 0, classData.length);
							if(name.indexOf('.') != -1) {
								String pck = name.substring(0, name.lastIndexOf('.'));
								if(!packages.contains(pck)) {
									definePackage(pck, "", "", "", "", "", "", null);
									packages.add(pck);
								}
							}
							loaded.put(name, clazz);							
						}
						catch(NoClassDefFoundError e) {
							throw new ClassNotFoundException();
						}
					}
					else {					
						clazz = findLoadedClass(name);
					}
				}
			}
			catch(SecurityException securityException) {
				return super.loadClass(name);
			}
		}

		assert clazz != null;
		return clazz;
	}

	public Class<?> findClass(String name) throws ClassNotFoundException {
		if(loaded.containsKey(name))
			return loaded.get(name);
		else
			throw new ClassNotFoundException();
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
