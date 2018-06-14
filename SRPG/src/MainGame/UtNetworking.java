package MainGame;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializer;
import java.text.CharacterIterator;

public class UtNetworking {

    public static final int PORT = 3000;


    public static void InitSerializer() {
        Serializer.registerClasses(
                TextMessage.class, 
                UserActionMessage.class,
                CharactorInfoMessage.class
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
    public static class CharactorInfoMessage extends com.jme3.network.AbstractMessage {
        private Vector3f pos;
        
        public CharactorInfoMessage() {
        }
            
        public CharactorInfoMessage(Vector3f pos) {
            this.pos = pos;
        }
        
        public Vector3f getPos() {
            return pos;
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


