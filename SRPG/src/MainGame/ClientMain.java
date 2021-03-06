package MainGame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.system.AppSettings;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientMain extends SimpleApplication {

    private Client client;

    private final String serverHost;

    //  some app state
    private BulletAppState bulletAppState;
    private SceneAppState sceneApp;
    private CharactorAppState myPlayerApp;
    private InteractionAppState inputHandleApp;

    //  thread safe container to put message in 
    private ConcurrentLinkedQueue<String> messageQueue;
    private volatile Map<String, Vector3f> charactorPosMapMessage;

    //  the apps of all of the players
    private HashMap<String, CharactorAppState> charactorsAppMap;

    //  add node
    private AudioNode meow;

    private static final Logger LOGGER = Logger.getLogger(ClientMain.class.getName());

    public static void main(String[] args) {

        //  let user input the server that want to connect
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the host of server you want to connect: ");
        String host = scanner.next();
        LOGGER.log(Level.INFO, "The Host Server: {0}", host);

        //  initialize the message class to Serialize
        UtNetworking.InitSerializer();

        //  create client
        ClientMain app = new ClientMain(host);
        app.start();
    }

    public ClientMain(String serverHost) {
        this.serverHost = serverHost;
    }

    @Override
    public void simpleInitApp() {
        LOGGER.log(Level.INFO, "ClientMan simpleInitialize Start ...");

        try {
            client = Network.connectToServer(serverHost, UtNetworking.PORT);
            client.start();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "can not connect to server", ex);
        }

        //  add music
        meow = new AudioNode(assetManager, "Sounds/meow.ogg");
        meow.setPositional(false);
        meow.setLooping(true);

        meow.setUserData("isPlayed", true);
        meow.setUserData("music pitch", 1f);
        meow.play();

        //  create the scene app
        sceneApp = new SceneAppState();

        //  create the player app of its own beforehand
        myPlayerApp = new CharactorAppState("" + client.getId());
        LOGGER.log(Level.INFO, "initialize myPlayerApp: {0}", client.getId());

        //  create the app of physic engine
        bulletAppState = new BulletAppState();

        //  the interactive activities for the input from the user
        inputHandleApp = new InteractionAppState(myPlayerApp);

        //  attach all of the apps
        stateManager.attachAll(bulletAppState, sceneApp, myPlayerApp, inputHandleApp);

        // flycam settings
        flyCam.setMoveSpeed(100);

        //  setup the settings
        AppSettings mySetting = new AppSettings(true);
        mySetting.setFrameRate(60);
        setSettings(mySetting);

        //  keep working when the window did not get the focus
        this.setPauseOnLostFocus(false);

        //  create empty container for initialize
        messageQueue = new ConcurrentLinkedQueue<>();
        charactorsAppMap = new HashMap<>();

        //  put the player itself to the map
        charactorsAppMap.put(myPlayerApp.getName(), myPlayerApp);

        //  add the messageListeners that run on another thread to received the message
        client.addMessageListener(new TextMessageListener());
        client.addMessageListener(new CharactorInfoMessageListener());

        //  define the action the clients do when the client connected and disconnected to the server
        client.addClientStateListener(new ClientStateListener() {
            @Override
            public void clientConnected(Client c) {
                LOGGER.log(Level.INFO, "client {0} connected ... ", c.getId());
            }

            @Override
            public void clientDisconnected(Client c, ClientStateListener.DisconnectInfo info) {
                LOGGER.log(Level.SEVERE, "client {0} disconnect ... {1}", new Object[]{c.getId(), info});
            }

        });

        //  add keys mapping
        inputManager.addMapping("music pause", new KeyTrigger(KeyInput.KEY_M));
        inputManager.addMapping("increase music pitch", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("decrease music pitch", new KeyTrigger(KeyInput.KEY_O));

        //  add the additional action to work with user's inputs
        inputManager.addListener(new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {

                switch (name) {

                    case "Movement":
                        if (isPressed) {
                            Vector3f pt = myPlayerApp.getCharactorNode().getUserData("TargetDestination");
                            client.send(new UtNetworking.UserActionMessage(pt));

                            LOGGER.log(Level.INFO, "client TargetDestination: {0}", pt);
                        }

                        break;

                    case "music pause":
                        if (isPressed) {
                            meow.setUserData("isPlayed", !(boolean) meow.getUserData("isPlayed"));
                            if (meow.getUserData("isPlayed")) {
                                meow.play();
                            } else {
                                meow.pause();
                            }

                        }

                        break;

                    case "increase music pitch":
                        if (isPressed) {
                            meow.setUserData("music pitch", (float) meow.getUserData("music pitch") + 0.02f);
                            if ((float) meow.getUserData("music pitch") > 2.0f) {
                                meow.setUserData("music pitch", 2.0f);
                            }

                            meow.setPitch((float) meow.getUserData("music pitch"));
                        }

                        break;

                    case "decrease music pitch":
                        if (isPressed) {
                            meow.setUserData("music pitch", (float) meow.getUserData("music pitch") - 0.02f);
                            if ((float) meow.getUserData("music pitch") < 0.5f) {
                                meow.setUserData("music pitch", 0.5f);
                            }

                            meow.setPitch((float) meow.getUserData("music pitch"));
                        }

                        break;
                }
            }

        }, "Movement", "music pause", "increase music pitch", "decrease music pitch");

        LOGGER.log(Level.INFO, "ClientMan simpleInitialize Done ...");
    }

    @Override

    public void simpleUpdate(float tpf) {

        //  text message print on gui
        String message = (String) messageQueue.poll();
        if (message != null) {
            fpsText.setText(message);
        }

        //  lock the screen or not
        if (myPlayerApp.getCharactorNode().getUserData("aspect")) {
            cam.lookAt(myPlayerApp.getCharactorNode().getLocalTranslation(), new Vector3f(0.0F, 1.0F, 0.0F));
        }

        //  update all of the charactor's position
        System.out.println(charactorPosMapMessage);
        if (charactorPosMapMessage != null) {

            LOGGER.log(Level.INFO, "charactorPosMapMessage sizes: {0}", charactorPosMapMessage.size());
            LOGGER.log(Level.INFO, "charactorsAppMap sizes: {0}", charactorsAppMap.size());

            for (Map.Entry<String, Vector3f> playerEntry : charactorPosMapMessage.entrySet()) {
                CharactorAppState playerApp = charactorsAppMap.get(playerEntry.getKey());

                LOGGER.log(
                        Level.INFO,
                        "# playerApp: {0}, {1}\n# myPlayerApp: {2}, {3}",
                        new Object[]{
                            playerApp,
                            playerEntry.getKey(),
                            myPlayerApp,
                            myPlayerApp.getName()
                        }
                );

                if (playerApp == null) {
                    playerApp = new CharactorAppState(playerEntry.getKey());
                    stateManager.attach(playerApp);
                    charactorsAppMap.put(playerEntry.getKey(), playerApp);
                }

                if (playerApp.isInited()) {
                    playerApp.getCharactorControl().warp(playerEntry.getValue());  // drop ???
                }

            }

            //  remove the players that are not online
            Set<String> mantain = charactorPosMapMessage.keySet();
            for (Iterator<String> iter = charactorsAppMap.keySet().iterator(); iter.hasNext();) {
                String key = iter.next();
                if (!mantain.contains(key)) {
                    charactorsAppMap.get(key).cleanup();
                    iter.remove();
                    LOGGER.log(Level.SEVERE, "Client {0} removed ... ", key);
                }

            }

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
            if (m instanceof UtNetworking.CharactorsInfoMessage) {
                UtNetworking.CharactorsInfoMessage message = (UtNetworking.CharactorsInfoMessage) m;
                LOGGER.log(Level.INFO, "Got One CharactorsInfoMessage ... {0}", message.getPosMap());

                charactorPosMapMessage = message.getPosMap();
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
