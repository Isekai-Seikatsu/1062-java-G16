package MainGame;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializer;
import java.util.Map;

public class UtNetworking {

    public static final int PORT = 3000;


    public static void InitSerializer() {
        Serializer.registerClasses(
                TextMessage.class, 
                UserActionMessage.class,
                CharactorsInfoMessage.class
        );
    }

    @com.jme3.network.serializing.Serializable
    public static class UserActionMessage extends com.jme3.network.AbstractMessage {
        private Vector3f clickPt;
        
        public UserActionMessage() {    
        }
        
        public UserActionMessage(Vector3f clickPt) {
            this.clickPt = clickPt;
        }
        
        public Vector3f getClickPt() {
            return clickPt;
        }
    }
    
    @com.jme3.network.serializing.Serializable
    public static class CharactorsInfoMessage extends com.jme3.network.AbstractMessage {
        private Map<String, Vector3f> positionsMap;
        
        public CharactorsInfoMessage() {
        }
            
        public CharactorsInfoMessage(Map<String, Vector3f> positionsMap) {
            this.positionsMap = positionsMap;
        }
        
        public Map<String, Vector3f> getPosMap() {
            return positionsMap;
        }
    }

    @com.jme3.network.serializing.Serializable
    public static class TextMessage extends com.jme3.network.AbstractMessage {

        private String message;

        public TextMessage() {
        }

        public TextMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}


