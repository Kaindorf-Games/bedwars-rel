package at.kaindorf.games.exceptions.exist;

public class TourneyTeamExistsException extends TournamentEntityExistsException{
    public TourneyTeamExistsException(String name) {
        super("Team " + name + " exists already!");
    }
}
