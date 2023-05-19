package Components.MenuComponents;

import Components.BaseComponents.AssetsDeposit;
import Components.BaseComponents.ImageWrapper;
import Components.Notifiable;
import Components.StaticComponent;
import Enums.ColorType;
import Enums.ComponentType;
import Enums.MessageType;
import Scenes.Messages.Message;
import Scenes.Scene;
import Utils.Coordinate;
import Utils.Rectangle;

import java.awt.*;
import java.util.*;
import java.util.List;

import static Utils.Constants.FIRST_ITEM;
import static Utils.Constants.MAX_ITEMS_NUMBER;

public class Inventory implements StaticComponent, Notifiable {
    private final Notifiable scene;
    private final List<ComponentType> gunsTypes;
    private final Map<Integer, StaticComponent> items;
    private Integer itemsNumber;
    private Integer selectedWeaponId ;

    public Inventory(Notifiable scene) {
        this.scene = scene;
        gunsTypes = new LinkedList<>();
        items = new LinkedHashMap<>();

        // add boxes
        items.put(0, new BoxItem(this, new Rectangle(new Coordinate<>(900, 750), 150, 100)));
        items.put(1, new BoxItem(this, new Rectangle(new Coordinate<>(1075, 750), 150, 100)));
        items.put(2, new BoxItem(this, new Rectangle(new Coordinate<>(1250, 750), 150, 100)));
        items.put(3, new BoxItem(this, new Rectangle(new Coordinate<>(1425, 750), 150, 100)));
        items.put(4, new BoxItem(this, new Rectangle(new Coordinate<>(1600, 750), 150, 100)));
    }

    @Override
    public void notify(Message message) {
        switch (message.type()) {
            case IS_PICKED_UP -> {
                ImageWrapper item = new ImageWrapper(AssetsDeposit.get().getGun(message.source()).getImageWrapper());
                Rectangle rectangle = ((BoxItem) items.get(itemsNumber++)).getRectangle();

                if (itemsNumber <= MAX_ITEMS_NUMBER) {
                    item.setScale(3).setPosition(new Coordinate<>(rectangle.getCenterX() - item.getRectangle().getWidth() / 2,
                            rectangle.getCenterY() - item.getRectangle().getHeight() / 2));
                    gunsTypes.add(message.source());
                    items.put(message.componentId(), item);
                    if (itemsNumber == FIRST_ITEM) {
                        selectedWeaponId = message.componentId();
                        scene.notify(new Message(MessageType.WEAPON_IS_SELECTED, message.source(), message.componentId()));
                    }
                }
            }
            case BUTTON_CLICKED -> {
                int counter = 0 ;
                for (Integer key : items.keySet()) {
                    if (counter++ == MAX_ITEMS_NUMBER + message.componentId()) {
                        scene.notify(new Message(MessageType.DISABLE_GUN , ComponentType.INVENTORY   , selectedWeaponId));
                        selectedWeaponId = key;
                        scene.notify(new Message(MessageType.WEAPON_IS_SELECTED, gunsTypes.get(message.componentId()), key));
                        break;
                    }
                }
            }
            case CLEAR_INVENTORY -> {
                int counter = 0;
                List<Integer> toBeRemoved = new ArrayList<>();
                for (Map.Entry<Integer, StaticComponent> item : items.entrySet()) {
                    if (counter >= MAX_ITEMS_NUMBER) {
                        toBeRemoved.add(item.getKey());
                    }
                    counter++;
                }
                for (Integer id : toBeRemoved) {
                    items.remove(id);
                }
                itemsNumber = 0;
                selectedWeaponId = 0;
            }
        }
    }

    @Override
    public void update() {
        for (Map.Entry<Integer, StaticComponent> item : items.entrySet()) {
            item.getValue().update();
        }
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        for (Map.Entry<Integer, StaticComponent> item : items.entrySet()) {
            item.getValue().draw(graphics2D);
        }
    }
}
