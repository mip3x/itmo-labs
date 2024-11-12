package moves.status;

import ru.ifmo.se.pokemon.StatusMove;
import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.Stat;

public class DoubleTeam extends StatusMove {
    public DoubleTeam() {
        super(Type.NORMAL, 0, 100);
    }

    @Override
    protected String describe() {
        return super.describe() + " with " + getClass().getSimpleName();
    }

    @Override
    protected void applySelfEffects(Pokemon pokemon) {
        // Double Team raises the user's Evasiveness by one stage
        super.applySelfEffects(pokemon);

        Effect effect = new Effect().stat(Stat.EVASION, 1);
        pokemon.addEffect(effect);
    }
}
