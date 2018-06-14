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

public class SceneAppState extends BaseAppState {

    private Node sceneNode;
    private RigidBodyControl floorControl;

    @Override
    protected void initialize(Application app) {
        System.out.println("SceneAppState Initialize...");
        app = (SimpleApplication) app;
        sceneNode = (Node) app.getAssetManager().loadModel("Scenes/newScene.j3o");

        sceneNode.move(new Vector3f(0, -10f, 0));

        CollisionShape floorColliShape = CollisionShapeFactory.createMeshShape(sceneNode);

        floorControl = new RigidBodyControl(floorColliShape, 0.0F);

        sceneNode.addControl(floorControl);

        floorControl.setPhysicsLocation(new Vector3f(0.0F, -10.0F, 0.0F));
        System.out.println("bas..." + getStateManager().getState(BulletAppState.class));
        getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(floorControl);
        getStateManager().getState(BulletAppState.class).getPhysicsSpace().addAll(sceneNode);

        app.getViewPort().setBackgroundColor(new ColorRGBA(66f / 255f, 134f / 255f, 244f / 255f, 0.5f));
        setupLights();
        System.out.println("SceneAppState Initialize... DONE");
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
