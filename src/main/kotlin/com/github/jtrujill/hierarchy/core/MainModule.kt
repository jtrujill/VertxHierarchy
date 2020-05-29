package com.github.jtrujill.hierarchy.core

import com.github.jtrujill.hierarchy.web.services.HierarchyService
import com.github.jtrujill.hierarchy.web.services.HierarchyServiceImpl
import com.google.inject.AbstractModule
import com.google.inject.BindingAnnotation
import com.google.inject.Provides
import com.google.inject.Singleton
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.asyncsql.AsyncSQLClient
import io.vertx.ext.asyncsql.MySQLClient
import org.modelmapper.ModelMapper
import org.modelmapper.convention.NameTokenizers


@BindingAnnotation
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class WebPort

class MainModule(private val args: Array<String>) : AbstractModule() {
    override fun configure() {
        bindConstant().annotatedWith(WebPort::class.java).to(8080)
        bind(HierarchyService::class.java).to(HierarchyServiceImpl::class.java)
    }

    @Provides
    @Singleton
    fun provideModelMapper(): ModelMapper {
        val modelMapper = ModelMapper()
        modelMapper.configuration
            .setSourceNameTokenizer(NameTokenizers.UNDERSCORE)
            .setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE)
        return modelMapper
    }

    @Provides
    @Singleton
    fun provideDbConnection(vertx: Vertx): AsyncSQLClient {
        val mySQLClientConfig = JsonObject()
            .put("host", "mysql")
            .put("username", "jeremy")
            .put("password", "super_secret")
            .put("database", "vertx_hierarchy")

        return MySQLClient.createShared(vertx, mySQLClientConfig)
    }
}