import pokemons.*;;
import ru.ifmo.se.pokemon.Battle;
import ru.ifmo.se.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // create battle
        Battle battle = new Battle();

        // load and shuffle pokemons
        List<Pokemon> pokemons = new ArrayList<>(
                List.of(new Kyurem("Базыч", 1), new Tyrogue("Boba4_777", 1), new Hitmonchan("Дугин", 1),
                        new Budew("Joe the Nword", 1), new Roselia("Rose666", 1), new Roserade("Roserade007", 1)));
        Collections.shuffle(pokemons);

        // print loaded pokemons
        System.out.println("  POKEMONS LOADED  ");
        pokemons.forEach(
                pokemon -> System.out.printf(" | [%d] %s %n", pokemon.getLevel(), pokemon.getClass().getSimpleName()));
        System.out.println();

        // add to teams
        for (int i = 0; i < pokemons.size(); i++) {
            if (i % 2 == 0) {
                battle.addFoe(pokemons.get(i));
            } else {
                battle.addAlly(pokemons.get(i));
            }
        }

        // start battle
        battle.go();
    }
}
