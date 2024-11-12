package pokemons;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Move;

import moves.status.Confide;
import moves.status.SwordsDance;

public class Budew extends Pokemon {

    public Budew(String name, int level) {
        super(name, level);

        setType(Type.GRASS, Type.POISON);
        setStats(40, 30, 35, 50, 70, 55);

        final Move[] moves = new Move[] { new Confide(), new SwordsDance() };
        setMove(moves);
    }
}
