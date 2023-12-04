package moves.special;

import ru.ifmo.se.pokemon.SpecialMove;
import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.Stat;

public class Glaciate extends SpecialMove {
    public Glaciate() {
        super(Type.ICE, 65, 95);
    }

    @Override
    protected String describe() {
        return super.describe() + " with " + getClass().getSimpleName();
    }

    @Override
    protected void applyOppEffects(Pokemon pokemon) {
        // Glaciate deals damage and lowers the target's Speed by one stage.
        super.applyOppEffects(pokemon);

        Effect effect = new Effect().stat(Stat.SPEED, -1);
        pokemon.addEffect(effect);
    }

    @Override
    protected void applyOppDamage(Pokemon pokemon, double damage) {
        super.applyOppDamage(pokemon, damage);
    }
}
