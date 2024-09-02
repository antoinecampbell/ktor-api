package com.antoinecampbell.ktor.item

import com.antoinecampbell.ktor.plugins.suspendTransaction
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDate
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

object ItemTable : IntIdTable("item") {
    val name = text("name")
    val timestamp = timestamp("timestamp").defaultExpression(CurrentTimestamp)
    val date = date("date").defaultExpression(CurrentDate)
    val zonedTimestamp = timestamp("zoned_timestamp").defaultExpression(CurrentTimestamp)
}

class ItemDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ItemDao>(ItemTable)

    var name: String by ItemTable.name
    private var timestamp: Instant by ItemTable.timestamp
    private var date: LocalDate by ItemTable.date
    private var zonedTimestamp: Instant by ItemTable.zonedTimestamp

    fun toItem() = Item(
        id = id.value,
        name = name,
        timestamp = timestamp,
        date = date,
        zonedTimestamp = zonedTimestamp.atZone(ZoneOffset.systemDefault())
    )
}

interface ItemRepository {
    suspend fun save(item: Item): Item
    suspend fun findById(id: Int): Item?
    suspend fun findAll(): List<Item>
}

class DefaultItemRepository : ItemRepository {
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
