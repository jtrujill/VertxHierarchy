package com.github.jtrujill.hierarchy.app.repo

import com.google.inject.Inject
import io.vertx.core.json.JsonArray
import io.vertx.ext.asyncsql.AsyncSQLClient

class NodeRepo @Inject constructor(private val client: AsyncSQLClient) {
    fun insertNodes(parentId: String?, nodes: List<String>) {
        val uniqueNodes = nodes.toSet()

        val params = JsonArray()
        val bulkInsert = uniqueNodes.map {
            params.add(it).add(parentId)
            "(?, ?)"
        }.joinToString()

        client.getConnection {
            if (it.succeeded()) {
                val connection = it.result()
                val sql = "INSERT INTO hierarchy (name, parent_id) VALUES $bulkInsert"

                try {
                    connection.updateWithParams(sql, params) { result ->
                        if (result.succeeded()) {
                            val updatedResult = result.result().toJson()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                // Failed to get connection - deal with it
                println("Result failed")
            }
        }
    }
}