package at.kaindorf.games.exceptions.exist;

public class TourneyPlayerExistsException extends TournamentEntityExistsException{
    public TourneyPlayerExistsException(String name) {
        super("Player " + name + " is already in a team!");
    }
}
