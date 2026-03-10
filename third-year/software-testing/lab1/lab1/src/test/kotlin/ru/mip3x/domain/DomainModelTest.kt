package ru.mip3x.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertNotNull
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
        assertEquals(BodyState.DRUNK, zaphod.bodyState)

        assertNotNull(zaphod.lastDrink)
        assertEquals("Coca-Cola", zaphod.lastDrink?.name)
        assertEquals(DrinkType.PAN_GALACTIC_GARGLE_BLASTER, zaphod.lastDrink?.type)

        assertEquals(SpaceShipLocation.BRIDGE_CORNER, ford.spaceShipLocation)
        assertEquals(CharacterActivityType.RESTING, ford.activity.type)
        assertEquals(BodyState.NORMAL, ford.bodyState)

        assertEquals(SpaceShipLocation.BRIDGE_CORNER, trillian.spaceShipLocation)
        assertEquals(CharacterActivityType.RESTING, trillian.activity.type)
        assertEquals(BodyState.NORMAL, trillian.bodyState)

        assertEquals(SpaceShipLocation.CABIN, arthur.spaceShipLocation)
        assertEquals(CharacterActivityType.READING, arthur.activity.type)
        assertEquals(BodyState.NORMAL, arthur.bodyState)

        assertNotNull(arthur.currentBook)
        assertEquals("Путеводитель по Галактике для автостопщиков", arthur.currentBook?.title)
        assertEquals(ford, arthur.currentBook?.owner)
    }

    @Test
    fun discussionFromFactoryHasFordAndTrillian() {
        val scene = DomainModelFactory.createHitchhikerScene()

        val ford = scene.characters.first { 
            character -> character.name == "Форд"
        }
        val trillian = scene.characters.first {
            character -> character.name == "Триллиан"
        }

        val discussion = Discussion("Жизнь и её последствия", setOf(ford, trillian))

        assertEquals("Жизнь и её последствия", discussion.topic)
        assertEquals(2, discussion.participants.size)
        assertTrue(discussion.participants.contains(ford))
        assertTrue(discussion.participants.contains(trillian))

        discussion.topic = "Смерть и её последствия"
        assertEquals("Смерть и её последствия", discussion.topic)
    }

    @Test
    fun discussionRequiresAtLeastTwoParticipants() {
        val ford = Character("Форд")

        assertThrows<IllegalArgumentException> {
            Discussion("Жизнь и её последствия", setOf(ford))
        }
    }

    @Test
    fun discussionJoinAddsParticipant() {
        val ford = Character("Форд")
        val trillian = Character("Триллиан")
        val arthur = Character("Артур")

        val discussion = Discussion("Жизнь и её последствия", setOf(ford, trillian))
        discussion.join(arthur)

        assertEquals(3, discussion.participants.size)
        assertTrue(arthur in discussion.participants)
    }

    @Test
    fun discussionLeaveRemovesParticipantButKeepsAtLeastTwo() {
        val ford = Character("Форд")
        val trillian = Character("Триллиан")
        val arthur = Character("Артур")

        val discussion = Discussion("Жизнь и её последствия", setOf(ford, trillian, arthur))
        discussion.leave(arthur)

        assertEquals(2, discussion.participants.size)
        assertTrue(ford in discussion.participants)
        assertTrue(trillian in discussion.participants)
        assertTrue(arthur !in discussion.participants)
    }

    @Test
    fun discussionLeaveFailsWhenOnlyTwoParticipants() {
        val ford = Character("Форд")
        val trillian = Character("Триллиан")

        val discussion = Discussion("Жизнь и её последствия", setOf(ford, trillian))

        assertThrows<IllegalArgumentException> {
            discussion.leave(ford)
        }
    }

    @Test
    fun drinkRequiresPositivePortions() {
        val zaphod = Character("Зафод")
        val blaster = Drink("Пангалактический бульк-бластер", DrinkType.PAN_GALACTIC_GARGLE_BLASTER)

        assertThrows<IllegalArgumentException> {
            zaphod.drink(blaster, 0)
        }

        assertThrows<IllegalArgumentException> {
            zaphod.drink(blaster, -1)
        }
    }

    @Test
    fun drinkChangesBodyStateDependingOnDrinkTypeAndPortions() {
        val zaphod = Character("Зафод")
        val blaster = Drink("Пангалактический бульк-бластер", DrinkType.PAN_GALACTIC_GARGLE_BLASTER)
        val coffee = Drink("Coffee", DrinkType.COFFEE)
        val tea = Drink("Tea", DrinkType.TEA)
        val water = Drink("Water", DrinkType.WATER)

        assertEquals("Coffee", coffee.name)

        zaphod.drink(blaster, 1)
        assertEquals(BodyState.ENERGIZED, zaphod.bodyState)

        zaphod.drink(blaster, 10)
        assertEquals(BodyState.DRUNK, zaphod.bodyState)

        zaphod.drink(coffee, 1)
        assertEquals(BodyState.ENERGIZED, zaphod.bodyState)

        zaphod.drink(tea, 1)
        assertEquals(BodyState.RELAXED, zaphod.bodyState)

        zaphod.drink(water, 1)
        assertEquals(BodyState.NORMAL, zaphod.bodyState)
    }

    @Test
    fun spaceshipDistanceCanBeSetAndRead() {
        val ship = SpaceShip("Золотое сердце")
        val nebula = CelestialObject("Туманность Конской Головы")

        assertEquals("Туманность Конской Головы", nebula.name)
        assertEquals(FlightActivityType.STAYING, ship.flightActivity.type)
        assertEquals(ship.getDistanceTo(nebula), 0L)

        ship.setDistance(nebula, 420)
        assertEquals(420, ship.getDistanceTo(nebula))

        ship.setDistance(nebula, 0)
        assertEquals(0, ship.getDistanceTo(nebula))

        assertThrows<IllegalArgumentException> {
            ship.setDistance(nebula, -1)
        }
    }
}