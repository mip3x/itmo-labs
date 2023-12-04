package moves.status;

import ru.ifmo.se.pokemon.StatusMove;
import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.Stat;

public class Agility extends StatusMove {
    public Agility() {
        super(Type.PSYCHIC, 0, 100);
    }

    @Override
    protected String describe() {
        return super.describe() + " with " + getClass().getSimpleName();
    }

    @Override
    protected void applySelfEffects(Pokemon pokemon) {
        // Confide lowers the target's Special Attack by one stage.
        super.applySelfEffects(pokemon);

        Effect effect = new Effect().stat(Stat.SPEED, 2);
        pokemon.addEffect(effect);
    }
}
