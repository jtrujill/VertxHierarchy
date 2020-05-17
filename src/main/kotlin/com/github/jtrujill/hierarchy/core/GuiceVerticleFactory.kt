package com.github.jtrujill.hierarchy.core

import com.google.inject.Injector
import io.vertx.core.Verticle
import io.vertx.core.impl.verticle.CompilingClassLoader
import io.vertx.core.spi.VerticleFactory


class GuiceVerticleFactory(val injector: Injector) : VerticleFactory {
    val PREFIX = "kotlin-guice"

    override fun createVerticle(verticleNameWithPrefix: String, classLoader: ClassLoader): Verticle {
        val verticleName = VerticleFactory.removePrefix(verticleNameWithPrefix)

        val clazz = if (verticleName.endsWith(".java")) {
            val compilingLoader = CompilingClassLoader(classLoader, verticleName)
            val className = compilingLoader.resolveMainClassName()
            compilingLoader.loadClass(className)
        } else {
            classLoader.loadClass(verticleName)
        }

        return injector.getInstance(clazz) as Verticle
    }

    override fun prefix(): String {
        return PREFIX
    }

}