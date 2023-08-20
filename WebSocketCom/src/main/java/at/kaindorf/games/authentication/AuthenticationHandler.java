package at.kaindorf.games.authentication;

public interface AuthenticationHandler {
    boolean authenticate(String identifier);
}
