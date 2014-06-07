package com.next.dynamic;

import dalvik.system.DexClassLoader;

public class SHClassLoader extends DexClassLoader{

	public SHClassLoader(String dexPath, String optimizedDirectory,
			String libraryPath, ClassLoader parent) {
		super(dexPath, optimizedDirectory, libraryPath, parent);
		// TODO Auto-generated constructor stub
	}

}
