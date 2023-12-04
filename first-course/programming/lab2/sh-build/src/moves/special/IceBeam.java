package moves.special;

import ru.ifmo.se.pokemon.SpecialMove;
import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Effect;

public class IceBeam extends SpecialMove {
    public IceBeam() {
        super(Type.ICE, 90, 100);
    }

    @Override
    protected String describe() {
        return super.describe() + " with " + getClass().getSimpleName();
    }

    @Override
    protected void applyOppEffects(Pokemon pokemon) {
        // Ice Beam deals damage and has a 10% chance of freezing the target.
        super.applyOppEffects(pokemon);

        if (0.1 > Math.random())
            Effect.freeze(pokemon);
        return;
    }

    @Override
    protected void applyOppDamage(Pokemon pokemon, double damage) {
        super.applyOppDamage(pokemon, damage);
    }
}
