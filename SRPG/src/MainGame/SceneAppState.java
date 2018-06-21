package MainGame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.util.TangentBinormalGenerator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SceneAppState extends BaseAppState {

    private Node sceneNode, dragon;
    private RigidBodyControl floorControl;
    
    private AnimChannel channel;
    private AnimControl control;

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
        setupDragon(app);

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

    public void setupDragon(Application app) {
        dragon = (Node) app.getAssetManager().loadModel("Models/dragonwalk/Dragon_Mesh.mesh.xml");
        dragon.setLocalScale(4f);
        dragon.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        dragon.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.PI/1.38f,   new Vector3f(0,1,0)));
        
        dragon.setLocalTranslation(-310.15854f, 15f, 298.97787f);
        sceneNode.attachChild(dragon);

        Material draogonMat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        draogonMat.setTexture("DiffuseMap", app.getAssetManager().loadTexture("Textures/textures/Dragon_ground_color.jpg"));
        draogonMat.setTexture("NormalMap", app.getAssetManager().loadTexture("Textures/textures/Dragon_Nor.jpg"));
        draogonMat.setBoolean("UseMaterialColors", true);  // needed for shininess
        draogonMat.setColor("Specular", ColorRGBA.White); // needed for shininess
        draogonMat.setColor("Diffuse", ColorRGBA.White); // needed for shininess
        draogonMat.setFloat("Shininess", 5f); // shininess from 1-128
        TangentBinormalGenerator.generate(dragon);

        dragon.setMaterial(draogonMat);
        control = dragon.getControl(AnimControl.class);
//        control.addListener(this);
        channel = control.createChannel();
        channel.setAnim("my_animation");
    }

}
