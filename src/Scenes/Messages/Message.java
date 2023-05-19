package Scenes.Messages;

import Enums.ComponentType;
import Enums.MessageType;

/**
 * This class encapsulates the information that can be shared between components or scenes.
 */
public record Message(MessageType type, ComponentType source, int componentId) {}
