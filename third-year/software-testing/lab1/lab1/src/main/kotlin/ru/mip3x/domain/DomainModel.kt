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

enum class DrinkType {
    PAN_GALACTIC_GARGLE_BLASTER,
    TEA,
    WATER,
    COFFEE
}

data class FlightActivity(
    val type: FlightActivityType,
)

data class Drink(
    val name: String,
    val type: DrinkType
)

class Discussion(
    var topic: String,
    discussionParticipants: Set<Character>
) {
    private val _participants = discussionParticipants.toMutableSet()
    val participants: Set<Character> get() = _participants

    init {
        require(_participants.size >= 2) {
            "Discussion must have at least 2 members"
        }
    }

    fun join(character: Character) {
        _participants.add(character)
    }

    fun leave(character: Character) {
        require(_participants.size > 2) {
            "Cannot leave discussion with only two participants"
        }
        _participants.remove(character)
    }
}

class SpaceShip(val name: String) {
    private val distances = mutableMapOf<CelestialObject, Long>()

    var flightActivity: FlightActivity = FlightActivity(FlightActivityType.STAYING)
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
        flightActivity = FlightActivity(FlightActivityType.FLYING)
    }
}

class Character(val name: String) {
    var activity: CharacterActivity = CharacterActivity(CharacterActivityType.RESTING)
        private set

    var spaceShipLocation: SpaceShipLocation? = null
        private set

    var bodyState: BodyState = BodyState.NORMAL
        private set

    var currentBook: Book? = null
        private set

    var lastDrink: Drink? = null
        private set

    fun moveTo(spaceShipLocation: SpaceShipLocation) {
        this.spaceShipLocation = spaceShipLocation
    }

    fun read(book: Book) {
        activity = CharacterActivity(CharacterActivityType.READING)
        currentBook = book
    }

    fun drink(drink: Drink, portions: Int) {
        require(portions > 0) {
            "portions must be positive"
        }
        
        lastDrink = drink
        bodyState = when (drink.type) {
            DrinkType.PAN_GALACTIC_GARGLE_BLASTER -> if (portions >= 10) BodyState.DRUNK else BodyState.ENERGIZED
            DrinkType.COFFEE -> BodyState.ENERGIZED
            DrinkType.TEA -> BodyState.RELAXED
            DrinkType.WATER -> BodyState.NORMAL
        }

        activity = CharacterActivity(CharacterActivityType.DRINKING)
    }
}

data class CelestialObject(val name: String)

data class SceneState(
    val spaceShip: SpaceShip,
    val characters: List<Character>
)

data class Book(
    val title: String,
    val owner: Character
)

enum class CharacterActivityType {
    READING,
    RESTING,
    DRINKING
}

data class CharacterActivity(
    val type: CharacterActivityType,
)

enum class BodyState {
    NORMAL,
    RELAXED,
    ENERGIZED,
    DRUNK
}

object DomainModelFactory {
    fun createHitchhikerScene() : SceneState {
        val goldenHeartSpaceShip = SpaceShip("Золотое сердце")
        val horseHeadNebula = CelestialObject("Туманность Конской Головы")
        goldenHeartSpaceShip.flyAwayFrom(horseHeadNebula, 420)

        var characters: MutableList<Character> = mutableListOf<Character>()

        val cocaCola = Drink("Coca-Cola", DrinkType.PAN_GALACTIC_GARGLE_BLASTER)

        val zaphod = Character("Зафод")
        characters.add(zaphod)
        zaphod.moveTo(SpaceShipLocation.BRIDGE_UNDER_PALM)
        zaphod.drink(cocaCola, 42)

        val ford = Character("Форд")
        characters.add(ford)
        ford.moveTo(SpaceShipLocation.BRIDGE_CORNER)

        val trillian = Character("Триллиан")
        characters.add(trillian)
        trillian.moveTo(SpaceShipLocation.BRIDGE_CORNER)

        val discussTopicNumberOne = "Жизнь и её последствия"
        val discussion = Discussion(discussTopicNumberOne, setOf(ford, trillian))

        val arthur = Character("Артур")
        characters.add(arthur)
        arthur.moveTo(SpaceShipLocation.CABIN)

        val guideBook = Book(
            "Путеводитель по Галактике для автостопщиков",
            ford
        )
        arthur.read(guideBook)

        return SceneState(
            goldenHeartSpaceShip,
            characters
        )
    }
}
