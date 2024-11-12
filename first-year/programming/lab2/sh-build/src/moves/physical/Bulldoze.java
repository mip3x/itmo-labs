package moves.physical;

import ru.ifmo.se.pokemon.PhysicalMove;
import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.Stat;

public class Bulldoze extends PhysicalMove {
    public Bulldoze() {
        super(Type.GROUND, 60, 100);
    }

    @Override
    protected String describe() {
        return super.describe() + " with " + getClass().getSimpleName();
    }

    @Override
    protected void applyOppEffects(Pokemon pokemon) {
        super.applyOppEffects(pokemon);

        Effect effect = new Effect().stat(Stat.SPEED, -1);
        pokemon.addEffect(effect);
    }

    @Override
    protected void applyOppDamage(Pokemon pokemon, double damage) {
        super.applyOppDamage(pokemon, damage);
    }
}
