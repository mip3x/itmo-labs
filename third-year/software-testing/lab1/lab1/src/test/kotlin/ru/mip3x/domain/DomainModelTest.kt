package ru.mip3x.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DomainModelTest {
    @Test
    fun sceneFromFactoryMatchesNarrative() {
        val scene = DomainModelFactory.createHitchhikerScene()

        assertEquals("Золотое сердце", scene.spaceShip.name)
        assertEquals(4, scene.characters.size)

        val zaphod = scene.characters.first {
            character -> character.name == "Зафод"
        }
        val ford = scene.characters.first {
            character -> character.name == "Форд"
        }
        val trillian = scene.characters.first {
            character -> character.name == "Триллиан"
        }
        val arthur = scene.characters.first {
            character -> character.name == "Артур"
        }

        assertEquals(SpaceShipLocation.BRIDGE_UNDER_PALM, zaphod.spaceShipLocation)
        assertEquals(CharacterActivityType.DRINKING, zaphod.activity.type)
        assertTrue(zaphod.activity.details.contains("Пангалактический бульк-бластер"))

        assertEquals(SpaceShipLocation.BRIDGE_CORNER, ford.spaceShipLocation)
        assertEquals(CharacterActivityType.DISCUSSING, ford.activity.type)
        assertTrue(ford.activity.details.contains("Триллиан"))

        assertEquals(SpaceShipLocation.BRIDGE_CORNER, trillian.spaceShipLocation)
        assertEquals(CharacterActivityType.DISCUSSING, trillian.activity.type)
        assertTrue(trillian.activity.details.contains("Форд"))

        assertEquals(SpaceShipLocation.CABIN, arthur.spaceShipLocation)
        assertEquals(CharacterActivityType.READING, arthur.activity.type)
        assertTrue(arthur.activity.details.contains("Путеводитель по Галактике для автостопщиков"))
    }

    @Test
    fun discussRequiresAtLeastOneCompanion() {
        val ford = Character("Форд")

        assertThrows<IllegalArgumentException> {
            ford.discuss("Автомобили", emptyList())
        }
    }

    @Test
    fun drinkRequiresPositivePortions() {
        val zaphod = Character("Зафод")

        assertThrows<IllegalArgumentException> {
            zaphod.drink("Пангалактический бульк-бластер", 0)
        }

        assertThrows<IllegalArgumentException> {
            zaphod.drink("Пангалактический бульк-бластер", -1)
        }
    }

    @Test
    fun spaceshipDistanceCanBeSetAndRead() {
        val ship = SpaceShip("Золотое сердце")
        val nebula = CelestialObject("Туманность Конской Головы")

        ship.setDistance(nebula, 420)
        assertEquals(420, ship.getDistanceTo(nebula))

        ship.setDistance(nebula, 0)
        assertEquals(0, ship.getDistanceTo(nebula))

        assertThrows<IllegalArgumentException> {
            ship.setDistance(nebula, -1)
        }
    }
}