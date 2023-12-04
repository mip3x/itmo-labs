package moves.physical;

import ru.ifmo.se.pokemon.PhysicalMove;
import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.Stat;

public class SteelWing extends PhysicalMove {
    public SteelWing() {
        super(Type.STEEL, 70, 90);
    }

    @Override
    protected String describe() {
        return super.describe() + " with " + getClass().getSimpleName();
    }

    @Override
    protected void applySelfEffects(Pokemon pokemon) {
        // Steel Wing deals damage and has a 10% chance of raising the user's Defense by
        // one stage.

        super.applySelfEffects(pokemon);

        Effect effect = new Effect().chance(0.1).stat(Stat.DEFENSE, 1);
        pokemon.addEffect(effect);
    }

    @Override
    protected void applyOppDamage(Pokemon pokemon, double damage) {
        super.applyOppDamage(pokemon, damage);
    }
}
