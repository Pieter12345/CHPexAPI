package io.github.pieter12345.CHPexAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.laytonsmith.annotations.api;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CBoolean;
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

public class CHPexFunctions {
	
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
			String player = args[0].nval();
			if(player == null || player.isEmpty()) {
				throw new CREIllegalArgumentException("Player may not be empty or null.", t);
			}
			PermissionUser permUser = PermissionsEx.getPermissionManager().getUser(player);
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
		@SuppressWarnings("unchecked")
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {CREIllegalArgumentException.class};
		}
		
		@Override
		public String docs() {
			return "array {player/uuid} Returns all personal options of the given player/uuid in an associative array in format:"
					+ " array('': optionArray, 'worldName': optionArray)"
					+ " where optionArray is an associative array with possible keys name, prefix and suffix.";
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
			String player = args[0].nval();
			String optionKey = args[1].nval();
			String optionValue = args[2].nval();
			if(player == null || player.isEmpty()) {
				throw new CREIllegalArgumentException("Player may not be empty or null.", t);
			}
			if(optionKey == null || optionKey.isEmpty()) {
				throw new CREIllegalArgumentException("optionKey may not be empty or null.", t);
			}
			PermissionUser permUser = PermissionsEx.getPermissionManager().getUser(player);
			permUser.setOption(optionKey, optionValue);
			return CVoid.VOID;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {CREIllegalArgumentException.class};
		}
		
		@Override
		public String docs() {
			return "array {player/uuid, optionKey, optionValue} Sets the given option for the given player/uuid in all worlds."
					+ " Option keys used by Pex are: name, prefix and suffix."
					+ " Setting the value to null will remove the option from the player.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {3};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_get_user_permissions extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			String player = args[0].nval();
			if(player == null || player.isEmpty()) {
				throw new CREIllegalArgumentException("Player may not be empty or null.", t);
			}
			PermissionUser permUser = PermissionsEx.getPermissionManager().getUser(player);
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
		@SuppressWarnings("unchecked")
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {CREIllegalArgumentException.class};
		}
		
		@Override
		public String docs() {
			return "array {player/uuid} Returns an array containing all personal permissions the player/uuid has in format:"
					+ " array('': personalPermissions, 'worldName': personalPermissions)."
					+ " This does NOT include permissions a player/uuid has through groups.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {1};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_get_user_groups extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			String player = args[0].nval();
			if(player == null || player.isEmpty()) {
				throw new CREIllegalArgumentException("Player may not be empty or null.", t);
			}
			PermissionUser permUser = PermissionsEx.getPermissionManager().getUser(player);
			CArray ret = new CArray(t);
			for(PermissionGroup permGroup : permUser.getParents()) {
				ret.push(new CString(permGroup.getName(), t), t);
			}
			return ret;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {CREIllegalArgumentException.class};
		}
		
		@Override
		public String docs() {
			return "array {player/uuid} Returns an array containing all groups the player/uuid is in."
					+ " Returns the default group if it has been set and no other groups were found.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {1};
		}
	}

	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_set_user_groups extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			String player = args[0].nval();
			Construct groups = args[1];
			if(player == null || player.isEmpty()) {
				throw new CREIllegalArgumentException("Player may not be empty or null.", t);
			}
			if(groups == null) {
				throw new CRENullPointerException("Groups may not be null.", t);
			}
			if(!(groups instanceof CArray) || ((CArray) groups).isAssociative()) {
				throw new CRECastException("Expecting a non-associative array for groups. Received: " + groups.typeof() + ".", t);
			}
			CArray groupsArray = (CArray) groups;
			PermissionUser permUser = PermissionsEx.getPermissionManager().getUser(player);
			List<PermissionGroup> parents = new ArrayList<PermissionGroup>((int) groupsArray.size());
			for(Construct group : groupsArray.asList()) {
				if(!(group instanceof CString)) {
					throw new CRECastException("Expecting group names to be non-null strings. Received: " + group.typeof() + ".", t);
				}
				parents.add(PermissionsEx.getPermissionManager().getGroup(group.nval()));
			}
			permUser.setParents(parents);
			return CVoid.VOID;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {CREIllegalArgumentException.class, CRENullPointerException.class, CRECastException.class};
		}
		
		@Override
		public String docs() {
			return "array {player/uuid, groupArray} Sets the groups for the given player/uuid in all worlds.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {2};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_get_groups extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
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
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			String group = args[0].nval();
			if(group == null || group.isEmpty()) {
				throw new CREIllegalArgumentException("Group may not be empty or null.", t);
			}
			PermissionGroup permGroup = PermissionsEx.getPermissionManager().getGroup(group);
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
		@SuppressWarnings("unchecked")
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {CREIllegalArgumentException.class};
		}
		
		@Override
		public String docs() {
			return "array {group} Returns all options of the given group in an associative array in format:"
					+ " array('': optionArray, 'worldName': optionArray)"
					+ " where optionArray is an associative array with possible keys default, prefix and suffix.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {1};
		}
	}

	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_set_group_option extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			String group = args[0].nval();
			String optionKey = args[1].nval();
			String optionValue = args[2].nval();
			if(group == null || group.isEmpty()) {
				throw new CREIllegalArgumentException("Group may not be empty or null.", t);
			}
			if(optionKey == null || optionKey.isEmpty()) {
				throw new CREIllegalArgumentException("optionKey may not be empty or null.", t);
			}
			PermissionGroup permGroup = PermissionsEx.getPermissionManager().getGroup(group);
			permGroup.setOption(optionKey, optionValue);
			return CVoid.VOID;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {CREIllegalArgumentException.class};
		}
		
		@Override
		public String docs() {
			return "array {group, optionKey, optionValue} Sets the given option for the given group in all worlds."
					+ " Option keys used by Pex are: default, prefix and suffix."
					+ " Setting the value to null will remove the option from the group.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {3};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_get_group_permissions extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			String group = args[0].nval();
			if(group == null || group.isEmpty()) {
				throw new CREIllegalArgumentException("Group may not be empty or null.", t);
			}
			PermissionGroup permGroup = PermissionsEx.getPermissionManager().getGroup(group);
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
		@SuppressWarnings("unchecked")
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {CREIllegalArgumentException.class};
		}
		
		@Override
		public String docs() {
			return "array {group} Returns all permissions of the given group in an associative array in format:"
					+ " array('': permissionArray, 'worldName': permissionArray)"
					+ " where permissionArray is an array of permission strings.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {1};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_get_group_users extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			String group = args[0].nval();
			if(group == null || group.isEmpty()) {
				throw new CREIllegalArgumentException("Group may not be empty or null.", t);
			}
			String world = null;
			if(args.length == 2) {
				if(!(args[1] instanceof CString) && !(args[1] instanceof CNull)) {
					throw new CRECastException("The second argument of " + getName() + "(group, world) has to be a string.", t);
				}
				world = args[1].nval();
			}
			PermissionGroup permGroup = PermissionsEx.getPermissionManager().getGroup(group);
			CArray groupMembers = new CArray(t);
			for(PermissionUser groupMember : (world == null ? permGroup.getUsers() : permGroup.getUsers(world))) {
				groupMembers.push(new CString(groupMember.getIdentifier(), t), t);
			}
			return groupMembers;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {CREIllegalArgumentException.class, CRECastException.class};
		}
		
		@Override
		public String docs() {
			return "array {group, [world]} Returns the identifiers (name/uuid) of all players in the given group"
					+ " (excluding through inheritance) in an array. If the world is given, only members who are"
					+ " member of the given group when they are in the given world are returned.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {1, 2};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_get_group_parents extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			String group = args[0].nval();
			if(group == null || group.isEmpty()) {
				throw new CREIllegalArgumentException("Group may not be empty or null.", t);
			}
			PermissionGroup permGroup = PermissionsEx.getPermissionManager().getGroup(group);
			CArray parents = new CArray(t);
			for(PermissionGroup parent : permGroup.getOwnParents()) {
				parents.push(new CString(parent.getIdentifier(), t), t);
			}
			return parents;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {CREIllegalArgumentException.class};
		}
		
		@Override
		public String docs() {
			return "array {group} Returns the names of all parents of the given group (excluding indirect parents) in an array.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {1};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_get_group_children extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			String group = args[0].nval();
			if(group == null || group.isEmpty()) {
				throw new CREIllegalArgumentException("Group may not be empty or null.", t);
			}
			PermissionGroup permGroup = PermissionsEx.getPermissionManager().getGroup(group);
			CArray parents = new CArray(t);
			for(PermissionGroup child : permGroup.getChildGroups()) {
				parents.push(new CString(child.getIdentifier(), t), t);
			}
			return parents;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {CREIllegalArgumentException.class};
		}
		
		@Override
		public String docs() {
			return "array {group} Returns the names of all children of the given group (excluding indirect children) in an array.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {1};
		}
	}
	
	@api(environments = {CommandHelperEnvironment.class})
	public static class pex_get_uuid extends PexFunction {
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Static.checkPlugin("PermissionsEx", t);
			String player = args[0].nval();
			if(player == null || player.isEmpty()) {
				throw new CREIllegalArgumentException("Player may not be empty or null.", t);
			}
			PermissionUser permUser = PermissionsEx.getPermissionManager().getUser(player);
			String identifier = permUser.getIdentifier();
			if(player.equals(identifier)) {
				return CNull.NULL;
			} else {
				return new CString(identifier, t);
			}
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {CREIllegalArgumentException.class};
		}
		
		@Override
		public String docs() {
			return "string {player} Returns the UUID of the given player or null if the player was not identified by a UUID in the pex permissions file.";
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
			String player = args[0].nval();
			if(!(args[1] instanceof CString)) {
				throw new CRECastException("The second argument of " + getName() + "(player/uuid, permission, [world]) has to be a string.", t);
			}
			String permission = args[1].nval();
			String world = null;
			if(args.length == 3) {
				if(!(args[2] instanceof CString) && !(args[2] instanceof CNull)) {
					throw new CRECastException("The third argument of " + getName() + "(player/uuid, permission, world) has to be a string.", t);
				}
				world = args[2].nval();
			}
			if(player == null || player.isEmpty()) {
				throw new CREIllegalArgumentException("Player may not be empty or null.", t);
			}
			if(permission == null || permission.isEmpty()) {
				throw new CREIllegalArgumentException("Permission may not be empty or null.", t);
			}
			PermissionUser permUser = PermissionsEx.getPermissionManager().getUser(player);
			return CBoolean.get((world == null ? permUser.has(permission) : permUser.has(permission, world)));
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {CREIllegalArgumentException.class, CRECastException.class};
		}
		
		@Override
		public String docs() {
			return "boolean {player/uuid, permission, [world]} Returns true if the player/uuid has the given permission in the given world, false otherwise."
					+ " If no world is supplied or if world is null, the permission will be checked globally.";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[] {2, 3};
		}
	}
	
}
