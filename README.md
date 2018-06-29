# CHPexAPI
A CommandHelper extension that adds functions to interact with the PermissionsEx plugin API.

# Functions
## CHPexFunctions
### array pex\_get\_group\_children(group):
Returns the names of all children of the given group (excluding indirect children) in an array.

### array pex\_get\_group\_options(group):
Returns all options of the given group in an associative array in format: array('': optionArray, 'worldName': optionArray) where optionArray is an associative array with possible keys default, prefix and suffix.

### array pex\_get\_group\_parents(group):
Returns the names of all parents of the given group (excluding indirect parents) in an array.

### array pex\_get\_group\_permissions(group):
Returns all permissions of the given group in an associative array in format: array('': permissionArray, 'worldName': permissionArray) where permissionArray is an array of permission strings.

### array pex\_get\_group\_users(group, [world]):
Returns the identifiers (name/uuid) of all players in the given group (excluding through inheritance) in an array. If the world is given, only members who are member of the given group when they are in the given world are returned.

### array pex\_get\_groups():
Returns an array containing all groups in the pex config.

### array pex\_get\_user\_groups(player/uuid):
Returns an array containing all groups the player/uuid is in. Returns the default group if it has been set and no other groups were found.

### array pex\_get\_user\_options(player/uuid):
Returns all personal options of the given player/uuid in an associative array in format: array('': optionArray, 'worldName': optionArray) where optionArray is an associative array with possible keys name, prefix and suffix.

### array pex\_get\_user\_permissions(player/uuid):
Returns an array containing all personal permissions the player/uuid has in format: array('': personalPermissions, 'worldName': personalPermissions). This does NOT include permissions a player/uuid has through groups.

### array pex\_get\_users():
Returns an array containing all players/uuids in the pex config.

### string pex\_get\_uuid(player):
Returns the UUID of the given player or null if the player was not identified by a UUID in the pex permissions file.

### boolean pex\_has\_permission(player/uuid, permission, [world]):
Returns true if the player/uuid has the given permission in the given world, false otherwise. If no world is supplied or if world is null, the permission will be checked globally.

### array pex\_set\_group\_option(group, optionKey, optionValue):
Sets the given option for the given group in all worlds. Option keys used by Pex are: default, prefix and suffix. Setting the value to null will remove the option from the group.

### array pex\_set\_user\_groups(player/uuid, groupArray):
Sets the groups for the given player/uuid in all worlds.

### array pex\_set\_user\_option(player/uuid, optionKey, optionValue):
Sets the given option for the given player/uuid in all worlds. Option keys used by Pex are: name, prefix and suffix. Setting the value to null will remove the option from the player.

