
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author brian
 */
public class MapTest {

    private HashMap<String, Integer> map;

    public MapTest() {
        map = new HashMap() {
            {
                put("qwq", 1);
                put("www", 2);
                put("ttt", 3);
            }
        };
    }

    public static void main(String[] args) {
        MapTest mapTest = new MapTest();
        Set<String> mantain = mapTest.map.keySet();
        for (Iterator<String> iter = mantain.iterator(); iter.hasNext();) {
            String key = iter.next();
            if (!mantain.contains(key)) {
                charactorStateMap.get(key).stateDetached(stateManager);
                iter.remove();
                LOGGER.log(Level.SEVERE, "Client {0} removed ... ", key);
            }

        }

    }
}

}
