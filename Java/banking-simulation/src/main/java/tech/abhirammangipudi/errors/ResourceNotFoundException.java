package tech.abhirammangipudi.errors;

public class ResourceNotFoundException extends Exception {
    private final String id;

    public ResourceNotFoundException(String message, String id) {
        super(message);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
