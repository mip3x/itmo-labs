package pokemons;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Move;

import moves.status.Confide;
import moves.status.SwordsDance;
import moves.physical.SeedBomb;

public class Roselia extends Pokemon {

    public Roselia(String name, int level) {
        super(name, level);

        setType(Type.GRASS, Type.POISON);
        setStats(50, 60, 45, 100, 80, 65);

        final Move[] moves = new Move[] { new Confide(), new SwordsDance(), new SeedBomb() };
        setMove(moves);
    }
}
