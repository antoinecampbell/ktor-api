package com.antoinecampbell.ktor.item

import com.antoinecampbell.ktor.item.ItemTable.date
import com.antoinecampbell.ktor.item.ItemTable.id
import com.antoinecampbell.ktor.item.ItemTable.name
import com.antoinecampbell.ktor.item.ItemTable.offsetTimestamp
import com.antoinecampbell.ktor.item.ItemTable.timestamp
import com.antoinecampbell.ktor.item.ItemTable.zonedTimestamp
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.javatime.CurrentDate
import org.jetbrains.exposed.v1.javatime.CurrentTimestamp
import org.jetbrains.exposed.v1.javatime.date
import org.jetbrains.exposed.v1.javatime.timestamp
import org.jetbrains.exposed.v1.jdbc.insertReturning
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset

interface ItemRepository {
    suspend fun save(item: Item): Item
    suspend fun findById(id: Int): Item?
    suspend fun findAll(): List<Item>
}

object ItemTable : IntIdTable("item") {
    val name = text("name")
    val timestamp = timestamp("timestamp").defaultExpression(CurrentTimestamp)
    val date = date("date").defaultExpression(CurrentDate)
    val zonedTimestamp = timestamp("zoned_timestamp").defaultExpression(CurrentTimestamp)
    val offsetTimestamp = timestamp("offset_timestamp").defaultExpression(CurrentTimestamp)
}

class ItemDao(
    id: EntityID<Int>
) : IntEntity(id) {
    companion object : IntEntityClass<ItemDao>(ItemTable)

    var name: String by ItemTable.name
    var timestamp: Instant by ItemTable.timestamp
    var date: LocalDate by ItemTable.date
    var zonedTimestamp: Instant by ItemTable.zonedTimestamp
    var offsetTimestamp: Instant by ItemTable.offsetTimestamp
}

fun ItemDao.toItem() = Item(
    id = id.value,
    name = name,
    timestamp = timestamp,
    date = date,
    zonedTimestamp = zonedTimestamp.atZone(ZoneOffset.systemDefault()),
    offsetTimestamp = offsetTimestamp.atOffset(OffsetDateTime.now().offset)
)

fun ResultRow.toItem() = Item(
    id = this[id].value,
    name = this[name],
    timestamp = this[timestamp],
    date = this[date],
    zonedTimestamp = this[zonedTimestamp].atZone(ZoneId.systemDefault()),
    offsetTimestamp = this[offsetTimestamp].atOffset(OffsetDateTime.now().offset)
)

/**
 * Repository using JetBrains Exposed ORM via DAO
 */
class DaoItemRepository : ItemRepository {
    override suspend fun save(item: Item) = suspendTransaction {
        ItemDao.new { name = item.name }.toItem()
    }

    override suspend fun findById(id: Int) = suspendTransaction {
        ItemDao.findById(id)?.toItem()
    }

    override suspend fun findAll() = suspendTransaction {
        ItemDao.all().map(ItemDao::toItem)
    }
}

/**
 * Repository using JetBrains Exposed ORM via Table
 */
class TableItemRepository : ItemRepository {
    override suspend fun save(item: Item) = suspendTransaction {
        ItemTable
            .insertReturning {
                it[name] = item.name
            }.single()
            .toItem()
    }

    override suspend fun findById(id: Int) = suspendTransaction {
        ItemTable
            .selectAll()
            .where { ItemTable.id eq id }
            .singleOrNull()
            ?.toItem()
    }

    override suspend fun findAll() = suspendTransaction {
        ItemTable.selectAll().toList().map { it.toItem() }
    }
}
