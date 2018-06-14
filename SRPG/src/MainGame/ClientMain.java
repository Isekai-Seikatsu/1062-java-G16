package MainGame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientMain extends SimpleApplication {

    private Client client;

    private BulletAppState bulletAppState;
    private SceneAppState sceneApp;
    private CharactorAppState playerApp;
    private InteractionAppState inputHandleApp;

    private ConcurrentLinkedQueue<String> messageQueue;
    private ConcurrentLinkedQueue<Vector3f> charactorPosQueue;

    private Vector3f playerPos;
    private static final Logger LOGGER = Logger.getLogger(ClientMain.class.getName());

    public static void main(String[] args) {
        UtNetworking.InitSerializer();
        ClientMain app = new ClientMain();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        try {
            client = Network.connectToServer("localhost", 3000);
            client.start();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "can not connect to server", ex);
        }
        sceneApp = new SceneAppState();
        playerApp = new CharactorAppState();
        bulletAppState = new BulletAppState();
        inputHandleApp = new InteractionAppState();

        stateManager.attachAll(bulletAppState, sceneApp, playerApp, inputHandleApp);

        // flycam settings
        flyCam.setMoveSpeed(100);
        
        this.setPauseOnLostFocus(false);

        messageQueue = new ConcurrentLinkedQueue<>();
        charactorPosQueue = new ConcurrentLinkedQueue<>();

        client.addMessageListener(new TextMessageListener());
        client.addMessageListener(new CharactorInfoMessageListener());

        inputManager.addListener(new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {

                switch (name) {

                    case "Movement":
                        Vector3f pt = playerApp.getCharactorNode().getUserData("TargetDestination");
//                client.send(new UtNetworking.UserActionMessage((Vector3f) playerApp.getCharactorNode().getUserData("TargetDestination")));
                        client.send(new UtNetworking.UserActionMessage(pt));

                        LOGGER.log(Level.INFO, "TargetDestination: {0}", pt);

                        break;
                }
            }

        }, "Movement");
    }

    @Override
    public void simpleUpdate(float tpf) {
        String message = (String) messageQueue.poll();
        playerPos = charactorPosQueue.poll();

        if (message != null) {
            fpsText.setText(message);
        }

        if (playerPos != null) {
            this.enqueue(new Callable() {
                @Override
                public Object call() throws Exception {
                    playerApp.getCharactorControl().warp(playerPos);
                    return null;
                }
            });
        }
    }

    @Override
    public void destroy() {
        client.close();
        super.destroy();
    }

    private class CharactorInfoMessageListener implements MessageListener<Client> {

        @Override
        public void messageReceived(Client source, Message m) {
            if (m instanceof UtNetworking.CharactorInfoMessage) {
                UtNetworking.CharactorInfoMessage message = (UtNetworking.CharactorInfoMessage) m;

                charactorPosQueue.add(message.getPos());
            }
        }

    }

    private class TextMessageListener implements MessageListener<Client> {

        @Override
        public void messageReceived(Client source, Message m) {
            if (m instanceof UtNetworking.TextMessage) {
                UtNetworking.TextMessage message = (UtNetworking.TextMessage) m;

                messageQueue.add(message.getMessage());
            }
        }
    }
}
