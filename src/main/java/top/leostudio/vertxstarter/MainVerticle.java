package top.leostudio.vertxstarter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.codec.BodyCodec;

public class MainVerticle extends AbstractVerticle
{

  private HttpRequest<JsonObject> requet;

  @Override public void start () throws Exception
  {
    requet = WebClient.create(vertx).get(443, "icanhazdadjoke.com", "/").ssl(true).putHeader("Accept",
      "Application/json").as(BodyCodec.jsonObject()).expect(ResponsePredicate.SC_OK);

    vertx.setPeriodic(3000, id -> fetchJoke());
  }

  private void fetchJoke ()
  {
    requet.send(asyncResult -> {
      if (asyncResult.succeeded()) {
        System.out.println(asyncResult.result().body().getString("joke"));
        System.out.println("😂");
        System.out.println();
      }
    });
  }

  public static void main (String[] args)
  {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }

}
