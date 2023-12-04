package pokemons;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Move;

import moves.status.Confide;
import moves.status.SwordsDance;
import moves.status.DoubleTeam;
import moves.physical.SeedBomb;

public class Roserade extends Pokemon {
    public Roserade(String name, int level) {
        super(name, level);

        setType(Type.GRASS, Type.POISON);
        setStats(60, 70, 65, 125, 105, 90);

        final Move[] moves = new Move[] { new Confide(), new SwordsDance(), new SeedBomb(), new DoubleTeam() };
        setMove(moves);
    }
}
