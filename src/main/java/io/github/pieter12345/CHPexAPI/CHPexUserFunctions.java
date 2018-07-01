package io.github.pieter12345.CHPexAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.laytonsmith.annotations.api;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CNull;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.CVoid;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.CommandHelperEnvironment;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.exceptions.CRE.CRECastException;
import com.laytonsmith.core.exceptions.CRE.CREIllegalArgumentException;
import com.laytonsmith.core.exceptions.CRE.CRENullPointerException;
import com.laytonsmith.core.exceptions.CRE.CREThrowable;

import io.github.pieter12345.CHPexAPI.LifeCycle.PexFunction;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * Contains functions for PermissionsEx user management.
 * @author P.J.S. Kools
 */
public class CHPexUserFunctions extends CHPexFunctions {
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_get_users extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			CArray ret = new CArray(t);
			for(PermissionUser permUser : PermissionsEx.getPermissionManager().getUsers()) {
				ret.push(new CString(permUser.getIdentifier(), t), t);
			}
			return ret;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {};
		}
		
		@Override
		public String docs() {
			return "array {} Returns an array containing all players/uuids in the pex config.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {0};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_get_user_options extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionUser permUser = getPexUser(args[0], t);
			CArray ret = CArray.GetAssociativeArray(t);
			for(Entry<String, Map<String, String>> optionEntry : permUser.getAllOptions().entrySet()) {
				CArray cOptionList = CArray.GetAssociativeArray(t);
				for(Entry<String, String> entry : optionEntry.getValue().entrySet()) {
					cOptionList.set(entry.getKey(), entry.getValue(), t);
				}
				ret.set(optionEntry.getKey(), cOptionList, t);
			}
			return ret;
		}
		
		@Override
		public String docs() {
			return "array {player/uuid} Returns all personal options of the given player/uuid in an associative array"
					+ " in format: array('': optionArray, 'worldName': optionArray)"
					+ " where optionArray is an associative array with possible keys name, prefix and suffix."
					+ " Throws IllegalArgumentException when player/uuid is empty."
					+ " Throws NullPointerException when player/uuid is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {1};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_set_user_option extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionUser permUser = getPexUser(args[0], t);
			// An empty key will overwrite the options array with an empty string, so disallow empty keys.
			String optionKey = convertNonNullNonEmptyStringArg(args[1], "optionKey", t);
			String optionValue = convertStringArg(args[2], "optionValue", t);
			String world = (args.length > 3 ? convertStringArg(args[3], "world", t) : null);
			if(world != null && !world.isEmpty()) {
				permUser.setOption(optionKey, optionValue, world);
			} else {
				permUser.setOption(optionKey, optionValue);
			}
			return CVoid.VOID;
		}
		
		@Override
		public String docs() {
			return "void {player/uuid, optionKey, optionValue, [world]} Sets the given option for the given player/uuid"
					+ " in the given or all worlds."
					+ " Option keys used by Pex are: name, prefix and suffix."
					+ " Setting the value to null will remove the option from the player/uuid."
					+ " The world argument will be ignored when it is null or empty."
					+ " Throws IllegalArgumentException when player/uuid or optionKey is empty."
					+ " Throws NullPointerException when player/uuid or optionKey is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {3, 4};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_get_user_permissions extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionUser permUser = getPexUser(args[0], t);
			CArray ret = CArray.GetAssociativeArray(t);
			for(Entry<String, List<String>> permissionsEntry : permUser.getAllPermissions().entrySet()) {
				CArray perms = new CArray(t);
				for(String perm : permissionsEntry.getValue()) {
					perms.push(new CString(perm, t), t);
				}
				ret.set(permissionsEntry.getKey(), perms, t);
			}
			return ret;
		}
		
		@Override
		public String docs() {
			return "array {player/uuid} Returns an array containing all personal permissions the player/uuid has"
					+ " in format: array('': personalPermissions, 'worldName': personalPermissions)."
					+ " where personalPermissions is an array of permission strings."
					+ " This does NOT include permissions a player/uuid has through groups."
					+ " Throws IllegalArgumentException when player/uuid is empty."
					+ " Throws NullPointerException when player/uuid is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {1};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_set_user_permissions extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionUser permUser = getPexUser(args[0], t);
			
			// Get permissions.
			if(!(args[1] instanceof CArray)) {
				throw new CRECastException("Expecting the second argument of " + getName()
						+ "(player/uuid, permissionArray) to be an array, but found: " + args[1].typeof() + ".", t);
			}
			CArray permsArray = (CArray) args[1];
			if(permsArray.isAssociative()) {
				throw new CREIllegalArgumentException("Expecting the second argument of " + getName()
				+ "(player/uuid, permissionArray) to be a non-associative array.", t);
			}
			List<String> permsList = new ArrayList<>();
			for(Construct perm : permsArray.asList()) {
				permsList.add(convertNonNullNonEmptyStringArg(perm, "permission", t));
			}
			
			// Get world.
			String world = (args.length > 2 ? convertStringArg(args[2], "world", t) : null);
			
			// Set permissions.
			if(world != null && !world.isEmpty()) {
				permUser.setPermissions(permsList, world);
			} else {
				permUser.setPermissions(permsList);
			}
			return CVoid.VOID;
		}
		
		@Override
		public String docs() {
			return "void {player/uuid, permissionArray, [world}"
					+ " Sets the given permissions for the given player/uuid in the given or all worlds."
					+ " The world argument will be ignored when it is null or empty."
					+ " Throws IllegalArgumentException when player/uuid or one of the permissions is empty."
					+ " Throws NullPointerException when player/uuid or one of the permissions is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {2, 3};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_add_user_permission extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionUser permUser = getPexUser(args[0], t);
			String perm = convertNonNullNonEmptyStringArg(args[1], "permission", t);
			String world = (args.length > 2 ? convertStringArg(args[2], "world", t) : null);
			if(world != null && !world.isEmpty()) {
				permUser.addPermission(perm, world);
			} else {
				permUser.addPermission(perm);
			}
			return CVoid.VOID;
		}
		
		@Override
		public String docs() {
			return "void {player/uuid, permission, [world]}"
					+ " Adds the given permissions to the given player/uuid in the given or all worlds."
					+ " The world argument will be ignored when it is null or empty."
					+ " Throws IllegalArgumentException when player/uuid or permission is empty."
					+ " Throws NullPointerException when player/uuid or permission is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {2, 3};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_remove_user_permission extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionUser permUser = getPexUser(args[0], t);
			String perm = convertNonNullNonEmptyStringArg(args[1], "permission", t);
			String world = (args.length > 2 ? convertStringArg(args[2], "world", t) : null);
			if(world != null && !world.isEmpty()) {
				permUser.removePermission(perm, world);
			} else {
				permUser.removePermission(perm);
			}
			return CVoid.VOID;
		}
		
		@Override
		public String docs() {
			return "void {player/uuid, permission, [world]}"
					+ " Removes the given permissions from the given player/uuid in the given or all worlds."
					+ " The world argument will be ignored when it is null or empty."
					+ " Throws IllegalArgumentException when player/uuid or permission is empty."
					+ " Throws NullPointerException when player/uuid or permission is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {2, 3};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_get_user_groups extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionUser permUser = getPexUser(args[0], t);
			String world = (args.length > 1 ? convertStringArg(args[1], "world", t) : null);
			List<PermissionGroup> permGroups = (world == null || world.isEmpty()
					? permUser.getOwnParents() : permUser.getOwnParents(world));
			CArray userGroups = new CArray(t);
			for(PermissionGroup permGroup : permGroups) {
				userGroups.push(new CString(permGroup.getName(), t), t);
			}
			return userGroups;
		}
		
		@Override
		public String docs() {
			return "array {player/uuid, [world]} Returns an array containing all groups the player/uuid is in,"
					+ " specifically for the given or all worlds"
					+ " (so when getting groups from some world, the non-world specific groups are not included)."
					+ " The world argument will be ignored when it is null or empty."
					+ " Returns an array containing only the default group if a default group has been set and no"
					+ " other groups were found."
					+ " Throws IllegalArgumentException when player/uuid is empty."
					+ " Throws NullPointerException when player/uuid is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {1, 2};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_set_user_groups extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionUser permUser = getPexUser(args[0], t);
			
			// Get groups.
			Construct groups = args[1];
			if(groups instanceof CNull) {
				throw new CRENullPointerException("Groups array can not be null.", t);
			}
			if(!(groups instanceof CArray) || ((CArray) groups).isAssociative()) {
				throw new CRECastException(
						"Expecting a non-associative array for groups. Received: " + groups.typeof() + ".", t);
			}
			CArray groupsArray = (CArray) groups;
			List<String> parentNames = new ArrayList<String>((int) groupsArray.size());
			for(Construct group : groupsArray.asList()) {
				parentNames.add(convertNonNullNonEmptyStringArg(group, "group", t));
			}
			
			// Get world.
			String world = (args.length > 2 ? convertStringArg(args[2], "world", t) : null);
			
			// Set groups (converting them to PermissionGroups is useless as they are set by name).
			if(world != null && !world.isEmpty()) {
				permUser.setParentsIdentifier(parentNames, world);
			} else {
				permUser.setParentsIdentifier(parentNames);
			}
			return CVoid.VOID;
		}
		
		@Override
		public String docs() {
			return "void {player/uuid, groupArray, [world]} Sets the groups for the given player/uuid in the given"
					+ " or all worlds."
					+ " The world argument will be ignored when it is null or empty."
					+ " Throws IllegalArgumentException when player/uuid or a group in the groupArray is empty."
					+ " Throws NullPointerException when player/uuid or a group in the groupArray is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {2, 3};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_add_user_group extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionUser permUser = getPexUser(args[0], t);
			String group = convertNonNullStringArg(args[1], "group", t);
			String world = (args.length > 2 ? convertStringArg(args[2], "world", t) : null);
			if(world != null && !world.isEmpty()) {
				permUser.addGroup(group, world);
			} else {
				permUser.addGroup(group);
			}
			return CVoid.VOID;
		}
		
		@Override
		public String docs() {
			return "void {player/uuid, group, [world]} Adds the given player/uuid to the given group in the given"
					+ " or all worlds."
					+ " The world argument will be ignored when it is null or empty."
					+ " Throws IllegalArgumentException when player/uuid or group is empty."
					+ " Throws NullPointerException when player/uuid or group is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {2, 3};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_remove_user_group extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionUser permUser = getPexUser(args[0], t);
			String group = convertNonNullStringArg(args[1], "group", t);
			String world = (args.length > 2 ? convertStringArg(args[2], "world", t) : null);
			if(world != null && !world.isEmpty()) {
				permUser.removeGroup(group, world);
			} else {
				permUser.removeGroup(group);
			}
			return CVoid.VOID;
		}
		
		@Override
		public String docs() {
			return "void {player/uuid, group, [world]} Removes the given player/uuid from the given group in the given"
					+ " or all worlds."
					+ " The world argument will be ignored when it is null or empty."
					+ " Throws IllegalArgumentException when player/uuid or group is empty."
					+ " Throws NullPointerException when player/uuid or group is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {2, 3};
		}
	}
}
