package moves.status;

import ru.ifmo.se.pokemon.StatusMove;
import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.Stat;

public class Confide extends StatusMove {
    public Confide() {
        super(Type.NORMAL, 0, 100);
    }

    @Override
    protected String describe() {
        return super.describe() + " with " + getClass().getSimpleName();
    }

    @Override
    protected void applyOppEffects(Pokemon pokemon) {
        // Confide lowers the target's Special Attack by one stage.
        super.applyOppEffects(pokemon);

        Effect effect = new Effect().stat(Stat.SPECIAL_ATTACK, -1);
        pokemon.addEffect(effect);
    }
}
