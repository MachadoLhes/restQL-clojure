package restql.core.hooks;

import restql.core.interop.Hook;
import restql.core.response.QueryResponse;

/**
 * Created by iago.osilva on 30/01/17.
 */
public abstract class AfterRequestHook extends Hook {

    public QueryResponse getResponseBody() {
        Object response = this.getData().get("body");

        return new QueryResponse((String) response.toString());
    }

    public Integer getResponseStatusCode() {
        return (Integer) this.getData().get("status");
    }

    public Boolean isError() {
        return this.getData().containsKey("errordetail");
    }

}
