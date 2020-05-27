package com.github.jtrujill.hierarchy

import com.github.jtrujill.hierarchy.core.GuiceVerticleFactory
import com.github.jtrujill.hierarchy.core.MainModule
import com.github.jtrujill.hierarchy.core.VertxModule
import com.github.jtrujill.hierarchy.web.verticles.MainVerticle
import com.google.inject.CreationException
import com.google.inject.Guice
import io.netty.util.internal.logging.InternalLoggerFactory
import io.netty.util.internal.logging.Slf4JLoggerFactory
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.logging.LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME
import io.vertx.core.logging.SLF4JLogDelegateFactory
import io.vertx.kotlin.core.deployVerticleAwait
import org.flywaydb.core.Flyway
import java.lang.System.setProperty

private val logger = org.slf4j.LoggerFactory.getLogger(MainVerticle::class.java)

suspend fun main(args: Array<String>) {
    setupLogging()
    migrateDb()

    val vertx = Vertx.vertx()
    initGuice(args, vertx)
    vertx.deployVerticleAwait("kotlin-guice:${MainVerticle::class.java.canonicalName}")
}

private fun setupLogging() {
    setProperty(
        LOGGER_DELEGATE_FACTORY_CLASS_NAME,
        SLF4JLogDelegateFactory::class.java.name
    )
    LoggerFactory.getLogger(LoggerFactory::class.java)
    InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE)
}

private fun migrateDb() {
    try {
        val dbUrl = "jdbc:mysql://mysql/vertx_hierarchy"
        val flyway = Flyway.configure().dataSource(dbUrl, "jeremy", "super_secret").load()
        flyway.migrate()
    } catch (e: Exception) {
        logger.error("Failed to migrate database", e)
        throw e
    }
}

private fun initGuice(args: Array<String>, vertx: Vertx) {
    try {
        val guice = Guice.createInjector(
            MainModule(args),
            VertxModule(vertx)
        )
        vertx.registerVerticleFactory(GuiceVerticleFactory(guice))
    } catch (e: CreationException) {
        logger.error("Failed to create GUICE modules", e)
    }
}