package com.github.jtrujill.hierarchy.web.services

import com.github.jtrujill.hierarchy.app.UserWorshiper
import com.github.jtrujill.hierarchy.web.models.HierarchicalBody
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.ext.web.api.OperationRequest
import io.vertx.ext.web.api.OperationResponse

class HierarchyServiceImpl(private val userWorship: UserWorshiper) :
    HierarchyService {
    override fun getHierarchy(
        parentId: String,
        context: OperationRequest,
        resultHandler: Handler<AsyncResult<OperationResponse>>
    ) {
        TODO("Not yet implemented")
    }

    override fun postHierarchy(
        body: HierarchicalBody,
        context: OperationRequest,
        resultHandler: Handler<AsyncResult<OperationResponse>>
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteHierarchy(
        parentId: String,
        context: OperationRequest,
        resultHandler: Handler<AsyncResult<OperationResponse>>
    ) {
        TODO("Not yet implemented")
    }
}