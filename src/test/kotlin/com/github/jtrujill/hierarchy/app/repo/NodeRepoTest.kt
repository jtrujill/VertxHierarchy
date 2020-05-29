package com.github.jtrujill.hierarchy.app.repo

import io.mockk.mockk
import io.vertx.ext.asyncsql.AsyncSQLClient
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.modelmapper.ModelMapper

@ExtendWith(VertxExtension::class)
internal class NodeRepoTest {
    private lateinit var repo: NodeRepo

    @BeforeEach
    fun setup() {
        val asyncClient = mockk<AsyncSQLClient>()
        val modelMapper = mockk<ModelMapper>()
        repo = NodeRepo(asyncClient, modelMapper)
    }

    @Test
    fun whenBulkInsertReceivesNoValidNodeNames_ShouldFailFuture(testContext: VertxTestContext) {
        val nodes = listOf("  ", "")

        testContext
            .assertFailure(repo.bulkInsert("", nodes))
            .onComplete {
                Assertions.assertTrue(it.cause() is IllegalArgumentException)
                testContext.completeNow()
            }
    }
}