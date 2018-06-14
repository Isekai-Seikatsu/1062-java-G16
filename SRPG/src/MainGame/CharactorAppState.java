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
import java.util.logging.Logger;

public class CharactorAppState extends BaseAppState {

    private Node charactorNode;

    private SimpleApplication app;
    private BetterCharacterControl charactorControl;

    private static final Logger LOGGER = Logger.getLogger(CharactorAppState.class.getName());

    @Override
    protected void initialize(Application app) {
        System.out.println("CharactorAppState Initialize...");

        this.app = ((SimpleApplication) app);

        //  charactor init
        charactorNode = new Node("Charator");
        charactorNode.attachChild(setupCharactor());
        charactorNode.setUserData("aspect", true);
        charactorNode.setUserData("TargetDestination", null);
        charactorNode.setUserData("MovementSpeed", 20f);

        charactorControl = new BetterCharacterControl(1.414F, 3.0F, 1.0F);
        charactorNode.addControl(charactorControl);

        charactorControl.setGravity(new Vector3f(0, -1000f, 0));
        charactorControl.warp(new Vector3f(0, 10f, 0));

        //  physics setting
        ((BulletAppState) getStateManager().getState(BulletAppState.class)).getPhysicsSpace().add(charactorControl);
        ((BulletAppState) getStateManager().getState(BulletAppState.class)).getPhysicsSpace().addAll(charactorNode);

        app.getInputManager().setCursorVisible(true);
        System.out.println("CharactorAppState Initialize... DONE");

    }

    @Override
    protected void cleanup(Application app) {
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
        if (charactorNode.getUserData("aspect")) {
            app.getCamera().lookAt(charactorNode.getLocalTranslation(), new Vector3f(0.0F, 1.0F, 0.0F));
        }

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
}
