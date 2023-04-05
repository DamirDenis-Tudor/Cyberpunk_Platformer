package Components.StaticComponents;
import Components.DinamicComponents.Items.Bullet;
import Components.DinamicComponents.Items.Gun;
import Components.DinamicComponents.Map.GameMap;
import Enums.AnimationType;
import Enums.MapType;
import Enums.Weapon;
import Utils.Coordinate;
import Utils.Rectangle;
import Window.Camera;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;

import static Enums.MapType.*;
import static Utils.Constants.mapDim;
import static Utils.Constants.mapScale;

import java.util.Map;

/**
 * Here will load predifined maps , animations , and so on
 */
public class AssetsDeposit {
    private static AssetsDeposit instance = null;
    private final Map<MapType, GameMap> gameMaps;
    private final Map <AnimationType, Animation> animations;
    private final Map<Weapon , Gun> guns;
    private final Map<Weapon , Bullet> bullets;

    private final Map<Weapon , Weapon> gunsBulletsRelation;
    private AssetsDeposit() throws Exception {

        // -----------------------load game maps
        gameMaps = new HashMap<>();
        gameMaps.put(GreenCityMap , new GameMap("src/Resources/maps/green_map.tmx"));
        gameMaps.put(IndustrialCity , new GameMap("src/Resources/maps/industrial_map.tmx"));

        // -----------------------load game animations
        animations = new HashMap<>();

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        Document document = builder.parse(new File("src/Resources/in_game_assets/animations.tsx"));

        Element root = document.getDocumentElement();

        NodeList elements = root.getElementsByTagName("tile");

        for (int index = 0; index < elements.getLength(); ++index) {
            Element element = (Element) elements.item(index);
            Element property = (Element) element.getElementsByTagName("property").item(0);
            Element imageElement = (Element) element.getElementsByTagName("image").item(0);

            String source = imageElement.getAttribute("source").replace("..", "src/Resources");
            String id = element.getAttribute("class");
            int spriteSheetWidth = Integer.parseInt(imageElement.getAttribute("width"));
            int height = Integer.parseInt(imageElement.getAttribute("height"));
            int width = Integer.parseInt(property.getAttribute("value"));

            Element box = (Element) element.getElementsByTagName("objectgroup").item(0).getFirstChild().getNextSibling();
            int x = (int)Float.parseFloat(box.getAttributeNode("x").getValue());
            int y = (int)Float.parseFloat(box.getAttributeNode("y").getValue());
            int boxHeight = (int) (Float.parseFloat(box.getAttributeNode("height").getValue())*mapScale);
            int boxWidth = (int)(Float.parseFloat(box.getAttributeNode("width").getValue())*mapScale);
            Rectangle boxBounding = new Rectangle(new Coordinate<>(x,y) , boxWidth, boxHeight);
            animations.put(AnimationType.valueOf(id), new Animation(source , spriteSheetWidth , width ,height , boxBounding, AnimationType.valueOf(id) ));
        }

        // -----------------------load game weapons
        // first of all let's make a mapping that describes
        // the bullet-gun relationship
        gunsBulletsRelation = new HashMap<>();
        gunsBulletsRelation.put(Weapon.Gun1 , Weapon.Bullet1);
        gunsBulletsRelation.put(Weapon.Gun2 , Weapon.Bullet2);
        gunsBulletsRelation.put(Weapon.Gun3 , Weapon.Bullet3);
        gunsBulletsRelation.put(Weapon.Gun4 , Weapon.Bullet4);
        gunsBulletsRelation.put(Weapon.Gun5 , Weapon.Bullet5);
        gunsBulletsRelation.put(Weapon.Gun6 , Weapon.Bullet6);
        gunsBulletsRelation.put(Weapon.Gun7 , Weapon.Bullet7);
        gunsBulletsRelation.put(Weapon.Gun8 , Weapon.Bullet8);
        gunsBulletsRelation.put(Weapon.Gun9 , Weapon.Bullet9);
        gunsBulletsRelation.put(Weapon.Gun10 , Weapon.Bullet10);


        guns = new HashMap<>();
        bullets = new HashMap<>();

        document = builder.parse(new File("src/Resources/in_game_assets/Weapons&Buletts.tsx"));

        root = document.getDocumentElement();

        elements = root.getElementsByTagName("tile");

        for (int index = 0; index < elements.getLength(); ++index) {
            Element element = (Element) elements.item(index);
            Element imageElement = (Element) element.getElementsByTagName("image").item(0);

            String source = imageElement.getAttribute("source").replace("..", "src/Resources");
            String id = element.getAttribute("class");
            int width = Integer.parseInt(imageElement.getAttribute("width"));
            int height = Integer.parseInt(imageElement.getAttribute("height"));

            Rectangle boxBounding = new Rectangle(new Coordinate<>(0,0) , width, height);

            if(id.contains("Gun")){
                guns.put(Weapon.valueOf(id),new Gun(ImageIO.read(new File(source)) , boxBounding));
            }else {
                bullets.put(Weapon.valueOf(id),new Bullet(ImageIO.read(new File(source)) , boxBounding));
            }
        }
    }

    /**
     * @return
     */
    public static AssetsDeposit getInstance() throws Exception {
        if (instance == null) {
            instance = new AssetsDeposit();
        }
        return instance;
    }
    public GameMap getGameMap(MapType name){
        Camera.getInstance().setGameMapPixelDimension(gameMaps.get(name).getWidth()*mapDim);
        return gameMaps.get(name);
    }

    public Animation getAnimation(AnimationType name){
        return animations.get(name);
    }

    //public Animation getGun
}
