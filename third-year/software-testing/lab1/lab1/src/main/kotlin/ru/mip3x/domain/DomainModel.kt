package ru.mip3x.domain

enum class SpaceShipLocation {
    BRIDGE_UNDER_PALM,
    BRIDGE_CORNER,
    CABIN
}

enum class FlightActivityType {
    FLYING,
    STAYING
}

data class FlightActivity(
    val type: FlightActivityType,
    val details: String
)

class SpaceShip(val name: String) {
    private val distances = mutableMapOf<CelestialObject, Long>()

    var flightActivity: FlightActivity = FlightActivity(
        FlightActivityType.STAYING, "Not flying")
        private set

    fun setDistance(target: CelestialObject, distance: Long) {
        require(distance >= 0) {
            "distance must be non-negative"
        }
        distances[target] = distance
    }

    fun getDistanceTo(target: CelestialObject): Long {
        return distances[target] ?: 0L
    }

    fun flyAwayFrom(target: CelestialObject, distance: Long) {
        setDistance(target, distance)
        flightActivity = FlightActivity(
            FlightActivityType.FLYING,
            "Leaves light years between ship and ${target.name}"
        )
    }
}

class Character(val name: String) {
    var activity: CharacterActivity = CharacterActivity(CharacterActivityType.RESTING, "chiiill")
        private set

    var spaceShipLocation: SpaceShipLocation? = null
        private set

    fun moveTo(spaceShipLocation: SpaceShipLocation) {
        this.spaceShipLocation = spaceShipLocation
    }

    fun read(book: Book) {
        activity = CharacterActivity(
            CharacterActivityType.READING,
            "reads '${book.owner}''s '${book.title}'"
        )
    }

    fun discuss(topic: String, withCharacters: List<Character>) {
        require(withCharacters.isNotEmpty()) {
            "withCharacters must not be empty"
        }

        val joinedNames = withCharacters.joinToString(", ") {
            character -> character.name
        }

        activity = CharacterActivity(
            CharacterActivityType.DISCUSSING,
            "discusses '$topic' with $joinedNames"
        )
    }

    fun drink(drinkName: String, portions: Int) {
        require(portions > 0) {
            "portions must be positive"
        }

        activity = CharacterActivity(
            CharacterActivityType.DRINKING,
            "drinks '$drinkName' in portions: $portions"
        )
    }
}

data class CelestialObject(val name: String)

data class SceneState (
    val spaceShip: SpaceShip,
    val characters: List<Character> 
)

data class Book(
    val title: String,
    val owner: Character
)

enum class CharacterActivityType {
    READING,
    DISCUSSING,
    RESTING,
    DRINKING
}

data class CharacterActivity(
    val type: CharacterActivityType,
    val details: String
)

object DomainModelFactory {
    fun createHitchhikerScene() : SceneState {
        val spaceShip = SpaceShip("Золотое сердце")
        val horseHeadNebula = CelestialObject("Туманность Конской Головы")
        spaceShip.flyAwayFrom(horseHeadNebula, 420)

        var characters: MutableList<Character> = mutableListOf<Character>()

        val zaphod = Character("Зафод")
        characters.add(zaphod)
        zaphod.moveTo(SpaceShipLocation.BRIDGE_UNDER_PALM)
        zaphod.drink("Пангалактический бульк-бластер", 42)

        val ford = Character("Форд")
        characters.add(ford)
        ford.moveTo(SpaceShipLocation.BRIDGE_CORNER)

        val trillian = Character("Триллиан")
        characters.add(trillian)
        trillian.moveTo(SpaceShipLocation.BRIDGE_CORNER)

        val discussTopicNumberOne = "Жизнь и её последствия"
        ford.discuss(
            discussTopicNumberOne,
            mutableListOf(trillian)
        )
        trillian.discuss(
            discussTopicNumberOne,
            mutableListOf(ford)
        )

        val arthur = Character("Артур")
        characters.add(arthur)
        arthur.moveTo(SpaceShipLocation.CABIN)

        val guideBook = Book(
            "Путеводитель по Галактике для автостопщиков",
            ford
        )
        arthur.read(guideBook)

        return SceneState(
            spaceShip,
            characters
        )
    }
}
