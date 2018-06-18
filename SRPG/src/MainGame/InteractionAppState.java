/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainGame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author brian
 */
public class InteractionAppState extends BaseAppState implements AnalogListener, ActionListener {

    private CharactorAppState playerApp;
    private Node charactorNode;
    private Node sceneNode;
    private SimpleApplication app;
    private Geometry marker;

    public InteractionAppState(CharactorAppState playerApp) {
        this.playerApp = playerApp;
    }
    
    @Override
    protected void initialize(Application app) {
        System.out.println("InteractionAppState Initialize Start ...");
        this.app = (SimpleApplication) app;

        initMark();

        sceneNode = getStateManager().getState(SceneAppState.class).getSceneNode();
//        charactorNode = getStateManager().getState(CharactorAppState.class).getCharactorNode();
        charactorNode = playerApp.getCharactorNode();

        app.getInputManager().setCursorVisible(true);
        System.out.println("InteractionAppState Initialize Done ...");
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        setupKeys();
    }

    @Override
    protected void onDisable() {
        app.getInputManager().deleteMapping("ToggleAspect");
        app.getInputManager().deleteMapping("Step");
        app.getInputManager().deleteMapping("Movement");

        app.getInputManager().removeListener(this);

    }

    public void setupKeys() {
        app.getInputManager().addMapping("ToggleAspect", new KeyTrigger(KeyInput.KEY_SPACE));
        app.getInputManager().addMapping("Step", new KeyTrigger(KeyInput.KEY_S));
        app.getInputManager().addMapping("Movement", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        app.getInputManager().addListener(this, "ToggleAspect", "Movement", "Step");
    }

    public void initMark() {

        Sphere sphereMesh = new Sphere(10, 10, 2f);
        marker = new Geometry("Mark", sphereMesh);

        Material Mat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        Mat.setBoolean("UseMaterialColors", true);
        Mat.setColor("Ambient", ColorRGBA.Red);
        Mat.setColor("Diffuse", ColorRGBA.Red);
        marker.setMaterial(Mat);
    }

    @Override
    public void onAnalog(String name, float intensity, float tpf) {
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case "ToggleAspect":
                if (isPressed) {
                    charactorNode.setUserData("aspect", !((Boolean) charactorNode.getUserData("aspect")));
                }

                break;
            case "Movement":
                if (isPressed) {
                    CollisionResults results = new CollisionResults();

                    Vector2f click2d = app.getInputManager().getCursorPosition();
                    Vector3f click3d = app.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
                    Vector3f dir = app.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();

                    sceneNode.collideWith(new Ray(click3d, dir), results);

                    if (results.size() > 0) {
                        Vector3f clickPt = results.getClosestCollision().getContactPoint();
                        charactorNode.setUserData("TargetDestination", clickPt);

                        marker.setLocalTranslation(clickPt);
                        app.getRootNode().attachChild(marker);
                    } else {
                        app.getRootNode().detachChild(marker);
                    }
                }
                break;

            case "Step":
                if (isPressed) {
                    charactorNode.setUserData("TargetDestination", null);
                    app.getRootNode().detachChild(marker);

                }
                break;
        }
    }

}
