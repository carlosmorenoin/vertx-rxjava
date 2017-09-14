package org.carlosmorenoin.micro.vertxrxjava;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.carlosmorenoin.micro.vertxrxjava.task.TaskService;

import io.reactivex.Maybe;
import io.reactivex.internal.schedulers.RxThreadFactory;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Created by carlos on 14/09/2017.
 */
public class RestServer extends AbstractVerticle {

	private static final String APPLICATION_JSON = "application/json; charset=utf-8";

	private ExecutorService executorService;

	private TaskService taskService = new TaskService();

	public static void main(String[] args) {
		RestServer myVerticle = new RestServer();
		Vertx.vertx().deployVerticle(myVerticle);
	}

	@Override
	public void start() {
		Router router = Router.router(vertx);

		router.route().handler(BodyHandler.create());
		router.get("/task/:number").handler(this::handleTask);

		vertx.createHttpServer().requestHandler(router::accept).listen(8081);

		executorService = Executors.newFixedThreadPool(1,new RxThreadFactory("ServerComponent",5));
	}

	private void handleTask(RoutingContext routingContext) {
		Maybe.just(routingContext)
				.map(r -> r.request().getParam("number"))
				.map(Integer::valueOf)
				.observeOn(Schedulers.from(executorService))
				.flatMap(number -> taskService.executeComplexTask(number))
				.filter(Objects::nonNull)
				.subscribe(result -> {
							if (result == null) {
								routingContext.response().setStatusCode(400).end();
							} else {
								routingContext.response().end(result.toString());
							}
						},
						error -> routingContext.response().setStatusCode(500).end());
	}
}
