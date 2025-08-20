package at.kaindorf.games.exceptions.exist;

import at.kaindorf.games.BedwarsRel;
import com.google.common.collect.ImmutableMap;

public class TourneyTeamExistsException extends TournamentEntityExistsException{
    public TourneyTeamExistsException(String name) {
        super(BedwarsRel._l("tourney.errors.teamexists", ImmutableMap.of("name", name)));
    }
}
