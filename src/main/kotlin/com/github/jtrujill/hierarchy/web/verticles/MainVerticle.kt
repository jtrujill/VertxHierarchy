package com.github.jtrujill.hierarchy.web.verticles

import com.github.jtrujill.hierarchy.core.WebPort
import com.github.jtrujill.hierarchy.web.services.HierarchyService
import com.google.inject.Inject
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import io.vertx.serviceproxy.ServiceBinder
import io.vertx.serviceproxy.ServiceException
import org.slf4j.LoggerFactory


class MainVerticle @Inject constructor(@WebPort private val port: Int, private val hierarchyService: HierarchyService) :
    AbstractVerticle() {
    private val logger = LoggerFactory.getLogger(MainVerticle::class.java)
    private lateinit var serviceBinder: ServiceBinder
    private lateinit var server: HttpServer
    private lateinit var hierarchyConsumer: MessageConsumer<JsonObject>

    override fun start(startPromise: Promise<Void>) {
        serviceBinder = ServiceBinder(vertx)

        startHierarchyService()
        startHttpServer().onComplete {
            if (it.succeeded()) {
                startPromise.complete()
            } else {
                startPromise.fail(it.cause())
            }
        }
    }

    override fun stop() {
        this.server.close()
        hierarchyConsumer.unregister()
    }

    private fun startHierarchyService() {
        hierarchyConsumer = serviceBinder
            .setAddress("hierarchy.App")
            .register(HierarchyService::class.java, hierarchyService)
    }

    private fun startHttpServer(): Future<Void> {
        val promise = Promise.promise<Void>()
        OpenAPI3RouterFactory.create(vertx, "/openapi.json") {
            if (it.succeeded()) {
                val routerFactory = it.result()
                routerFactory.mountServicesFromExtensions()

                val router = routerFactory.router

                // All this error routing logic would be better in a separate class especially if there are more verticle
                //  but with the logic not being too complex and there only being one verticle, nah
                router.errorHandler(400, ::handleBadRequestException)
                router.errorHandler(404, ::handleNotFoundException)
                router.errorHandler(405, ::handleMethodNotAllowed)
                router.errorHandler(500, ::handleInternalException)

                server = vertx.createHttpServer(HttpServerOptions().setPort(port).setHost("0.0.0.0"))

                server.requestHandler(router).listen { listenResult ->
                    if (listenResult.succeeded()) {
                        promise.complete()
                        logger.info("${MainVerticle::class.java.name} started")
                    } else {
                        promise.fail(listenResult.cause())
                    }
                }
            } else {
                promise.fail(it.cause())
                logger.error("Failed to initialize ${MainVerticle::class.java.canonicalName} router", it.cause())
            }
        }
        return promise.future()
    }

    private fun handleBadRequestException(routingContext: RoutingContext) {
        var message = routingContext.failure()?.message ?: "Invalid request"
        message = message.replace("$.", "")
        createError(routingContext, message, 400, "invalidRequestError")
    }

    private fun handleNotFoundException(routingContext: RoutingContext) {
        val message = routingContext.failure()?.message ?: "Resource not found"
        createError(routingContext, message, 404, "notFoundError")
    }

    private fun handleMethodNotAllowed(routingContext: RoutingContext) {
        val msg = "Method not allowed with the specified resource"
        createError(routingContext, msg, 405, "methodNotAllowed")
    }

    private fun handleInternalException(routingContext: RoutingContext) {
        val exception = routingContext.failure()
        if (exception is ServiceException && exception.failureCode() != 500) {
            routingContext.fail(exception.failureCode(), exception)
        } else {
            val msg =
                "We apologize that an error occurred while servicing your request. If the problem persists please send an email to blah.blah@blah.com"
            createError(routingContext, msg, 500, "apiError")
        }
    }

    private fun createError(routingContext: RoutingContext, message: String, code: Int, codeMsg: String = "apiError") {
        val errorObject = JsonObject().put("error", JsonObject().put("code", codeMsg).put("message", message))
        routingContext
            .response()
            .setStatusCode(code)
            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .end(errorObject.encode())
    }
}