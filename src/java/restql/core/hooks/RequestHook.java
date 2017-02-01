package restql.core.hooks;

import restql.core.interop.Hook;

import java.util.Collections;
import java.util.Map;

/**
 * Created by iago.osilva on 31/01/17.
 */
public abstract class RequestHook extends Hook {
    
    public String getUrl() {
        return (String) this.getData().get("url");
    }

    public Long getTimeout() {
        return ((Number) this.getData().get("timeout")).longValue();
    }

    public Long getTime() {
        Object time = this.getData().get("time");
        return (time != null ? ((Number) time).longValue() : null);
    }

    public Map<String, String> getHeaders() {
        Object headers = this.getData().get("headers");
        return (headers != null ? Collections.synchronizedMap((Map<String, String>) headers) : null);
    }
}
