package Components.DinamicComponents.Map;

import Components.DinamicComponents.DinamicComponent;
import Components.StaticComponents.ParallaxWallpaper;
import Enums.ComponentType;
import Enums.MessageType;
import Scenes.Messages.Message;
import Utils.Coordinate;
import Utils.Rectangle;
import Window.Camera;
import Window.GameWindow;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

import static Utils.Constants.mapDim;
import static Utils.Constants.mapScale;

/**
 * This class contains all the information about the game map:
 * matrix of objects|tiles, dimensions, predefined object positions.
 */
public class GameMap extends DinamicComponent {
    private final GameWindow gameWindow = GameWindow.getInstance();
    private int width; // lines
    private int height; // columns
    private Map<String, MapAsset> tiles; // String - id , Tile
    private Map<String, MapAsset> objects; // String - id , Object
    private String[][] tilesIndexes; // indexes for tiles
    private String[][] objectsIndexes; // indexes for objects
    private ParallaxWallpaper background; // parallax background
    private Map<String, List<Rectangle>> entitiesCoordinates; // map of the preloaded entities

    boolean wasGroundCollision = false;
    boolean wasTopCollision = false;
    boolean wasLeftCollision = false;
    boolean wasRightCollision = false;

    public GameMap(String path) {
        try {
            //   first initialize the document element
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            //  then create the parsed document
            Document document = builder.parse(new File(path));
            document.getDocumentElement().normalize();
            //  create the root element
            Element root = document.getDocumentElement();

            // -------------------------------------
            // first load the tiles by reading all
            // the necessary information
            tiles = new HashMap<>();
            Element source = (Element) root.getElementsByTagName("tileset").item(0);

            Document tilesDocument = builder.parse(new File(source.getAttribute("source").replace("..", "src/Resources")));
            Element tilesRoot = tilesDocument.getDocumentElement();
            NodeList elements = tilesRoot.getElementsByTagName("tile");

            //   then for each element map asset(tile) is created
            //   and added on the tiles map
            for (int index = 0; index < elements.getLength(); ++index) {
                Element tileElement = (Element) elements.item(index);
                Element imageElement = (Element) tileElement.getFirstChild().getNextSibling();

                String tileSource = imageElement.getAttribute("source").replace("..", "src/Resources");
                String tileId = Integer.toString(Integer.parseInt(tileElement.getAttribute("id")) + 1);
                int tileWidth = (int) (Integer.parseInt(imageElement.getAttribute("width")) * mapScale);
                int tileHeight = (int) (Integer.parseInt(imageElement.getAttribute("height")) * mapScale);

                tiles.put(tileId, new MapAsset(tileSource, tileWidth, tileHeight));
            }

            // -------------------------------------
            // load the parallax background
            // and all the necessary information
            background = new ParallaxWallpaper();

            Element backGroundSource = (Element) root.getElementsByTagName("tileset").item(1);
            Document backgroundDocument = builder.parse(new File(backGroundSource.getAttribute("source").replace("..", "src/Resources")));
            document.getDocumentElement().normalize();

            Element backgroundRoot = backgroundDocument.getDocumentElement();

            NodeList backgrounds = backgroundRoot.getElementsByTagName("tile");

            //   then for each element an image
            //   is added in the background object
            for (int index = 0; index < backgrounds.getLength(); ++index) {
                Element tileElement = (Element) backgrounds.item(index);
                Element imageElement = (Element) tileElement.getFirstChild().getNextSibling();
                background.addImage(ImageIO.read(new File(imageElement.getAttribute("source").replace("../", "src/Resources/"))));
            }

            // -------------------------------------
            // load the objects by reading all
            // the necessary information

            objects = new HashMap<>();

            Element objSource = (Element) root.getElementsByTagName("tileset").item(2);
            String lastMapId = objSource.getAttribute("firstgid");
            Document objectsDocument = builder.parse(new File(objSource.getAttribute("source").replace("..", "src/Resources")));
            Element objectsRoot = objectsDocument.getDocumentElement();
            NodeList objectsElements = objectsRoot.getElementsByTagName("tile");

            // for each object element read the information
            // necessary and add the object in the dedicated map
            for (int index = 0; index < objectsElements.getLength(); ++index) {
                Element objectElement = (Element) objectsElements.item(index);
                Element imageElement = (Element) objectElement.getFirstChild().getNextSibling();

                String objectSource = imageElement.getAttribute("source").replace("..", "src/Resources");
                String objectId = Integer.toString(Integer.parseInt(objectElement.getAttribute("id")) + Integer.parseInt(lastMapId));
                int objectWidth = (int) (Integer.parseInt(imageElement.getAttribute("width")) * mapScale);
                int objectHeight = (int) (Integer.parseInt(imageElement.getAttribute("height")) * mapScale);

                objects.put(objectId, new MapAsset(objectSource, objectWidth, objectHeight));
            }


            // -------------------------------------
            // it's the time to load the
            // first layer(tiles layer) matrix of indexes
            Element layer = (Element) root.getElementsByTagName("layer").item(0);
            height = Integer.parseInt(layer.getAttribute("height"));
            width = Integer.parseInt(layer.getAttribute("width"));

            String buffer = root.getElementsByTagName("data").item(0).
                    getFirstChild().getTextContent();

            String[] rows = buffer.split("\n"); // split the string into rows

            height = rows.length - 1;
            width = rows[1].split(",").length;

            tilesIndexes = new String[height][width];

            for (int i = 0; i < height; i++) {
                String[] cols = rows[i + 1].split(",");
                System.arraycopy(cols, 0, tilesIndexes[i], 0, width);
            }

            // -------------------------------------
            // it's the time to load the
            // second layer(objects layer) matrix of indexes
            String objectsBuffer = root.getElementsByTagName("data").item(1).
                    getFirstChild().getTextContent();

            String[] objectsRows = objectsBuffer.split("\n"); // split the string into rows

            objectsIndexes = new String[height][width];

            for (int i = 0; i < height; i++) {
                String[] objectsCols = objectsRows[i + 1].split(",");
                System.arraycopy(objectsCols, 0, objectsIndexes[i], 0, width);
            }

            // -------------------------------------
            // few more code and the map is finally added
            // now load the predefined positions

            entitiesCoordinates = new HashMap<>();
            NodeList positions = root.getElementsByTagName("objectgroup");

            for (int index = 0; index < positions.getLength(); ++index) {
                Node objectgroupNode = positions.item(index);

                Element objectgroupElement = (Element) objectgroupNode;
                String objectgroupName = objectgroupElement.getAttribute("name");
                NodeList objectList = objectgroupElement.getElementsByTagName("object");
                List<Rectangle> list = new ArrayList<>();

                for (int objectIndex = 0; objectIndex < objectList.getLength(); ++objectIndex) {
                    Node objectNode = objectList.item(objectIndex);
                    Element objectElement = (Element) objectNode;
                    int x = (int) (Float.parseFloat(objectElement.getAttributeNode("x").getValue()) * mapScale);
                    int y = (int) (Float.parseFloat(objectElement.getAttributeNode("y").getValue()) * mapScale);
                    int w = (int) (Float.parseFloat(objectElement.getAttributeNode("width").getValue()) * mapScale);
                    int h = (int) (Float.parseFloat(objectElement.getAttributeNode("height").getValue()) * mapScale);
                    list.add(new Rectangle(new Coordinate<Integer>(x,y),w, h));

                    entitiesCoordinates.put(objectgroupName, list);
                }
            }

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void notify(Message message) {
        if (message.getSource() == ComponentType.Player) {
            //wasGroundCollision = MessageNames.ActivateBottomCollision == message.getType();
        }
    }

    @Override
    public void update() throws Exception {
        background.update();
    }

    @Override
    public void draw() {
        /*
         * drawing the parallax background
         */
        background.draw();

        /*
         * drawing the tiles
         */
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; j++) {
                if (!Objects.equals(tilesIndexes[i][j], "0")) {
                    MapAsset asset = tiles.get(tilesIndexes[i][j]);
                    int xPos = j * mapDim + Camera.getInstance().getCurrentXoffset();
                    int yPos = i * mapDim;
                    int dimW = asset.getWidth();
                    int dimH = asset.getHeight();

                    gameWindow.getGraphics().drawImage(asset.getImage(), xPos, yPos, dimW, dimH, null);
                    //   gameWindow.getGraphics().drawRect(xPos, yPos, dimW, dimH);
                }
            }
        }

        /*
         * drawing the objects
         */
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; j++) {
                if (!Objects.equals(objectsIndexes[i][j], "0")) {
                    MapAsset asset = objects.get(objectsIndexes[i][j]);
                    int dimW = asset.getWidth();
                    int dimH = asset.getHeight();
                    int xPos = j * mapDim + Camera.getInstance().getCurrentXoffset();
                    int yPos = i * mapDim - dimH + mapDim;

                    gameWindow.getGraphics().drawImage(asset.getImage(), xPos, yPos, dimW, dimH, null);
                }
            }
        }
    }

    @Override
    public ComponentType getType() {
        return ComponentType.Map;
    }

    /**
     * In this context, map handles the interaction with any movable object
     *
     * @param component interacts with
     */
    @Override
    public void handleInteractionWith(DinamicComponent component) throws Exception {
        Rectangle rectangle = component.getCollideBox();

        // fist of all we need to check if the player is on a ladder
        if (component.getType() == ComponentType.Player) {
            for (Rectangle ladder : entitiesCoordinates.get("ladders")){
                if(rectangle.intersects(ladder)){
                    component.notify(new Message(MessageType.IsOnLadder , ComponentType.Map));
                    return;
                }
            }
            component.notify(new Message(MessageType.IsNoLongerOnLadder , ComponentType.Map));
        }

        // if not we need to place the component
        // into a specific area of map => clamping
        int xStart = Math.max(0, rectangle.getPosition().getPosX() / mapDim - 1);
        int xEnd = Math.min(width, (rectangle.getPosition().getPosX() + rectangle.getWidth()) / mapDim + 2);
        int yStart = Math.max(0, rectangle.getPosition().getPosY() / mapDim - 1);
        int yEnd = Math.min(height, (rectangle.getPosition().getPosY() + rectangle.getHeight()) / mapDim + 1);

        // variables for special cases collision detection
        wasGroundCollision = false;
        wasTopCollision = false;
        wasLeftCollision = false;
        wasRightCollision = false;

        for (int y = yStart; y < yEnd; y++) {
            for (int x = xStart; x < xEnd; x++) {
                if (!Objects.equals(tilesIndexes[y][x], "0")) {
                    Rectangle tileRect = getRectangle(x, y);
                    // solve the collision and save the offsets

                    rectangle.solveCollision(tileRect);

                    // determine vertical collision type
                    if (rectangle.getDy() < 0) {
                        wasGroundCollision = true;
                    } else if (rectangle.getDy() > 0) {
                        wasTopCollision = true;
                    }

                    // determine left_right wall collision
                    if (rectangle.getDx() < 0) {
                        wasRightCollision = true;
                    } else if (rectangle.getDx() > 0) {
                        wasLeftCollision = true;
                    }
                }
            }
        }

        // notify the component
        if (wasGroundCollision) {
            component.notify(new Message(MessageType.ActivateBottomCollision, ComponentType.Map));
        } else {
            component.notify(new Message(MessageType.DeactivateBottomCollision, ComponentType.Map));
        }
        if (wasTopCollision) {
            component.notify(new Message(MessageType.ActivateTopCollision, ComponentType.Map));
        }

        // particular behavior for some components
        if (component.getType() != ComponentType.Player) {
            // collision verification is necessary to prevent components from falling off the platform
            if (Objects.equals(tilesIndexes[rectangle.getCenterY() / mapDim + 1][rectangle.getMaxX() / mapDim - 1], "0")) {
                wasLeftCollision = true;
            } else if (Objects.equals(tilesIndexes[rectangle.getCenterY() / mapDim + 1][rectangle.getMinX() / mapDim + 1], "0")) {
                wasRightCollision = true;
            }

            // notify the component
            if (wasLeftCollision) {
                component.notify(new Message(MessageType.LeftCollision, ComponentType.Map));
            } else if (wasRightCollision) {
                component.notify(new Message(MessageType.RightCollision, ComponentType.Map));
            }
        }
    }

    /**
     * @param x map relative X coordinate
     * @param y map relative Y coordinate
     * @return rectangle object
     */
    public Rectangle getRectangle(int x, int y) {
        Coordinate<Integer> pos = new Coordinate<>(x * mapDim, y * mapDim);
        return new Rectangle(pos, mapDim, mapDim);
    }

    public Coordinate<Integer> getPlayerPosition() {
        return entitiesCoordinates.get("player").get(0).getPosition();
    }

    public List<Coordinate<Integer>> getEnemiesPositions() {
        List<Coordinate<Integer>> coordinates = new ArrayList<>();
        for (Rectangle rectangle:entitiesCoordinates.get("enemies")) {
            coordinates.add(rectangle.getPosition());
        }
        return coordinates;
    }

    public List<Coordinate<Integer>> getAnimalsPositions() {
        List<Coordinate<Integer>> coordinates = new ArrayList<>();
        for (Rectangle rectangle:entitiesCoordinates.get("animals")) {
            coordinates.add(rectangle.getPosition());
        }
        return coordinates;
    }

    public List<Coordinate<Integer>> getChestsPositions() {
        List<Coordinate<Integer>> coordinates = new ArrayList<>();
        for (Rectangle rectangle:entitiesCoordinates.get("chests")) {
            coordinates.add(rectangle.getPosition());
        }
        return coordinates;
    }

}