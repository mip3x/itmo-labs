package moves.status;

import ru.ifmo.se.pokemon.StatusMove;
import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.Stat;

public class SwordsDance extends StatusMove {
    public SwordsDance() {
        super(Type.NORMAL, 0, 100);
    }

    @Override
    protected String describe() {
        return super.describe() + " with " + getClass().getSimpleName();
    }

    @Override
    protected void applySelfEffects(Pokemon pokemon) {
        // Swords Dance raises the user's Attack by two stages.
        super.applySelfEffects(pokemon);

        Effect effect = new Effect().stat(Stat.ATTACK, 2);
        pokemon.addEffect(effect);
    }
}
