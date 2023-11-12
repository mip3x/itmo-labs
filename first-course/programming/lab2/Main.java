import ru.ifmo.se.pokemon.Battle;
import ru.ifmo.se.pokemon.Pokemon;

public class Main {
    public static void main(String[] args) {
        Battle battle = new Battle();
        Pokemon pokemon1 = new Pokemon("Чужой", 1);
        Pokemon pokemon2 = new Pokemon("Хищник", 1);
        battle.addAlly(pokemon1);
        battle.addFoe(pokemon2);
        battle.go();
    }
}
