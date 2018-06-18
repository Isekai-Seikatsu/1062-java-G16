package MainGame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;


public class Test extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private SceneAppState sceneApp;
    private CharactorAppState playerApp, player2App;
    private InteractionAppState inputHandleApp;

    public static void main(String[] args) {
        Test app = new Test();
        app.start();
    }

//    public Test() {
//
//        super(new SceneAppState(), new CharactorAppState());
//    }sssss
    @Override
    public void simpleInitApp() {
        System.out.println("Test simpleInitialize...");
        sceneApp = new SceneAppState();
        playerApp = new CharactorAppState("player1");
        player2App = new CharactorAppState("player2");
        bulletAppState = new BulletAppState();
        inputHandleApp = new InteractionAppState(playerApp);

        stateManager.attachAll(bulletAppState, sceneApp, playerApp, player2App, inputHandleApp);

//        stateManager.attach(sceneApp);
        flyCam.setMoveSpeed(100);
//        flyCam.setEnabled(false);

//        inputManager.setCursorVisible(true);
        System.out.println("Test simpleInitialize Done...");

    }

    @Override
    public void simpleUpdate(float tpf) {
        player2App.getCharactorControl().setWalkDirection(new Vector3f(10, 0, 0));
        playerApp.getCharactorControl().setWalkDirection(new Vector3f(-10, 0, 0));

    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {

    }
}
