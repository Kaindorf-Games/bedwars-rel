package at.kaindorf.games.exceptions.exist;

import at.kaindorf.games.BedwarsRel;
import com.google.common.collect.ImmutableMap;

public class TourneyPlayerExistsException extends TournamentEntityExistsException{
    public TourneyPlayerExistsException(String name) {
        super(BedwarsRel._l("tourney.errors.playerexists", ImmutableMap.of("name", name)));
    }
}
