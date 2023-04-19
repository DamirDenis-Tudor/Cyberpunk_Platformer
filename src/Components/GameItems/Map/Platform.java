package Components.GameItems.Map;

import Components.BaseComponents.AnimationHandler;
import Components.GameItems.DynamicComponent;
import Enums.*;
import Scenes.Messages.Message;
import Scenes.Scene;
import Utils.Coordinate;

import java.util.HashMap;
import java.util.Map;
import static Utils.Constants.platformVelocity;

public class Platform extends DynamicComponent {
    transient protected AnimationHandler animationHandler;
    protected final Map<ComponentStatus, Boolean> statuses;

    public Platform(Scene scene, Coordinate<Integer> position) {
        super();
        this.scene = scene;

        statuses = new HashMap<>();
        statuses.put(ComponentStatus.LeftCollision, false);
        statuses.put(ComponentStatus.RightCollision, false);

        animationHandler = new AnimationHandler();
        animationHandler.changeAnimation(AnimationType.Platform, new Coordinate<>(position));
        collideBox = animationHandler.getAnimation().getRectangle();
    }

    @Override
    public void notify(Message message) {
        switch (message.source()) {
            case Map, Platform -> {
                switch (message.type()) {
                    case LeftCollision -> {
                        statuses.put(ComponentStatus.LeftCollision, true);
                        statuses.put(ComponentStatus.RightCollision, false);
                        animationHandler.getAnimation().setDirection(true);
                    }
                    case RightCollision -> {
                        statuses.put(ComponentStatus.RightCollision, true);
                        statuses.put(ComponentStatus.LeftCollision, false);
                        animationHandler.getAnimation().setDirection(false);
                    }
                }
            }
        }
    }

    @Override
    public void interactionWith(Object object) {
        DynamicComponent component = (DynamicComponent) object;
        if (component.getGeneralType() == ComponentType.Platform) {
            collideBox.solveCollision(component.getCollideBox());
            if (collideBox.getDx() > 0) {
                component.notify(new Message(MessageType.RightCollision, ComponentType.Enemy, getId()));
                statuses.put(ComponentStatus.LeftCollision, true);
                statuses.put(ComponentStatus.RightCollision, false);
                animationHandler.getAnimation().setDirection(true);
            } else if (collideBox.getDx() < 0) {
                component.notify(new Message(MessageType.LeftCollision, ComponentType.Enemy, getId()));
                statuses.put(ComponentStatus.RightCollision, true);
                statuses.put(ComponentStatus.LeftCollision, false);
                animationHandler.getAnimation().setDirection(false);
            }
        } else if (component.getGeneralType() == ComponentType.Player) {
            if(animationHandler.getAnimation().getDirection()){
                component.getCollideBox().moveByX(platformVelocity);
            } else  {
                component.getCollideBox().moveByX(-platformVelocity);
            }

        }
    }

    @Override
    public void update() {
        if (!statuses.get(ComponentStatus.LeftCollision)) {
            collideBox.moveByX(-platformVelocity);
        } else if (!statuses.get(ComponentStatus.RightCollision)) {
            collideBox.moveByX(platformVelocity);
        }
        scene.notify(new Message(MessageType.HandleCollision, ComponentType.Platform, getId()));
        animationHandler.update();
    }

    @Override
    public void draw() {
        animationHandler.draw();
    }

    @Override
    public ComponentType getCurrentType() {
        return null;
    }

    @Override
    public ComponentType getGeneralType() {
        return ComponentType.Platform;
    }

    @Override
    public void addMissingPartsAfterDeserialization(Scene scene) {
        this.scene=scene;
        animationHandler = new AnimationHandler();
        animationHandler.changeAnimation(AnimationType.Platform, collideBox.getPosition());
    }
}
