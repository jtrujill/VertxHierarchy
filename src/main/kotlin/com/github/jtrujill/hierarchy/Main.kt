package com.github.jtrujill.hierarchy

import com.github.jtrujill.hierarchy.core.GuiceVerticleFactory
import com.github.jtrujill.hierarchy.core.MainModule
import com.github.jtrujill.hierarchy.core.VertxModule
import com.github.jtrujill.hierarchy.web.verticles.MainVerticle
import com.google.inject.Guice
import io.netty.util.internal.logging.InternalLoggerFactory
import io.netty.util.internal.logging.Slf4JLoggerFactory
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.logging.LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME
import io.vertx.core.logging.SLF4JLogDelegateFactory
import io.vertx.kotlin.core.deployVerticleAwait
import java.lang.System.setProperty


suspend fun main(args: Array<String>) {
    setProperty(
            LOGGER_DELEGATE_FACTORY_CLASS_NAME,
            SLF4JLogDelegateFactory::class.java.name
    )
    LoggerFactory.getLogger(LoggerFactory::class.java)
    InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE)

    val vertx = Vertx.vertx()
    val guice = Guice.createInjector(
            MainModule(args),
            VertxModule(vertx)
    )

    vertx.registerVerticleFactory(GuiceVerticleFactory(guice))
    vertx.deployVerticleAwait("kotlin-guice:${MainVerticle::class.java.canonicalName}")
}