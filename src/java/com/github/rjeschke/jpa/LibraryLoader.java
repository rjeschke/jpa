/*
* Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
* See LICENSE.txt for licensing information.
*/
package com.github.rjeschke.jpa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public final class LibraryLoader
{
	public final static String PATH_SEPARATOR = System.getProperty("path.separator");
	public final static String LINE_SEPARATOR = System.getProperty("line.separator");
	public final static String FILE_SEPARATOR = System.getProperty("file.separator");
	public final static String TEMP_DIR = System.getProperty("java.io.tmpdir");

	private LibraryLoader() { /* singleton */ }
	
	private final static Set<String> loadedLibs = new HashSet<String>(); 
	
	public final static void load(final String basePackage, final String lib)
	{
		final Class<?> caller = getCallingClass();
		load(caller, mapLibrary(caller, basePackage, lib), true, false);
	}
	
	public final static void load(final String basePackage, final String lib, final boolean tryOverwrite)
	{
		final Class<?> caller = getCallingClass();
		load(caller, mapLibrary(caller, basePackage, lib), tryOverwrite, false);
	}

    public final static void loadNative(final String os, final String basePackage, final String lib)
    {
        final Class<?> caller = getCallingClass();
        loadNative(caller, os, basePackage, lib, true);
    }
    
	public final static void loadNative(final String os, final String basePackage, final String lib, boolean tryOverwrite)
	{
        final Class<?> caller = getCallingClass();
        loadNative(caller, os, basePackage, lib, tryOverwrite);
	}
	
	private final static void loadNative(final Class<?> caller, final String os, final String basePackage, final String lib, boolean tryOverwrite)
	{
        final String osId = getOsIdentifier();
        final String osStr = osId + System.getProperty("sun.arch.data.model");
        
        if(osId.equals(os))
            load(caller, mapLibrary(caller, basePackage, lib), true, tryOverwrite);
        else if(osStr.equals(os))
            load(caller, basePackage + "." + osStr + "." + lib, true, tryOverwrite);
	}
	
	private final static void load(final Class<?> caller, final String lib, final boolean tryOverwrite, final boolean isNative)
	{
		String libname, pkg;
		if(lib.indexOf('.') == -1)
		{
			pkg = "/";
			libname = lib;
		}
		else
		{
			if(isNative)
			{
				int i = lib.lastIndexOf('.');
				i = lib.substring(0, i).lastIndexOf('.');
				pkg = "/" + lib.substring(0, i + 1).replace('.', '/');
				libname = lib.substring(i + 1);
			}
			else
			{
				final int i = lib.lastIndexOf('.');
				pkg = "/" + lib.substring(0, i + 1).replace('.', '/');
				libname = lib.substring(i + 1);
			}
		}
		try
		{
			System.loadLibrary(libname);
			return;
		}
		catch(UnsatisfiedLinkError e)
		{
			// Ignore
		}
		
		final String osLib = isNative ? libname : System.mapLibraryName(libname);
		final File f = new File(TEMP_DIR, osLib);
		if(tryOverwrite)
		{
			f.delete();
		}
		if(!f.exists())
		{
			final InputStream is = caller.getResourceAsStream(pkg + osLib);
			if(is == null)
			{
				throw new UnsatisfiedLinkError("Can't locate '" + osLib + "'");
			}
			fileCopy(is, f);
		}
		if(!loadedLibs.contains(f.getAbsolutePath()))
		{
			loadedLibs.add(f.getAbsolutePath());
			System.load(f.getAbsolutePath());
		}
	}
	
	private final static void fileCopy(final InputStream src, final File dest)
	{
		final byte[] buffer = new byte[8192];
		FileOutputStream fos = null;
		
		try
		{
			fos = new FileOutputStream(dest);
			
			for(;;)
			{
				final int read = src.read(buffer, 0, 8192);
				if(read <= 0)
				{
					break;
				}
				fos.write(buffer, 0, read);
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			try
			{
				if(fos != null)
				{
					fos.close();
				}
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
			finally
			{
				try
				{
					src.close();
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	private final static Class<?> getCallingClass()
	{
		try
		{
			final StackTraceElement myCaller = Thread.currentThread().getStackTrace()[3];
			return Class.forName(myCaller.getClassName());
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private final static String getOsIdentifier()
	{
		final String os = System.getProperty("os.name").toLowerCase();
		
		if(os.startsWith("windows"))
			return "win";
		if(os.startsWith("linux"))
			return "linux";
		if(os.startsWith("mac"))
			return "mac";

		throw new RuntimeException("Unsupported OS version:" + System.getProperty("os.name"));
	}

	private final static String mapLibrary(final Class<?> caller, final String basePackage, final String libName)
	{
		final String osid = getOsIdentifier();
		final String arch = System.getProperty("sun.arch.data.model");
		final String check = basePackage + "." + osid;
		if(caller.getResource("/" + check.replace('.', '/') + arch + "/") != null)
		{
			return check + arch + "." + libName;
		}
		if(caller.getResource("/" + check.replace('.', '/') + "32/") != null)
		{
			return check + "32." + libName;
		}
		if(caller.getResource("/" + check.replace('.', '/') + "/") != null)
		{
			return check + "." + libName;
		}
		throw new RuntimeException("Can't map library: " + basePackage + "/" + libName);
	}
}
