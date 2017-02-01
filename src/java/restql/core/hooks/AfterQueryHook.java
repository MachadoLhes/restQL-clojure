package restql.core.hooks;

import restql.core.response.QueryResponse;

/**
 * Created by iago.osilva on 31/01/17.
 */
public abstract class AfterQueryHook extends QueryHook {

    public QueryResponse getResult() {
        return new QueryResponse((String) this.getData().get("result"));
    }
}
