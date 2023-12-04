package pokemons;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Move;

import moves.status.Rest;
import moves.physical.SteelWing;
import moves.special.Glaciate;
import moves.special.IceBeam;

public class Kyurem extends Pokemon {

    public Kyurem(String name, int level) {
        super(name, level);

        setType(Type.DRAGON, Type.ICE);
        setStats(125, 130, 90, 130, 90, 95);

        final Move[] moves = new Move[] { new Rest(), new Glaciate(), new IceBeam(), new SteelWing() };
        setMove(moves);
    }
}
