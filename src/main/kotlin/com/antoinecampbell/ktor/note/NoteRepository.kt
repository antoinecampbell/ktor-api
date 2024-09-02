package com.antoinecampbell.ktor.note

import java.sql.ResultSet
import java.sql.SQLException
import java.time.OffsetDateTime
import java.time.ZoneId
import javax.sql.DataSource

interface NoteRepository {
    suspend fun save(note: Note): Note
    suspend fun findById(id: Int): Note?
    suspend fun findAll(): List<Note>
}

/**
 * Repository using plain JDBC
 */
class DefaultNoteRepository(private val dataSource: DataSource) : NoteRepository {
    override suspend fun save(note: Note): Note {
        return dataSource.connection.use { connection ->
            connection.prepareStatement("INSERT INTO note(name) VALUES (?) RETURNING *").apply {
                setString(1, note.name)
            }.executeQuery()
                .let { if (it.next()) it.toNote() else throw SQLException("Could not create new note") }
        }
    }

    override suspend fun findById(id: Int): Note? {
        return dataSource.connection.use { connection ->
            connection.prepareStatement("SELECT * FROM note WHERE id = ?").apply {
                setInt(1, id)
            }.executeQuery()
                .let { if (it.next()) it.toNote() else null }
        }
    }

    override suspend fun findAll(): List<Note> {
        return dataSource.connection.use { connection ->
            connection.prepareStatement("SELECT * FROM note").executeQuery()
                .let {
                    mutableListOf<Note>().apply {
                        while (it.next()) {
                            add(it.toNote())
                        }
                    }
                }
        }
    }
}

fun ResultSet.toNote(): Note {
    return Note(
        id = getInt("id"),
        name = getString("name"),
        date = getDate("date").toLocalDate(),
        timestamp = getTimestamp("timestamp").toInstant(),
        zonedTimestamp = getTimestamp("zoned_timestamp").toInstant().atZone(ZoneId.systemDefault()),
        offsetTimestamp = getTimestamp("offset_timestamp").toInstant().atOffset(OffsetDateTime.now().offset)
    )
}
