package io.github.pieter12345.CHPexAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.laytonsmith.annotations.api;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CNull;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.CVoid;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.CommandHelperEnvironment;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.exceptions.CRE.CRECastException;
import com.laytonsmith.core.exceptions.CRE.CREIllegalArgumentException;
import com.laytonsmith.core.exceptions.CRE.CRENullPointerException;
import com.laytonsmith.core.exceptions.CRE.CREThrowable;
import com.laytonsmith.core.natives.interfaces.Mixed;

import io.github.pieter12345.CHPexAPI.LifeCycle.PexFunction;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * Contains functions for PermissionsEx group management.
 * @author P.J.S. Kools
 */
public class CHPexGroupFunctions extends CHPexFunctions {
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_get_groups extends PexFunction {
		
		@Override
		public Mixed exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			CArray ret = new CArray(t);
			for(PermissionGroup permGroup : PermissionsEx.getPermissionManager().getGroupList()) {
				ret.push(new CString(permGroup.getIdentifier(), t), t);
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
			return "array {} Returns an array containing all groups in the pex config.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {0};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_get_group_options extends PexFunction {
		
		@Override
		public Mixed exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionGroup permGroup = getPexGroup(args[0], t);
			CArray ret = CArray.GetAssociativeArray(t);
			for(Entry<String, Map<String, String>> optionEntry : permGroup.getAllOptions().entrySet()) {
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
			return "array {group} Returns all options of the given group in an associative array in format:"
					+ " array('': optionArray, 'worldName': optionArray)"
					+ " where optionArray is an associative array with possible keys default, prefix and suffix."
					+ " Throws IllegalArgumentException when group is empty."
					+ " Throws NullPointerException when group is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {1};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_set_group_option extends PexFunction {
		
		@Override
		public Mixed exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionGroup permGroup = getPexGroup(args[0], t);
			// An empty key will overwrite the options array with an empty string, so disallow empty keys.
			String optionKey = convertNonNullNonEmptyStringArg(args[1], "optionKey", t);
			String optionValue = convertStringArg(args[2], "optionValue", t);
			String world = (args.length > 3 ? convertStringArg(args[3], "world", t) : null);
			if(world != null && !world.isEmpty()) {
				permGroup.setOption(optionKey, optionValue, world);
			} else {
				permGroup.setOption(optionKey, optionValue);
			}
			return CVoid.VOID;
		}
		
		@Override
		public String docs() {
			return "void {group, optionKey, optionValue, [world]} Sets the given option for the given group"
					+ " in the given or all worlds."
					+ " Option keys used by Pex are: default, prefix and suffix."
					+ " Setting the value to null will remove the option from the group."
					+ " The world argument will be ignored when it is null or empty."
					+ " Throws IllegalArgumentException when group or optionKey is empty."
					+ " Throws NullPointerException when group or optionKey is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {3, 4};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_get_group_permissions extends PexFunction {
		
		@Override
		public Mixed exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionGroup permGroup = getPexGroup(args[0], t);
			CArray ret = CArray.GetAssociativeArray(t);
			for(Entry<String, List<String>> permissionsEntry : permGroup.getAllPermissions().entrySet()) {
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
			return "array {group} Returns all permissions of the given group in an associative array in format:"
					+ " array('': permissionsArray, 'worldName': permissionsArray)"
					+ " where permissionsArray is an array of permission strings."
					+ " This does NOT include permissions a group has through inheritance of other groups."
					+ " Throws IllegalArgumentException when group is empty."
					+ " Throws NullPointerException when group is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {1};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_set_group_permissions extends PexFunction {
		
		@Override
		public Mixed exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionGroup permGroup = getPexGroup(args[0], t);
			
			// Get permissions.
			if(!(args[1] instanceof CArray)) {
				throw new CRECastException("Expecting the second argument of " + getName()
						+ "(group, permissionArray) to be an array, but found: " + args[1].typeof() + ".", t);
			}
			CArray permsArray = (CArray) args[1];
			if(permsArray.isAssociative()) {
				throw new CREIllegalArgumentException("Expecting the second argument of " + getName()
				+ "(group, permissionArray) to be a non-associative array.", t);
			}
			List<String> permsList = new ArrayList<>();
			for(Mixed perm : permsArray.asList()) {
				permsList.add(convertNonNullNonEmptyStringArg(perm, "permission", t));
			}
			
			// Get world.
			String world = (args.length > 2 ? convertStringArg(args[2], "world", t) : null);
			
			// Set permissions.
			if(world != null && !world.isEmpty()) {
				permGroup.setPermissions(permsList, world);
			} else {
				permGroup.setPermissions(permsList);
			}
			return CVoid.VOID;
		}
		
		@Override
		public String docs() {
			return "void {group, permissionArray, [world]}"
					+ " Sets the given permissions for the given group in the given or all worlds."
					+ " The world argument will be ignored when it is null or empty."
					+ " Throws IllegalArgumentException when group or one of the permissions is empty."
					+ " Throws NullPointerException when group or one of the permissions is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {2, 3};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_add_group_permission extends PexFunction {
		
		@Override
		public Mixed exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionGroup permGroup = getPexGroup(args[0], t);
			String perm = convertNonNullNonEmptyStringArg(args[1], "permission", t);
			String world = (args.length > 2 ? convertStringArg(args[2], "world", t) : null);
			if(world != null && !world.isEmpty()) {
				permGroup.addPermission(perm, world);
			} else {
				permGroup.addPermission(perm);
			}
			return CVoid.VOID;
		}
		
		@Override
		public String docs() {
			return "void {group, permission, [world]}"
					+ " Adds the given permissions to the given group in the given or all worlds."
					+ " The world argument will be ignored when it is null or empty."
					+ " Throws IllegalArgumentException when group or permission is empty."
					+ " Throws NullPointerException when group or permission is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {2, 3};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_remove_group_permission extends PexFunction {
		
		@Override
		public Mixed exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionGroup permGroup = getPexGroup(args[0], t);
			String perm = convertNonNullNonEmptyStringArg(args[1], "permission", t);
			String world = (args.length > 2 ? convertStringArg(args[2], "world", t) : null);
			if(world != null && !world.isEmpty()) {
				permGroup.removePermission(perm, world);
			} else {
				permGroup.removePermission(perm);
			}
			return CVoid.VOID;
		}
		
		@Override
		public String docs() {
			return "void {group, permission, [world]}"
					+ " Removes the given permissions from the given group in the given or all worlds."
					+ " The world argument will be ignored when it is null or empty."
					+ " Throws IllegalArgumentException when group or permission is empty."
					+ " Throws NullPointerException when group or permission is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {2, 3};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_get_group_users extends PexFunction {
		
		@Override
		public Mixed exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionGroup permGroup = getPexGroup(args[0], t);
			String world = (args.length > 1 ? convertStringArg(args[1], "world", t) : null);
			Set<PermissionUser> permUsers = (world == null || world.isEmpty()
					? permGroup.getUsers() : permGroup.getUsers(world));
			CArray groupUsers = new CArray(t);
			for(PermissionUser groupMember : permUsers) {
				groupUsers.push(new CString(groupMember.getIdentifier(), t), t);
			}
			return groupUsers;
		}
		
		@Override
		public String docs() {
			return "array {group, [world]} Returns an array containing the identifiers (name/uuid) of all players in"
					+ " the given group (excluding through inheritance). If the world is given, only members who are"
					+ " member of the given group in the given world are returned."
					+ " The world argument will be ignored when it is null or empty."
					+ " Throws IllegalArgumentException when group is empty."
					+ " Throws NullPointerException when group is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {1, 2};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_get_group_parents extends PexFunction {
		
		@Override
		public Mixed exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionGroup permGroup = getPexGroup(args[0], t);
			String world = (args.length > 1 ? convertStringArg(args[1], "world", t) : null);
			List<PermissionGroup> permGroups = (world == null || world.isEmpty()
					? permGroup.getOwnParents() : permGroup.getOwnParents(world));
			CArray parents = new CArray(t);
			for(PermissionGroup parent : permGroups) {
				parents.push(new CString(parent.getIdentifier(), t), t);
			}
			return parents;
		}
		
		@Override
		public String docs() {
			return "array {group, [world]} Returns an array containing the names of all direct parents of the given"
					+ " group, excluding world-specific parents."
					+ " When world is given, all parents specific to that world are added to the return array."
					+ " The world argument will be ignored when it is null or empty."
					+ " Throws IllegalArgumentException when group is empty."
					+ " Throws NullPointerException when group is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {1, 2};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_set_group_parents extends PexFunction {
		
		@Override
		public Mixed exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionGroup permGroup = getPexGroup(args[0], t);
			
			// Get groups.
			Mixed groups = args[1];
			if(groups instanceof CNull) {
				throw new CRENullPointerException("Parent array can not be null.", t);
			}
			if(!(groups instanceof CArray) || ((CArray) groups).isAssociative()) {
				throw new CRECastException(
						"Expecting a non-associative array for parents. Received: " + groups.typeof() + ".", t);
			}
			CArray groupsArray = (CArray) groups;
			List<String> parentNames = new ArrayList<String>((int) groupsArray.size());
			for(Mixed group : groupsArray.asList()) {
				parentNames.add(convertNonNullNonEmptyStringArg(group, "group", t));
			}
			
			// Get world.
			String world = (args.length > 2 ? convertStringArg(args[2], "world", t) : null);
			
			// Set parents (converting them to PermissionGroups is useless as they are set by name).
			if(world != null && !world.isEmpty()) {
				permGroup.setParentsIdentifier(parentNames, world);
			} else {
				permGroup.setParentsIdentifier(parentNames);
			}
			return CVoid.VOID;
		}
		
		@Override
		public String docs() {
			return "void {group, parentArray, [world]} Sets the parents of the given group in the given world"
					+ " or non-world specific if no world is given (affecting all worlds)."
					+ " The world argument will be ignored when it is null or empty."
					+ " Throws IllegalArgumentException when group or an element in parentArray is empty."
					+ " Throws NullPointerException when group or an element in parentArray is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {1, 2};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_add_group_parent extends PexFunction {
		
		@Override
		public Mixed exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionGroup permGroup = getPexGroup(args[0], t);
			String group = convertNonNullNonEmptyStringArg(args[1], "parent", t);
			String world = (args.length > 2 ? convertStringArg(args[2], "world", t) : null);
			
			
			// Get current world-specific parent groups.
			List<String> parentNames = (world != null && !world.isEmpty()
					? permGroup.getOwnParentIdentifiers(world) : permGroup.getOwnParentIdentifiers());
			
			// Add the new parent group.
			boolean added = parentNames.add(group);
			
			// Set parents (converting them to PermissionGroups is useless as they are set by name).
			if(added) {
				if(world != null && !world.isEmpty()) {
					permGroup.setParentsIdentifier(parentNames, world);
				} else {
					permGroup.setParentsIdentifier(parentNames);
				}
			}
			return CVoid.VOID;
		}
		
		@Override
		public String docs() {
			return "void {group, parent, [world]} Adds the given parent to the parent list of the given group"
					+ " in the given world or non-world specific if no world is given (affecting all worlds)."
					+ " The world argument will be ignored when it is null or empty."
					+ " Throws IllegalArgumentException when group or parent is empty."
					+ " Throws NullPointerException when group or parent is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {1, 2};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_remove_group_parent extends PexFunction {
		
		@Override
		public Mixed exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionGroup permGroup = getPexGroup(args[0], t);
			String group = convertNonNullNonEmptyStringArg(args[1], "parent", t);
			String world = (args.length > 2 ? convertStringArg(args[2], "world", t) : null);
			
			
			// Get current world-specific parent groups.
			List<String> parentNames = (world != null && !world.isEmpty()
					? permGroup.getOwnParentIdentifiers(world) : permGroup.getOwnParentIdentifiers());
			
			// Remove the given parent group.
			boolean removed = parentNames.remove(group);
			
			// Set parents (converting them to PermissionGroups is useless as they are set by name).
			if(removed) {
				if(world != null && !world.isEmpty()) {
					permGroup.setParentsIdentifier(parentNames, world);
				} else {
					permGroup.setParentsIdentifier(parentNames);
				}
			}
			return CVoid.VOID;
		}
		
		@Override
		public String docs() {
			return "void {group, parent, [world]} Removes the given parent from the parent list of the given group"
					+ " in the given world or non-world specific if no world is given (affecting all worlds)."
					+ " The world argument will be ignored when it is null or empty."
					+ " Throws IllegalArgumentException when group or parent is empty."
					+ " Throws NullPointerException when group or parent is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {1, 2};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_get_group_children extends PexFunction {
		
		@Override
		public Mixed exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			PermissionGroup permGroup = getPexGroup(args[0], t);
			String world = (args.length > 1 ? convertStringArg(args[1], "world", t) : null);
			List<PermissionGroup> permGroups = (world == null || world.isEmpty()
					? permGroup.getChildGroups() : permGroup.getChildGroups(world));
			CArray parents = new CArray(t);
			for(PermissionGroup child : permGroups) {
				parents.push(new CString(child.getIdentifier(), t), t);
			}
			return parents;
		}
		
		@Override
		public String docs() {
			return "array {group, [world]} Returns an array containing the names of all direct children of the given"
					+ " group, excluding world-specific children (groups that are only child of the given group in a"
					+ " specific world, other than the given world)."
					+ " The world argument will be ignored when it is null or empty."
					+ " Throws IllegalArgumentException when group is empty."
					+ " Throws NullPointerException when group is null.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {1, 2};
		}
	}
}
