package MainGame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.scene.Node;
import com.jme3.system.JmeContext;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMain extends SimpleApplication {

    private Server server;
    private BulletAppState bulletAppState;
    private SceneAppState sceneApp;
    private CharactorAppState playerApp;

    private static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());

    public static void main(String[] args) {
        UtNetworking.InitSerializer();
        ServerMain app = new ServerMain();
        app.start(JmeContext.Type.Headless);
    }

    /**
     *
     */
    @Override
    public void simpleInitApp() {
        try {
            server = Network.createServer(3000);
            server.start();
        } catch (IOException ex) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, "Server open port faill", ex);
        }

        sceneApp = new SceneAppState();
        playerApp = new CharactorAppState();
        bulletAppState = new BulletAppState();

        stateManager.attachAll(bulletAppState, sceneApp, playerApp);
        
        server.addMessageListener(new MessageHandler());
    }

    @Override
    public void simpleUpdate(float tpf) {

        Node charactorNode = playerApp.getCharactorNode();
        BetterCharacterControl charactorControl = playerApp.getCharactorControl();

        if (playerApp.getCharactorNode().getUserData("TargetDestination") != null) {
            LOGGER.log(Level.INFO, "TargetDestination: {0}", charactorNode.getUserData("TargetDestination"));
            Vector3f direction = ((Vector3f) charactorNode.getUserData("TargetDestination")).subtract(charactorNode.getWorldTranslation());
            direction = new Vector3f(direction.x, 0, direction.z).normalizeLocal();

            LOGGER.log(Level.INFO, "direction: {0}, isUnit: {1}", new Object[]{direction, direction.isUnitVector()});
            charactorControl.setWalkDirection(direction.mult((float) charactorNode.getUserData("MovementSpeed")));
        } else {
            charactorControl.setWalkDirection(new Vector3f(0, 0, 0));
        }
        
        Vector3f pt = charactorNode.getWorldTranslation();
        server.broadcast(new UtNetworking.CharactorInfoMessage(pt));  //
        Logger.getLogger(ServerMain.class.getName()).log(Level.INFO, "Server charactorNode Pos: " + pt);  //
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
                playerApp.getCharactorNode().setUserData("TargetDestination", message.getClickPt());
            }
        }

    }
}
