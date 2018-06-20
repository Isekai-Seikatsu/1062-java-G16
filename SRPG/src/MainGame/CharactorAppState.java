package MainGame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CharactorAppState extends BaseAppState {

    private Node charactorNode;
    private final String name;
    private boolean inited;

    private SimpleApplication app;
    private BetterCharacterControl charactorControl;

    private static final Logger LOGGER = Logger.getLogger(CharactorAppState.class.getName());

    public CharactorAppState(String name) {
        this.name = name;
    }

    @Override
    protected void initialize(Application app) {
        LOGGER.log(Level.INFO, "CharactorAppState Initialize...");

        this.app = ((SimpleApplication) app);

        //  create the node that be attached by player (charactor)
        charactorNode = new Node(name);
        charactorNode.attachChild(setupCharactor());

        //  initialize the datas of the player
        charactorNode.setUserData("aspect", true);
        charactorNode.setUserData("TargetDestination", null);
        charactorNode.setUserData("MovementSpeed", 20f);

        //  create the physical body shape and control then be added by charactor
        charactorControl = new BetterCharacterControl(1.414F, 2.1F, 1.0F);
        charactorNode.addControl(charactorControl);
        
        //  init the gravity of player
        charactorControl.setGravity(new Vector3f(0, -100f, 0));

        //  init the position of player
        charactorControl.warp(new Vector3f(0, 10f, 0));

        //  physical setting
        ((BulletAppState) getStateManager().getState(BulletAppState.class)).getPhysicsSpace().add(charactorControl);
        ((BulletAppState) getStateManager().getState(BulletAppState.class)).getPhysicsSpace().addAll(charactorNode);

        LOGGER.log(Level.INFO, "CharactorAppState Initialize Done...");
        
        //  initialized completely
        inited = true;
    }

    @Override
    protected void cleanup(Application app) {

        ((SimpleApplication) app).getRootNode().detachChild(charactorNode);
        charactorNode.removeFromParent();
        ((BulletAppState) getStateManager().getState(BulletAppState.class)).getPhysicsSpace().remove(charactorControl);
        ((BulletAppState) getStateManager().getState(BulletAppState.class)).getPhysicsSpace().removeAll(charactorNode);
    }

    @Override
    protected void onEnable() {
        app.getRootNode().attachChild(charactorNode);
    }

    @Override
    protected void onDisable() {
        app.getRootNode().detachChild(charactorNode);
    }

    @Override
    public void update(float tpf) {

    }

    public Spatial setupCharactor() {

        Box boxMesh = new Box(1.0F, 1.0F, 1.0F);
        Geometry boxGeo = new Geometry("Colored Box", boxMesh);
        Material boxMat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        boxMat.setBoolean("UseMaterialColors", true);
        boxMat.setColor("Ambient", ColorRGBA.Green);
        boxMat.setColor("Diffuse", ColorRGBA.Green);
        boxGeo.setMaterial(boxMat);

        Spatial charator = boxGeo;
        charator.move(new Vector3f(0.0F, 1.0F, 0.0F));
        return charator;
    }

    public Node getCharactorNode() {
        return charactorNode;
    }

    public BetterCharacterControl getCharactorControl() {
        return charactorControl;
    }

    public String getName() {
        return name;
    }

    public boolean isInited() {
        return inited;
    }
}
