package pokemons;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Move;

import moves.status.DoubleTeam;

public class Roserade extends Roselia {
    public Roserade(String name, int level) {
        super(name, level);

        setStats(60, 70, 65, 125, 105, 90);

        addMove(new DoubleTeam());
    }
}
