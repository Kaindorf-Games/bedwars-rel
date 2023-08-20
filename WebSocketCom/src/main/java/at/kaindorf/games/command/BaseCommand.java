package at.kaindorf.games.command;

import at.kaindorf.games.authentication.AuthenticationHandler;
import at.kaindorf.games.authentication.DefaultAuthenticationHandler;

import java.util.Map;

public abstract class BaseCommand {

    public abstract boolean execute(String command, Map<String, Object> params);

    public AuthenticationHandler authenticationHandler() {
        return new DefaultAuthenticationHandler();
    }
}
