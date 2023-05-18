package Enums;

/**
 * This class facilitates component interaction by providing flexibility in adding new behavioral interactions.
 */
public enum MessageType {
    // Components messages
    HANDLE_COLLISION, HAS_COLLISION, DESTROY, ACTIVATE_TOP_COLLISION,
    ACTIVATE_BOTTOM_COLLISION, DEACTIVATE_BOTTOM_COLLISION, IS_ON_LADDER,
    IS_NO_LONGER_ON_LADDER, LEFT_COLLISION, RIGHT_COLLISION, LEFT_COLLISION_WITH_OTHER,
    RIGHT_COLLISION_WITH_OTHER, ATTACK, PLAYER_DEATH, READY_TO_BE_OPENED, SPAWN_GUN,
    IS_PICKED_UP, LAUNCH_BULLET, PLAYER_DIRECTION_RIGHT, PLAYER_DIRECTION_LEFT,DISABLE_GUN,ENABLE_GUN,
    BULLET_LAUNCH_RIGHT, BULLET_LAUNCH_LEFT, HIDE_GUN, SHOW_GUN, SHOOT, ON_PLATFORM,
    ON_HELICOPTER, DETACHED_FROM_HELICOPTER, CLEAR_INVENTORY, WEAPON_IS_SELECTED,HAS_PLAYER_COLLISION,


    // Scenes Messages
    SCENE_HAS_BEEN_ACTIVATED, BUTTON_CLICKED,
    NEW_GAME, SAVE_GAME, LOAD_GAME,
    SAVE_HAS_BEEN_ADDED, GUN_NEEDS_RECALIBRATION,
    CYBORG_SELECTED, BIKER_SELECTED, PUNK_SELECTED,
    GREEN_MAP_SELECTED, INDUSTRIAL_MAP_SELECTED
}
