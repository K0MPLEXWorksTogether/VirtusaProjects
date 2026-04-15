package tech.abhirammangipudi.errors;

public class ResourceAlreadyExistsException extends Exception {
    private final String resource;

    public ResourceAlreadyExistsException(String message, String resource) {
        super(message);
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }
}
