package pokemons;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Move;

import moves.physical.SeedBomb;

public class Roselia extends Budew {

    public Roselia(String name, int level) {
        super(name, level);

        setStats(50, 60, 45, 100, 80, 65);

        addMove(new SeedBomb());
    }
}
