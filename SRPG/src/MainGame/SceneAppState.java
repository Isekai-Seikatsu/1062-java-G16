package MainGame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SceneAppState extends BaseAppState {

    private Node sceneNode;
    private RigidBodyControl floorControl;

    private static final Logger LOGGER = Logger.getLogger(SceneAppState.class.getName());

    @Override
    protected void initialize(Application app) {
        LOGGER.log(Level.INFO, "SceneAppState Initialize...");
        
        // cast to SimpleApplication to gain the app of main thread
        app = (SimpleApplication) app;
        
        //  load the model of scene 
        sceneNode = (Node) app.getAssetManager().loadModel("Scenes/newScene.j3o");

        // create physic shape and control
        CollisionShape floorColliShape = CollisionShapeFactory.createMeshShape(sceneNode);
        floorControl = new RigidBodyControl(floorColliShape, 0.0F);

        // add  control to scene that means floor
        sceneNode.addControl(floorControl);
        
        //  init the position of the floor's physics shape
        floorControl.setPhysicsLocation(new Vector3f(0.0F, -10.0F, 0.0F));
        
        //  attach app state
        getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(floorControl);
        getStateManager().getState(BulletAppState.class).getPhysicsSpace().addAll(sceneNode);

        //  add background color
        app.getViewPort().setBackgroundColor(new ColorRGBA(66f / 255f, 134f / 255f, 244f / 255f, 0.5f));
        
        //  lights
        setupLights();
        
        LOGGER.log(Level.INFO, "SceneAppState Initialize Done...");
    }

    @Override
    protected void cleanup(Application app) {
        sceneNode.removeFromParent();
        getStateManager().getState(BulletAppState.class).getPhysicsSpace().remove(sceneNode);
    }

    @Override
    protected void onEnable() {
        ((SimpleApplication) getApplication()).getRootNode().attachChild(sceneNode);
    }

    @Override
    protected void onDisable() {
        ((SimpleApplication) getApplication()).getRootNode().detachChild(sceneNode);
    }

    @Override
    public void update(float tpf) {
    }

    public Node getSceneNode() {
        return sceneNode;
    }

    public void setupLights() {
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5F, -0.5F, -0.5F).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        ((SimpleApplication) getApplication()).getRootNode().addLight(sun);
    }
}
