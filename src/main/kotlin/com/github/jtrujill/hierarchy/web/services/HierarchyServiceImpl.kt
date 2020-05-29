package com.github.jtrujill.hierarchy.web.services

import com.github.jtrujill.hierarchy.app.repo.NodeRepo
import com.github.jtrujill.hierarchy.web.models.HierarchicalBody
import com.google.inject.Inject
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonArray
import io.vertx.ext.web.api.OperationRequest
import io.vertx.ext.web.api.OperationResponse

private val logger = org.slf4j.LoggerFactory.getLogger(HierarchyServiceImpl::class.java)

class HierarchyServiceImpl @Inject constructor(
    private val nodeRepo: NodeRepo
) :
    HierarchyService {
    override fun getRootChildren(context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>) {
        getImmediateChildren(null, context, resultHandler)
    }

    override fun getImmediateChildren(
        parentId: String?,
        context: OperationRequest,
        resultHandler: Handler<AsyncResult<OperationResponse>>
    ) {
        nodeRepo.getImmediateChildren(parentId).map { nodes ->
            OperationResponse.completedWithJson(JsonArray(nodes))
        }.onComplete {
            resultHandler.handle(it)
        }
    }

    override fun postRootHierarchy(
        body: HierarchicalBody,
        context: OperationRequest,
        resultHandler: Handler<AsyncResult<OperationResponse>>
    ) {
        postHierarchy(null, body, context, resultHandler)
    }

    override fun postHierarchy(
        parentId: String?,
        body: HierarchicalBody,
        context: OperationRequest,
        resultHandler: Handler<AsyncResult<OperationResponse>>
    ) {
        nodeRepo.bulkInsert(parentId, body.nodes).map { nodes ->
            OperationResponse.completedWithJson(JsonArray(nodes))
        }.onComplete {
            resultHandler.handle(it)
        }
    }

    override fun deleteHierarchy(
        parentId: String,
        context: OperationRequest,
        resultHandler: Handler<AsyncResult<OperationResponse>>
    ) {
        nodeRepo.deleteNode(parentId)
            .map {
                OperationResponse()
                    .setStatusCode(204)
                    .setStatusMessage("NO Content")
                    .putHeader(HttpHeaders.CONTENT_TYPE.toString(), "application/json")
            }.onComplete {
                resultHandler.handle(it)
            }
    }
}