package tech.abhirammangipudi.errors;

public class ConnectionError extends RuntimeException {
    private final String host;

    public ConnectionError(String message, String host) {
        super(message);
        this.host = host;
    }

    public String getHost() {
        return host;
    }
}
