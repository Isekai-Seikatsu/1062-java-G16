package MainGame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.scene.Node;
import com.jme3.system.JmeContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import static java.util.logging.Level.INFO;
import java.util.logging.Logger;

public class ServerMain extends SimpleApplication {

    private Server server;

    //  apps
    private BulletAppState bulletAppState;
    private SceneAppState sceneApp;

    //  to store all of the players' positions
    HashMap<String, Vector3f> posMap;

    private static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());

    public static void main(String[] args) {
        UtNetworking.InitSerializer();
        ServerMain app = new ServerMain();
        app.start(JmeContext.Type.Headless);
    }

    @Override
    public void simpleInitApp() {
        try {
            server = Network.createServer(UtNetworking.PORT);
            server.start();
        } catch (IOException ex) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, "Server open port faill", ex);
        }

        //  apps
        sceneApp = new SceneAppState();
        bulletAppState = new BulletAppState();

        stateManager.attachAll(bulletAppState, sceneApp);

        //  define the actions the server do when the clients connected or disconnected to the server
        server.addConnectionListener(new ConnectionListener() {
            @Override
            public void connectionAdded(Server server, HostedConnection conn) {

                //  store the player's app to their own connections attributes
                CharactorAppState playerApp = new CharactorAppState("" + conn.getId());
                conn.setAttribute("playerApp", playerApp);

                LOGGER.log(INFO, "Client {1} has connected ID: {0}", new Object[]{conn.getId(), conn.getAddress()});
                stateManager.attach(playerApp);
            }

            @Override
            public void connectionRemoved(Server server, HostedConnection conn) {
                ((CharactorAppState) conn.getAttribute("playerApp")).cleanup();
                LOGGER.log(INFO, "Client {1} has disconnected ID: {0}", new Object[]{conn.getId(), conn.getAddress()});
            }

        });

        //  add a thread to deal with the messages the clients send
        server.addMessageListener(new MessageHandler());

        posMap = new HashMap<>();
    }

    @Override
    public void simpleUpdate(float tpf) {

        posMap.clear();
        LOGGER.log(INFO, "Server {1} Connections: {0}", new Object[]{server.getConnections(), server.getConnections().size()});

        for (HostedConnection conn : server.getConnections()) {

            //  get the app from the attributes of the connection
            CharactorAppState playerApp = conn.getAttribute("playerApp");

            //  get the player and the phtsical stuff
            Node charactorNode = playerApp.getCharactorNode();
            BetterCharacterControl charactorControl = playerApp.getCharactorControl();

            //  compute all of the players' position according to players' TargetDestination
            if (charactorNode != null && charactorControl != null) {
                if (charactorNode.getUserData("TargetDestination") != null) {
                    LOGGER.log(Level.INFO, "# playerID: {0}, TargetDestination: {1}", new Object[]{conn.getId(), charactorNode.getUserData("TargetDestination")});
                    Vector3f direction = ((Vector3f) charactorNode.getUserData("TargetDestination")).subtract(charactorNode.getWorldTranslation());
                    direction = new Vector3f(direction.x, 0, direction.z).normalizeLocal();

                    charactorControl.setWalkDirection(direction.mult((float) charactorNode.getUserData("MovementSpeed")));
                } else {
                    charactorControl.setWalkDirection(new Vector3f(0, 0, 0));
                }

                Vector3f pt = charactorNode.getWorldTranslation();

                posMap.put("" + conn.getId(), pt);
                LOGGER.log(Level.INFO, "Server charactorNode client: {1} Pos: {0}", new Object[]{pt, "" + conn.getId()});  //
            }
        }

        //  send all of the player's position to the connected client
        if (posMap.size() > 0) {
            server.broadcast(new UtNetworking.CharactorsInfoMessage(posMap));  //

        }
    }

    @Override
    public void destroy() {
        server.close();
        super.destroy();
    }

    private class MessageHandler implements MessageListener<HostedConnection> {

        @Override
        public void messageReceived(HostedConnection source, Message m) {
            if (m instanceof UtNetworking.UserActionMessage) {
                UtNetworking.UserActionMessage message = (UtNetworking.UserActionMessage) m;
                LOGGER.log(INFO, "Server recieve Client {0}, Message{1}", new Object[]{source.getId(), message});
                
                //  received the clients' click point messages and update the clients' movements 
                ((CharactorAppState) source.getAttribute("playerApp")).getCharactorNode().setUserData("TargetDestination", message.getClickPt());
            }
        }

    }
}
