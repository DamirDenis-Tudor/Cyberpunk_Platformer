package Scenes.InMenu;

import Components.DynamicComponents.MenuItems.Button;
import Enums.ComponentType;
import Enums.SceneType;
import Input.MouseInput;
import Scenes.Messages.Message;
import Scenes.Scene;
import Scenes.SceneHandler;
import Utils.Coordinate;
import Utils.Rectangle;
import Window.Camera;

final public class SettingsScene extends Scene {
    public SettingsScene(SceneHandler sceneHandler) {
        super(sceneHandler);
        components.add(new Button(this, ComponentType.Back,
                new Rectangle(new Coordinate<>(200, 800), 400, 100)));
    }

    @Override
    public void notify(Message message) throws Exception {
        switch (message.type()) {
            case SceneHasBeenActivated -> {
                Camera.getInstance().disableCameraOffset();
                MouseInput.getInstance().reset();
            }
            case ButtonClicked -> {
                switch (message.source()) {
                    case Back -> sceneHandler.handleSceneChangeRequest(SceneType.MainMenuScene);
                }
            }
        }
    }
}
