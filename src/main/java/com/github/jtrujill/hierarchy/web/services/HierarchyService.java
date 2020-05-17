package com.github.jtrujill.hierarchy.web.services;

import com.github.jtrujill.hierarchy.web.models.HierarchicalBody;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import io.vertx.ext.web.api.generator.WebApiServiceGen;

@WebApiServiceGen
public interface HierarchyService {
    void getHierarchy(String parentId,
                      OperationRequest context,
                      Handler<AsyncResult<OperationResponse>> resultHandler);

    void deleteHierarchy(String parentId,
                         OperationRequest context,
                         Handler<AsyncResult<OperationResponse>> resultHandler);

    void postHierarchy(HierarchicalBody body,
                       OperationRequest context,
                       Handler<AsyncResult<OperationResponse>> resultHandler);
}
