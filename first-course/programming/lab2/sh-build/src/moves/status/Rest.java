package moves.status;

import ru.ifmo.se.pokemon.StatusMove;
import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.Status;

public class Rest extends StatusMove {
    public Rest() {
        super(Type.PSYCHIC, 0, 100);
    }

    @Override
    protected String describe() {
        return super.describe() + " with " + getClass().getSimpleName();
    }

    @Override
    protected void applySelfEffects(Pokemon pokemon) {
        // User sleeps for 2 turns, but user is fully healed.
        super.applySelfEffects(pokemon);

        Effect effect = new Effect().turns(2).condition(Status.SLEEP);
        pokemon.restore(); // heals pokemon
        pokemon.addEffect(effect);
    }
}
