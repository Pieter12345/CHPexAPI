# CHPexAPI
A CommandHelper extension that adds functions to interact with the PermissionsEx plugin API.

# Functions
## CHPexFunctions
### string pex\_get\_uuid(player):
Returns the UUID of the given player or null if the player was not identified by a UUID in the pex permissions file. Throws IllegalArgumentException when player is empty. Throws NullPointerException when player is null.

### boolean pex\_has\_permission(player/uuid, permission, [world]):
Returns true if the player/uuid has the given permission in the given world, false otherwise. If no world is supplied or if world is empty or null, the permission will be checked globally. Throws IllegalArgumentException when player is empty. Throws NullPointerException when player or permission is null.

## CHPexUserFunctions
### array pex\_get\_users():
Returns an array containing all players/uuids in the pex config.

### array pex\_get\_user\_options(player/uuid):
Returns all personal options of the given player/uuid in an associative array in format: array('': optionArray, 'worldName': optionArray) where optionArray is an associative array with possible keys name, prefix and suffix. Throws IllegalArgumentException when player/uuid is empty. Throws NullPointerException when player/uuid is null.

### void pex\_set\_user\_option(player/uuid, optionKey, optionValue, [world]):
Sets the given option for the given player/uuid in the given or all worlds. Option keys used by Pex are: name, prefix and suffix. Setting the value to null will remove the option from the player/uuid. The world argument will be ignored when it is null or empty. Throws IllegalArgumentException when player/uuid or optionKey is empty. Throws NullPointerException when player/uuid or optionKey is null.

### array pex\_get\_user\_permissions(player/uuid):
Returns an array containing all personal permissions the player/uuid has in format: array('': personalPermissions, 'worldName': personalPermissions). where personalPermissions is an array of permission strings. This does NOT include permissions a player/uuid has through groups. Throws IllegalArgumentException when player/uuid is empty. Throws NullPointerException when player/uuid is null.

### void pex\_set\_user\_permissions(player/uuid, permissionArray, [world):
Sets the given permissions for the given player/uuid in the given or all worlds. The world argument will be ignored when it is null or empty. Throws IllegalArgumentException when player/uuid or one of the permissions is empty. Throws NullPointerException when player/uuid or one of the permissions is null.

### void pex\_add\_user\_permission(player/uuid, permission, [world]):
Adds the given permissions to the given player/uuid in the given or all worlds. The world argument will be ignored when it is null or empty. Throws IllegalArgumentException when player/uuid or permission is empty. Throws NullPointerException when player/uuid or permission is null.

### void pex\_remove\_user\_permission(player/uuid, permission, [world]):
Removes the given permissions from the given player/uuid in the given or all worlds. The world argument will be ignored when it is null or empty. Throws IllegalArgumentException when player/uuid or permission is empty. Throws NullPointerException when player/uuid or permission is null.

### array pex\_get\_user\_groups(player/uuid, [world, [specificOnly]]):
Returns an array containing all groups the player/uuid has in the given world. There are three types of groups: default (from group configs), global (personal user config) and world-specific (personal user config). When 'world' is non-empty, the world-specific groups are included. When 'world' is empty, the global groups are included. 'world' defaults to ''. When 'specificOnly' is false, the global groups are included. When 'specificOnly' is false and no groups have been included so far, default groups are included. 'specificOnly' defaults to false. Throws IllegalArgumentException when player/uuid is empty. Throws NullPointerException when player/uuid is null.

### void pex\_set\_user\_groups(player/uuid, groupArray, [world]):
Sets the groups for the given player/uuid in the given or all worlds. The world argument will be ignored when it is null or empty. Throws IllegalArgumentException when player/uuid or a group in the groupArray is empty. Throws NullPointerException when player/uuid or a group in the groupArray is null.

### void pex\_add\_user\_group(player/uuid, group, [world]):
Adds the given player/uuid to the given group in the given or all worlds. The world argument will be ignored when it is null or empty. Throws IllegalArgumentException when player/uuid or group is empty. Throws NullPointerException when player/uuid or group is null.

### void pex\_remove\_user\_group(player/uuid, group, [world]):
Removes the given player/uuid from the given group in the given or all worlds. The world argument will be ignored when it is null or empty. Throws IllegalArgumentException when player/uuid or group is empty. Throws NullPointerException when player/uuid or group is null.

## CHPexGroupFunctions
### array pex\_get\_groups():
Returns an array containing all groups in the pex config.

### array pex\_get\_group\_options(group):
Returns all options of the given group in an associative array in format: array('': optionArray, 'worldName': optionArray) where optionArray is an associative array with possible keys default, prefix and suffix. Throws IllegalArgumentException when group is empty. Throws NullPointerException when group is null.

### void pex\_set\_group\_option(group, optionKey, optionValue, [world]):
Sets the given option for the given group in the given or all worlds. Option keys used by Pex are: default, prefix and suffix. Setting the value to null will remove the option from the group. The world argument will be ignored when it is null or empty. Throws IllegalArgumentException when group or optionKey is empty. Throws NullPointerException when group or optionKey is null.

### array pex\_get\_group\_permissions(group):
Returns all permissions of the given group in an associative array in format: array('': permissionsArray, 'worldName': permissionsArray) where permissionsArray is an array of permission strings. This does NOT include permissions a group has through inheritance of other groups. Throws IllegalArgumentException when group is empty. Throws NullPointerException when group is null.

### void pex\_set\_group\_permissions(group, permissionArray, [world]):
Sets the given permissions for the given group in the given or all worlds. The world argument will be ignored when it is null or empty. Throws IllegalArgumentException when group or one of the permissions is empty. Throws NullPointerException when group or one of the permissions is null.

### void pex\_add\_group\_permission(group, permission, [world]):
Adds the given permissions to the given group in the given or all worlds. The world argument will be ignored when it is null or empty. Throws IllegalArgumentException when group or permission is empty. Throws NullPointerException when group or permission is null.

### void pex\_remove\_group\_permission(group, permission, [world]):
Removes the given permissions from the given group in the given or all worlds. The world argument will be ignored when it is null or empty. Throws IllegalArgumentException when group or permission is empty. Throws NullPointerException when group or permission is null.

### array pex\_get\_group\_users(group, [world]):
Returns an array containing the identifiers (name/uuid) of all players in the given group (excluding through inheritance). If the world is given, only members who are member of the given group in the given world are returned. The world argument will be ignored when it is null or empty. Throws IllegalArgumentException when group is empty. Throws NullPointerException when group is null.

### array pex\_get\_group\_parents(group, [world]):
Returns an array containing the names of all direct parents of the given group, excluding world-specific parents. When world is given, all parents specific to that world are added to the return array. The world argument will be ignored when it is null or empty. Throws IllegalArgumentException when group is empty. Throws NullPointerException when group is null.

### void pex\_set\_group\_parents(group, parentArray, [world]):
Sets the parents of the given group in the given world or non-world specific if no world is given (affecting all worlds). The world argument will be ignored when it is null or empty. Throws IllegalArgumentException when group or an element in parentArray is empty. Throws NullPointerException when group or an element in parentArray is null.

### void pex\_add\_group\_parent(group, parent, [world]):
Adds the given parent to the parent list of the given group in the given world or non-world specific if no world is given (affecting all worlds). The world argument will be ignored when it is null or empty. Throws IllegalArgumentException when group or parent is empty. Throws NullPointerException when group or parent is null.

### void pex\_remove\_group\_parent(group, parent, [world]):
Removes the given parent from the parent list of the given group in the given world or non-world specific if no world is given (affecting all worlds). The world argument will be ignored when it is null or empty. Throws IllegalArgumentException when group or parent is empty. Throws NullPointerException when group or parent is null.

### array pex\_get\_group\_children(group, [world]):
Returns an array containing the names of all direct children of the given group, excluding world-specific children (groups that are only child of the given group in a specific world, other than the given world). The world argument will be ignored when it is null or empty. Throws IllegalArgumentException when group is empty. Throws NullPointerException when group is null.
