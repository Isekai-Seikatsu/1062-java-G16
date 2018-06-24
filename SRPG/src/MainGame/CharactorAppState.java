package MainGame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.TangentBinormalGenerator;
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

        //  create the node that be attached by muscle (charactor)
        charactorNode = new Node(name);
        charactorNode.attachChild(setupCharactor(app));

        //  initialize the datas of the muscle
        charactorNode.setUserData("aspect", true);
        charactorNode.setUserData("TargetDestination", null);
        charactorNode.setUserData("MovementSpeed", 20f);

        //  create the physical body shape and control then be added by charactor
        charactorControl = new BetterCharacterControl(20F, 23F, 50F);
        charactorNode.addControl(charactorControl);

        //  init the gravity of muscle
        charactorControl.setGravity(new Vector3f(0, -100f, 0));

        //  init the position of muscle
        charactorControl.warp(new Vector3f(0, 200f, 0));

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

    public Spatial setupCharactor(Application app) {
        Spatial muscle = app.getAssetManager().loadModel("Models/muscle/no_title.002.mesh.j3o");

        Material muscleMat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        muscleMat.setTexture("DiffuseMap", app.getAssetManager().loadTexture("Textures/Muscled_mutant_Diffuse_specular.tga"));
        muscleMat.setTexture("NormalMap", app.getAssetManager().loadTexture("Textures/Muscled_mutant_normals.tga"));

        muscleMat.setBoolean("UseMaterialColors", true);  // needed for shininess
        muscleMat.setColor("Specular", ColorRGBA.White); // needed for shininess
        muscleMat.setColor("Diffuse", ColorRGBA.White); // needed for shininess
        muscleMat.setFloat("Shininess", 5f); // shininess from 1-128

        TangentBinormalGenerator.generate(muscle);

        muscle.setMaterial(muscleMat);
        muscle.setLocalScale(0.5f);

        muscle.move(new Vector3f(0.0F, 1.0F, 0.0F));
        return muscle;
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
