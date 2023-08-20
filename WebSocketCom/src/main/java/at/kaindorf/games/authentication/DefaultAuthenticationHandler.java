package at.kaindorf.games.authentication;

public class DefaultAuthenticationHandler implements AuthenticationHandler {
    @Override
    public boolean authenticate(String identifier) {
        return false;
    }
}
