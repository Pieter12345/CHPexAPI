package io.github.pieter12345.CHPexAPI;

import com.laytonsmith.annotations.api;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CBoolean;
import com.laytonsmith.core.constructs.CNull;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.CommandHelperEnvironment;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.exceptions.CRE.CRECastException;
import com.laytonsmith.core.exceptions.CRE.CREIllegalArgumentException;
import com.laytonsmith.core.exceptions.CRE.CRENullPointerException;
import io.github.pieter12345.CHPexAPI.LifeCycle.PexFunction;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * Contains functions for PermissionsEx management.
 * @author P.J.S. Kools
 */
public class CHPexFunctions {
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_get_uuid extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			String player = args[0].val();
			PermissionUser permUser = getPexUser(args[0], t);
			String identifier = permUser.getIdentifier();
			if(player.equalsIgnoreCase(identifier)) {
				return CNull.NULL;
			} else {
				return new CString(identifier, t);
			}
		}
		
		@Override
		public String docs() {
			return "string {player} Returns the UUID of the given player or null if the player was not identified by"
					+ " a UUID in the pex permissions file."
					+ " Throws IllegalArgumentException when player is empty."
					+ " Throws NullPointerException when player is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {1};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_has_permission extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionUser permUser = getPexUser(args[0], t);
			String permission = convertStringArg(args[1], "permission", t);
			String world = (args.length > 2 ? convertStringArg(args[2], "world", t) : null);
			if(permission == null || permission.isEmpty()) {
				return CBoolean.FALSE;
			}
			return CBoolean.get((world == null || world.isEmpty()
					? permUser.has(permission) : permUser.has(permission, world)));
		}
		
		@Override
		public String docs() {
			return "boolean {player/uuid, permission, [world]} Returns true if the player/uuid has the given"
					+ " permission in the given world, false otherwise."
					+ " If no world is supplied or if world is empty or null, the permission will be checked globally."
					+ " Throws IllegalArgumentException when player is empty."
					+ " Throws NullPointerException when player or permission is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {2, 3};
		}
	}
	
	static PermissionUser getPexUser(Construct user, Target t)
			throws CRENullPointerException, CRECastException, CREIllegalArgumentException {
		return PermissionsEx.getPermissionManager().getUser(convertNonNullNonEmptyStringArg(user, "user", t));
	}
	
	static PermissionGroup getPexGroup(Construct group, Target t)
			throws CRENullPointerException, CRECastException, CREIllegalArgumentException {
		return PermissionsEx.getPermissionManager().getGroup(convertNonNullNonEmptyStringArg(group, "group", t));
	}
	
	static String convertStringArg(Construct cString, String argName, Target t) throws CRECastException {
		if(cString instanceof CNull) {
			return null;
		}
		if(!(cString instanceof CString)) {
			throw new CRECastException("Expecting " + argName + " to be a string. Found: " + cString.typeof() + ".", t);
		}
		return cString.val();
	}
	
	static String convertNonNullStringArg(Construct cString, String argName, Target t)
			throws CRENullPointerException, CRECastException {
		if(cString instanceof CNull) {
			throw new CRENullPointerException("Argument " + argName + " can not be null.", t);
		}
		if(!(cString instanceof CString)) {
			throw new CRECastException("Expecting " + argName + " to be a string. Found: " + cString.typeof() + ".", t);
		}
		return cString.val();
	}
	
	static String convertNonNullNonEmptyStringArg(Construct cString, String argName, Target t)
			throws CRENullPointerException, CRECastException, CREIllegalArgumentException {
		String str = convertNonNullStringArg(cString, argName, t);
		if(str.isEmpty()) {
			throw new CREIllegalArgumentException("Argument " + argName + " can not be empty.", t);
		}
		return str;
	}
}
