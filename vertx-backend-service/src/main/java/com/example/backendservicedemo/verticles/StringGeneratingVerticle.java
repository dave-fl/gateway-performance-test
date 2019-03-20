package com.example.backendservicedemo.verticles;

import com.example.backendservicedemo.services.StringGeneratorService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class StringGeneratingVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(StringGeneratingVerticle.class);

    @Autowired
    StringGeneratorService stringGeneratorService;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);
        Route route = router.route("/demo");
        HttpServer server = vertx.createHttpServer();
        route.handler(routingContext -> {
            HttpServerRequest request = routingContext.request();
            String length = request.getParam("length");
            String delay = request.getParam("delay");
            if (length == null) {
                request.response().setStatusCode(400).end("Missing parameter length.");
            } else {
                if (delay == null) {
                    request.response().end(stringGeneratorService.getGeneratedString(Integer.parseInt(length)));
                } else {
                    vertx.setTimer(Long.parseLong(delay), id -> {
                        HttpServerResponse response = request.response();
                        if (!response.closed()) {
                            response.end(stringGeneratorService.getGeneratedString(Integer.parseInt(length)));
                        }
                    });
                }
            }
        });

        server.requestHandler(router).listen(8080, ar -> {
            if (ar.succeeded()) {
                LOG.info("StringGeneratingVerticle started: @" + this.hashCode());
                startFuture.complete();
            } else {
                startFuture.fail(ar.cause());
            }
        });
    }
}
