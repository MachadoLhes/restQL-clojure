package restql.core.query;

public class QueryInterpolator {

	public static String interpolate(String query, Object... args) {
		final String queryWithPlaceHolders = query.replace("?", "%s");
		final String[] escapedArgs = new String[args.length];

		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof String) {
				escapedArgs[i] = "\"" + args[i] + "\"";
			} else {
				escapedArgs[i] = args[i].toString();
			}
		}


		return String.format(queryWithPlaceHolders, escapedArgs);
	}
}
