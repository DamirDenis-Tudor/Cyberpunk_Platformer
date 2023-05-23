package Components.BaseComponents;

import Components.GameComponents.GameItems.Bullet;
import Components.GameComponents.GameItems.Gun;
import Components.GameComponents.Map.GameMap;
import Database.Database;
import Enums.AnimationType;
import Enums.ComponentType;
import Utils.Coordinate;
import Utils.Rectangle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static Utils.Constants.MAP_SCALE;

/**
 * This class has predefined maps, animations and so on.
 */
public class AssetsDeposit {
    private static AssetsDeposit instance = null;
    private final Map<ComponentType, GameMap> gameMaps;
    private final Map <AnimationType, Animation> animations;
    private final Map<ComponentType , Gun> guns;
    private final Map<ComponentType , Bullet> bullets;
    private final Map<ComponentType , ComponentType> gunsBulletsRelation;
    private ImageWrapper menuWallpaper = null;

    private ImageWrapper greenMapPreview ;
    private ImageWrapper industrialMapPreview ;
    private ImageWrapper powerMapPreview ;
    private ImageWrapper gameOverlay = null;
    private List<ImageWrapper> overlayEffects;
    /**
     * this constructor loads all the assets.
     */
    private AssetsDeposit() {
        gameMaps = new HashMap<>();
        animations = new HashMap<>();
        gunsBulletsRelation = new HashMap<>();
        guns = new HashMap<>();
        bullets = new HashMap<>();
        try {
            // -------------------------load menu wallpaper
            String source1 = "Resources/wallpapers/menu_wallpaper1.png";
            source1 = Objects.requireNonNull(Database.class.getClassLoader().getResource(source1)).getPath();
            menuWallpaper = new ImageWrapper(ImageIO.read(new File(source1)));

            //--------------------------load map previews
            source1 = "Resources/wallpapers/Green-Zone-Tileset-Pixel-Art.png";
            source1 = Objects.requireNonNull(Database.class.getClassLoader().getResource(source1)).getPath();
            greenMapPreview = new ImageWrapper(ImageIO.read(new File(source1)));

            source1 = "Resources/wallpapers/Free-Industrial-Zone-Tileset-Pixel-Art.png";
            source1 = Objects.requireNonNull(Database.class.getClassLoader().getResource(source1)).getPath();
            industrialMapPreview = new ImageWrapper(ImageIO.read(new File(source1)));

            source1 = "Resources/wallpapers/Power-Station-Free-Tileset-Pixel-Art-768x512.jpg";
            source1 = Objects.requireNonNull(Database.class.getClassLoader().getResource(source1)).getPath();
            powerMapPreview = new ImageWrapper(ImageIO.read(new File(source1)));

            // -------------------------load the overlay effects and set the transparency
            //TODO
            source1 = "Resources/resources/cyber-effects/Overlay/2.png";
            source1 = Objects.requireNonNull(Database.class.getClassLoader().getResource(source1)).getPath();
            BufferedImage image = ImageIO.read(new File(source1));
            BufferedImage transparentImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = transparentImage.createGraphics();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f)); // 20% transparency
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();
            gameOverlay = new ImageWrapper(transparentImage);


            // -----------------------load game maps
            gameMaps.put(ComponentType.GREEN_CITY, new GameMap(null,ComponentType.GREEN_CITY));
            gameMaps.put(ComponentType.INDUSTRIAL_CITY, new GameMap(null,ComponentType.INDUSTRIAL_CITY));
            gameMaps.put(ComponentType.POWER_STATION, new GameMap(null,ComponentType.POWER_STATION));

            // -----------------------load game animations
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            source1 = "Resources/in_game_assets/animations.tsx";
            source1 = Objects.requireNonNull(Database.class.getClassLoader().getResource(source1)).getPath();
            Document document = builder.parse(new File(source1));

            Element root = document.getDocumentElement();

            NodeList elements = root.getElementsByTagName("tile");

            for (int index = 0; index < elements.getLength(); ++index) {
                Element element = (Element) elements.item(index);
                Element property = (Element) element.getElementsByTagName("property").item(0);
                Element imageElement = (Element) element.getElementsByTagName("image").item(0);

                String source = imageElement.getAttribute("source").replace("..", "Resources");
                source = Objects.requireNonNull(Database.class.getClassLoader().getResource(source)).getPath().replace("%20" , " ");
                String id = element.getAttribute("type");
                int spriteSheetWidth = Integer.parseInt(imageElement.getAttribute("width"));
                int height = Integer.parseInt(imageElement.getAttribute("height"));
                int width = Integer.parseInt(property.getAttribute("value"));

                Element box = (Element) element.getElementsByTagName("objectgroup").item(0).getFirstChild().getNextSibling();
                int x = (int) Float.parseFloat(box.getAttributeNode("x").getValue());
                int y = (int) Float.parseFloat(box.getAttributeNode("y").getValue());
                int boxHeight = (int) (Float.parseFloat(box.getAttributeNode("height").getValue()) * MAP_SCALE);
                int boxWidth = (int) (Float.parseFloat(box.getAttributeNode("width").getValue()) * MAP_SCALE);
                Rectangle boxBounding = new Rectangle(new Coordinate<>(x, y), boxWidth, boxHeight);
                animations.put(AnimationType.valueOf(id), new Animation(source, spriteSheetWidth, width, height, boxBounding, AnimationType.valueOf(id)));
            }

            // -----------------------load game ComponentTypes
            // first of all let's make a mapping that describes
            // the bullet-gun relationship

            gunsBulletsRelation.put(ComponentType.GUN_1, ComponentType.BULLET_1);
            gunsBulletsRelation.put(ComponentType.GUN_2, ComponentType.BULLET_2);
            gunsBulletsRelation.put(ComponentType.GUN_3, ComponentType.BULLET_3);
            gunsBulletsRelation.put(ComponentType.GUN_4, ComponentType.BULLET_4);
            gunsBulletsRelation.put(ComponentType.GUN_5, ComponentType.BULLET_5);
            gunsBulletsRelation.put(ComponentType.GUN_6, ComponentType.BULLET_6);
            gunsBulletsRelation.put(ComponentType.GUN_7, ComponentType.BULLET_7);
            gunsBulletsRelation.put(ComponentType.GUN_8, ComponentType.BULLET_8);
            gunsBulletsRelation.put(ComponentType.GUN_9, ComponentType.BULLET_9);
            gunsBulletsRelation.put(ComponentType.GUN_10, ComponentType.BULLET_10);


            source1 = "Resources/in_game_assets/weapons&buletts.tsx";
            source1 = Objects.requireNonNull(Database.class.getClassLoader().getResource(source1)).getPath().replace("%20" , " ");

            document = builder.parse(new File(source1));
            root = document.getDocumentElement();
            elements = root.getElementsByTagName("tile");

            for (int index = 0; index < elements.getLength(); ++index) {
                Element element = (Element) elements.item(index);
                Element imageElement = (Element) element.getElementsByTagName("image").item(0);

                String source = imageElement.getAttribute("source").replace("..", "Resources");
                source = Objects.requireNonNull(Database.class.getClassLoader().getResource(source)).getPath().replace("%20" , " ");
                String id = element.getAttribute("type");
                int width = Integer.parseInt(imageElement.getAttribute("width"));
                int height = Integer.parseInt(imageElement.getAttribute("height"));

                if (id.contains("GUN")) {
                    Rectangle boxBounding = new Rectangle(new Coordinate<>(0, 0), (int) (width), (int) (height));
                    guns.put(ComponentType.valueOf(id), new Gun(ImageIO.read(new File(source)), boxBounding));
                } else {
                    Rectangle boxBounding = new Rectangle(new Coordinate<>(0, 0), (int) (width * 2), (int) (height * 2));
                    bullets.put(ComponentType.valueOf(id), new Bullet(ImageIO.read(new File(source)), boxBounding));
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage() + "->" + e.getLocalizedMessage());
        }
    }

    public static AssetsDeposit get(){
        if (instance == null) {
            instance = new AssetsDeposit();
        }
        return instance;
    }

    public void addGameMap(GameMap map){
        if(!gameMaps.containsKey(map.getCurrentType())){
           gameMaps.put(map.getCurrentType() , map);
        }
    }
    public GameMap getGameMap(ComponentType name){
        return gameMaps.get(name);
    }

    public Animation getAnimation(AnimationType name){
        return animations.get(name);
    }

    /**
     * @param name bun type
     * @return Gun object
     */
    public Gun getGun(ComponentType name){
        return guns.get(name);
    }

    /**
     * @param name bullet type
     * @return Bullet object
     */
    public Bullet getBulletByGunName(ComponentType name){return bullets.get(gunsBulletsRelation.get(name));}

    public ImageWrapper getMenuWallpaper() {return menuWallpaper;}

    public ImageWrapper getGameOverlay() {return gameOverlay;}

    public ImageWrapper getGreenMapPreview() {
        return greenMapPreview;
    }
    public ImageWrapper getIndustrialMapPreview() {return industrialMapPreview;}
    public ImageWrapper getPowerMapPreview() {return powerMapPreview;}
}
