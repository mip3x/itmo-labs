package pokemons;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Move;

import moves.physical.RockSlide;
import moves.status.Confide;
import moves.physical.Bulldoze;

public class Tyrogue extends Pokemon {

    public Tyrogue(String name, int level) {
        super(name, level);

        setType(Type.FIGHTING);
        setStats(35, 35, 35, 35, 35, 35);

        final Move[] moves = new Move[] { new RockSlide(), new Confide(), new Bulldoze() };
        setMove(moves);
    }
}
