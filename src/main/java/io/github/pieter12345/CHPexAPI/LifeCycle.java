package io.github.pieter12345.CHPexAPI;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.core.MSVersion;
import com.laytonsmith.core.LogLevel;
import com.laytonsmith.core.exceptions.CRE.CRECastException;
import com.laytonsmith.core.exceptions.CRE.CREIllegalArgumentException;
import com.laytonsmith.core.exceptions.CRE.CRENullPointerException;
import com.laytonsmith.core.exceptions.CRE.CREThrowable;
import com.laytonsmith.core.extensions.AbstractExtension;
import com.laytonsmith.core.extensions.MSExtension;
import com.laytonsmith.core.functions.AbstractFunction;

/**
 * CHPexAPI's LifeCycle.
 * @author P.J.S. Kools
 * @since 24-07-2016
 */

@MSExtension("CHPexAPI")
public class LifeCycle extends AbstractExtension {
	
	// Variables & Constants.
	private static final Version version;
	
	static {
		// Get the version from the manifest.
		Package pack = LifeCycle.class.getPackage();
		if(pack != null) {
			version = new SimpleVersion(
					pack.getImplementationVersion().replaceFirst("-", " ")); // Replace the "-" in A.B.C-SNAPSHOT so SimpleVersion can parse it.
		} else {
			version = new SimpleVersion(0, 0, 0, "Unknown");
		}
	}
	
	@Override
	public void onStartup() {
		System.out.println("CHPexAPI " + this.getVersion().toString() + " loaded.");
	}
	
	@Override
	public void onShutdown() {
		System.out.println("CHPexAPI " + this.getVersion().toString() + " unloaded.");
	}
	
	@Override
	public Version getVersion() {
		return version;
	}
	
	public static abstract class PexFunction extends AbstractFunction {
		
		@Override
		public boolean isRestricted() {
			return true;
		}
		
		@Override
		public MSVersion since() {
			return MSVersion.V3_3_2;
		}
		
		@Override
		public Boolean runAsync() {
			return false;
		}
		
		@Override
		public String getName() {
			return this.getClass().getSimpleName();
		}
		
		@Override
		public LogLevel profileAt() {
			return LogLevel.DEBUG;
		}
		
		/**
		 * Pex functions throw CREIllegalArgumentException, CRENullPointerException and CRECastException by default
		 * due to argument validation.
		 */
		@Override
		@SuppressWarnings("unchecked")
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {
					CREIllegalArgumentException.class, CRENullPointerException.class, CRECastException.class};
		}
	}
}
