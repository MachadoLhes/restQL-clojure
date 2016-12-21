package pdg.query;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ideais on 12/12/16.
 */
public class QueryOptions {

    private boolean debugging = false;

    public void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }

    public boolean isDebugging() {
        return this.debugging;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("debugging", debugging);
        return map;
    }

}
