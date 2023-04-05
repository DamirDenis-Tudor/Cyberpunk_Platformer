package Scenes.InGame;

import Components.DinamicComponents.Enemy;
import Components.DinamicComponents.Items.Chest;
import Components.DinamicComponents.Items.Gun;
import Components.DinamicComponents.Player;
import Components.DinamicComponents.DynamicComponent;
import Components.DinamicComponents.Map.GameMap;
import Components.StaticComponents.AssetsDeposit;
import Enums.ComponentType;
import Enums.MessageType;
import Enums.Weapon;
import Scenes.Messages.Message;
import Scenes.Scene;
import Utils.Coordinate;

import java.util.Random;

import static Enums.ComponentType.*;
import static Enums.MapType.GreenCityMap;
// TODO : Narator(DynamicComponent) -> for tutorial
final public class PlayScene extends Scene {
    Random rand = new Random(1000);
    public PlayScene() throws Exception {
        super();
        /*
             add the components specific to the scene
         */
        GameMap map = AssetsDeposit.getInstance().getGameMap(GreenCityMap);
        addComponent(map);

        for (Coordinate<Integer> position : map.getEnemiesPositions()) {
            ComponentType type = null;
            switch (rand.nextInt(2)) {
                case 0 -> type = BaseballEnemy;
                case 1 -> type = SkaterEnemy;
            }
            addComponent(new Enemy(this, position, type));
        }
        for (Coordinate<Integer> position : map.getAnimalsPositions()) {
            ComponentType type = null;
            switch (rand.nextInt(4)) {
                case 0 -> type = Dog1;
                case 1 -> type = Dog2;
                case 2 -> type = Cat1;
                case 3 -> type = Cat2;
            }
            addComponent(new Enemy(this, position, type));
        }

        for (Coordinate<Integer> position : map.getChestsPositions()) {
            addComponent(new Chest(this, position));
        }


        addComponent(new Player(this, map.getPlayerPosition(), Punk));
    }

    @Override
    public void notify(Message message) throws Exception {
        /*
            handle different request from components
         */
        switch (message.getSource()) {
            case Player -> {
                switch (message.getType()) {
                    case HandleCollision -> {
                        findComponent(Map).handleInteractionWith(findComponent(Player));
                        for (DynamicComponent component : getAllComponentsWithName(Enemy)) {
                            if (component.getActiveStatus()) {
                                findComponent(Player).handleInteractionWith(component);
                                component.handleInteractionWith(findComponent(Player));
                            }
                        }
                    }
                    case PlayerDeath -> {
                        for (DynamicComponent component : getAllComponentsWithName(Enemy)) {
                            if (component.getActiveStatus()) {
                                component.notify(new Message(MessageType.PlayerDeath, Player, message.getComponentId()));
                            }
                        }
                    }
                }
            }
            case Enemy -> {
                switch (message.getType()) {
                    case HandleCollision -> {
                        DynamicComponent component = findComponentWithId(message.getComponentId());
                        if (findComponentWithId(message.getComponentId()).getActiveStatus()) {
                            findComponent(Map).handleInteractionWith(component);
                            for (DynamicComponent otherComponent : getAllComponentsWithName(Enemy)) {
                                if (otherComponent.getActiveStatus()) {
                                    if (component != otherComponent) {
                                        component.handleInteractionWith(otherComponent);
                                    }
                                }
                            }
                        }
                    }
                    case EnemyDeath -> {
                        for (DynamicComponent component : getAllComponentsWithName(Enemy)) {
                            if (component.getActiveStatus()) {
                                findComponentWithId(message.getComponentId()).notify(message);
                            }
                        }
                    }
                }
            }
            case Chest -> {
                switch (message.getType()) {
                    case HandleCollision ->
                            findComponent(Player).handleInteractionWith(findComponentWithId(message.getComponentId()));
                    case SpawnGun -> {
                        Weapon type = null;
                        switch (rand.nextInt(10)){
                            case 0-> type = Weapon.Gun1;
                            case 1-> type = Weapon.Gun2;
                            case 2-> type = Weapon.Gun3;
                            case 3-> type = Weapon.Gun4;
                            case 4-> type = Weapon.Gun5;
                            case 5-> type = Weapon.Gun6;
                            case 6-> type = Weapon.Gun7;
                            case 7-> type = Weapon.Gun8;
                            case 8-> type = Weapon.Gun9;
                            case 9-> type = Weapon.Gun10;
                        }
                        addComponent(new Gun(this, AssetsDeposit.getInstance().getGun(type),
                                findComponentWithId(message.getComponentId()).getCollideBox().getPosition()));
                    }
                }
            }

            case Gun ->{
                switch (message.getType()){
                    case HandleCollision -> {
                        findComponent(Player).handleInteractionWith(findComponentWithId(message.getComponentId()));
                        findComponentWithId(message.getComponentId()).handleInteractionWith(findComponent(Player));
                    }
                    case LaunchBullet -> {
                        System.out.println("lauch bullet");
                    }
                }
            }
        }
    }
}

