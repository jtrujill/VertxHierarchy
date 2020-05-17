package com.github.jtrujill.hierarchy.core

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.file.FileSystem
import io.vertx.core.json.JsonObject
import io.vertx.core.shareddata.SharedData
import java.util.*


class VertxModule(val vertx: Vertx) : AbstractModule() {
    private val context: Context = vertx.orCreateContext

    override fun configure() {
        bind(Vertx::class.java).toInstance(vertx)
        bind(EventBus::class.java).toInstance(vertx.eventBus())
        bind(FileSystem::class.java).toInstance(vertx.fileSystem())
        bind(SharedData::class.java).toInstance(vertx.sharedData())

        Names.bindProperties(
                binder(),
                extractToProperties(context.config())
        )
    }

    private fun extractToProperties(config: JsonObject): Properties? {
        val properties = Properties()
        config.map.keys.stream().forEach { key: String? ->
            properties.setProperty(
                    key,
                    config.getValue(key) as String
            )
        }
        return properties
    }
}