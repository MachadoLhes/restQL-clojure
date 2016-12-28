package pdg.exception;

/**
 * Created by ideais on 15/12/16.
 */
public class ResponseParseException extends PDGException {

    public ResponseParseException(String message) { super(message); }

    public ResponseParseException(Throwable t) {
        super(t);
    }
}
