package com.github.jtrujill.hierarchy.web.services

import com.github.jtrujill.hierarchy.app.UserWorshiper
import com.github.jtrujill.hierarchy.app.models.Node
import com.github.jtrujill.hierarchy.web.models.HierarchicalBody
import com.google.inject.Inject
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.api.OperationRequest
import io.vertx.ext.web.api.OperationResponse
import org.modelmapper.ModelMapper

class HierarchyServiceImpl @Inject constructor(
    private val mapper: ModelMapper,
    private val userWorship: UserWorshiper
) :
    HierarchyService {
    override fun getHierarchy(
        parentId: String,
        context: OperationRequest,
        resultHandler: Handler<AsyncResult<OperationResponse>>
    ) {
        TODO("Not yet implemented")
    }

    override fun postRootHierarchy(
        body: HierarchicalBody,
        context: OperationRequest,
        resultHandler: Handler<AsyncResult<OperationResponse>>
    ) {
        return postHierarchy(null, body, context, resultHandler)
    }

    override fun postHierarchy(
        parentId: String?,
        body: HierarchicalBody,
        context: OperationRequest,
        resultHandler: Handler<AsyncResult<OperationResponse>>
    ) {
        val uniqueNodes = body.nodes.map {
            mapper.map(it, Node::class.java)
        }.toSet()

//        TODO: insert nodes into the db

        resultHandler.handle(
            Future.succeededFuture(
                OperationResponse.completedWithJson(JsonObject())
            )
        )
    }

    override fun deleteHierarchy(
        parentId: String,
        context: OperationRequest,
        resultHandler: Handler<AsyncResult<OperationResponse>>
    ) {
        TODO("Not yet implemented")
    }
}