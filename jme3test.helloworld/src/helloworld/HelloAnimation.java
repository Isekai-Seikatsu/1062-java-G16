/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helloworld;

/**
 *
 * @author brian
 */
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sample 7 - how to load an OgreXML model and play an animation, using
 * channels, a controller, and an AnimEventListener.
 */
public class HelloAnimation extends SimpleApplication implements AnimEventListener {

    private static final Logger LOGGER = Logger.getLogger(HelloAnimation.class.getName());

    private AnimChannel channel;
    private AnimControl control;
    Node player;

    public static void main(String[] args) {
        HelloAnimation app = new HelloAnimation();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(ColorRGBA.LightGray);
        initKeys();
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f, -1f, -1).normalizeLocal());
        rootNode.addLight(dl);
        player = (Node) assetManager.loadModel("Models/testskill/muscle.mesh.xml");
        LOGGER.log(Level.INFO, "player: {0}", player);
        player.setLocalScale(0.5f);
        rootNode.attachChild(player);

        control = player.getControl(AnimControl.class);
        LOGGER.log(Level.INFO, "control: {0}", control);
//        control.addListener(this);
//        channel = control.createChannel();
//        channel.setAnim("stand");
//        for (String anim : control.getAnimationNames()) {
//            System.out.println(anim);
//        }
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
//        if (animName.equals("Walk")) {
//            channel.setAnim("stand", 0.50f);
//            channel.setLoopMode(LoopMode.DontLoop);
//            channel.setSpeed(1f);
//        }
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        // unused
    }

    /**
     * Custom Keybinding: Map named actions to inputs.
     */
    private void initKeys() {
        inputManager.addMapping("Walk", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "Walk");
    }
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Walk") && !keyPressed) {
                if (!channel.getAnimationName().equals("Walk")) {
                    channel.setAnim("Walk", 0.50f);
                    channel.setLoopMode(LoopMode.Loop);
                }
            }
        }
    };
}
