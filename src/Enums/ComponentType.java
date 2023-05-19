package Enums;

import java.io.Serializable;

/**
 * This class contains all possible base-types or subtypes of components.
 */
public enum ComponentType implements Serializable {
    NONE,

    /*
        ENTITIES
     */
    PLAYER, BIKER, PUNK, CYBORG,

    MAP, GREEN_CITY, INDUSTRIAL_CITY,

    GROUND_ENEMY, BASEBALL_ENEMY, SKATER_ENEMY, GUNNER_ENEMY, MACHINE_GUN_ENEMY , DRONE_ENEMY , ANIMAL, CAT_1, DOG_1, CAT_2, DOG_2, AIR_ENEMY, AIRPLANE,

    /*
        ITEMS
     */
    CHEST, GUN, BULLET, PLATFORM, HELICOPTER,DRONE,
    GUN_1, GUN_2, GUN_3, GUN_4, GUN_5, GUN_6, GUN_7, GUN_8, GUN_9, GUN_10,
    BULLET_1, BULLET_2, BULLET_3, BULLET_4, BULLET_5, BULLET_6, BULLET_7, BULLET_8, BULLET_9, BULLET_10,

    // SCENE RELATED
    SCENE_HANDLER, SCENE, WALLPAPER,

    // BUTTONS
    NEW_GAME_BUTTON, LOAD_BUTTON, SETTINGS_BUTTON, EXIT_BUTTON, BACK, BACK_TO_MENU,INVENTORY,
    CONTINUE, DELETE_SAVE, DELETE_LATEST_SAVE, LOAD_SAVE, SAVE_BUTTON, SAVE_INFO,BOX_INFO,

    // LEVELS,
    LEVEL_1, LEVEL_2, LEVEL_3, LEVEL_4

}
