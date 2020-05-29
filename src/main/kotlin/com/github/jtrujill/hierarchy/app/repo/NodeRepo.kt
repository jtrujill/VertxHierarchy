package com.github.jtrujill.hierarchy.app.repo

import com.github.jtrujill.hierarchy.app.models.Node
import com.google.inject.Inject
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.asyncsql.AsyncSQLClient
import io.vertx.ext.sql.SQLConnection
import org.modelmapper.ModelMapper
import java.util.*


class NodeRepo @Inject constructor(private val client: AsyncSQLClient, private val mapper: ModelMapper) {
    fun bulkInsert(parentId: String?, nodes: List<String>): Future<List<Node>> {
        val uniqueNodes = nodes.toSet()

        return getConnection().compose { conn ->
            bulkInsert(conn, parentId, uniqueNodes)
                .compose {
                    getNewlyInsertedNodes(conn, it)
                }.map(::mapRowsToListNode)
        }
    }

    private fun mapRowsToListNode(data: List<JsonObject>): List<Node> {
        return data.map {
            mapper.map(it.map, Node::class.java)
        }.toList()
    }

    private fun bulkInsert(
        conn: SQLConnection,
        parentId: String?,
        uniqueNodes: Set<String>
    ): Future<String> {
        val batchSeq = UUID.randomUUID().toString()
        val params = JsonArray()
        val bulkInsert = uniqueNodes.map {
            params.add(it).add(parentId).add(batchSeq)
            "(?, ?, ?)"
        }.joinToString()

        val insertPromise = Promise.promise<String>()
        val sql = "INSERT INTO hierarchy (name, parent_id, batch_seq) VALUES $bulkInsert"
        conn.updateWithParams(sql, params) { result ->
            if (result.succeeded()) {
                insertPromise.complete(batchSeq)
            } else {
                insertPromise.fail(result.cause())
            }
        }
        return insertPromise.future()
    }

    private fun getNewlyInsertedNodes(
        conn: SQLConnection,
        batchSeq: String
    ): Future<List<JsonObject>> {
        val params = JsonArray().add(batchSeq)
        val sql = "SELECT id, name, parent_id FROM hierarchy WHERE batch_seq=?"

        val nodesPromise = Promise.promise<List<JsonObject>>()
        conn.queryWithParams(sql, params) {
            if (it.succeeded()) {
                nodesPromise.complete(it.result().rows)
            } else {
                nodesPromise.fail(it.cause())
            }
        }
        return nodesPromise.future()
    }

    fun getImmediateChildren(parentId: String?): Future<List<Node>> {
        return getConnection().compose { conn ->
            getNodeChildren(conn, parentId)
        }.map(::mapRowsToListNode)
    }

    private fun getNodeChildren(conn: SQLConnection, parentId: String?): Future<List<JsonObject>> {
        val promise = Promise.promise<List<JsonObject>>()
        val baseUrl = "SELECT id, name, parent_id FROM hierarchy WHERE parent_id"
        if (parentId == null) {
            conn.query("$baseUrl IS NULL") { result ->
                if (result.succeeded()) {
                    promise.complete(result.result().rows)
                } else {
                    promise.fail(result.cause())
                }
            }

        } else {
            conn.queryWithParams("$baseUrl = ?", JsonArray().add(parentId)) { result ->
                if (result.succeeded()) {
                    promise.complete(result.result().rows)
                } else {
                    promise.fail(result.cause())
                }
            }
        }

        return promise.future()
    }

    fun deleteNode(parentId: String): Future<Void> {
        return getConnection()
            .compose { conn ->
                deleteNodes(conn, parentId)
            }
    }

    private fun deleteNodes(
        conn: SQLConnection,
        parentId: String
    ): Future<Void> {
        val deletePromise = Promise.promise<Void>()
        val sql = "DELETE FROM hierarchy WHERE id = ?"
        conn.updateWithParams(sql, JsonArray().add(parentId)) {
            if (it.succeeded()) {
                deletePromise.complete()
            } else {
                deletePromise.fail(it.cause())
            }
        }
        return deletePromise.future()
    }

    private fun getConnection(): Future<SQLConnection> {
        val promise = Promise.promise<SQLConnection>()
        client.getConnection {
            if (it.succeeded()) {
                promise.complete(it.result())
            } else {
                promise.fail(it.cause())
            }
        }
        return promise.future()
    }
}