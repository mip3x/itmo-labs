package pokemons;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Move;

import moves.physical.RockSlide;
import moves.physical.Bulldoze;
import moves.status.Confide;
import moves.status.Agility;

public class Hitmonchan extends Pokemon {

    public Hitmonchan(String name, int level) {
        super(name, level);

        setType(Type.FIGHTING);
        setStats(50, 105, 79, 35, 110, 76);

        final Move[] moves = new Move[] { new RockSlide(), new Bulldoze(), new Confide(), new Agility() };
        setMove(moves);
    }
}
