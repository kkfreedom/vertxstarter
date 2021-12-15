/*
    Responsible: Leo Feng
*/
package top.leostudio.vertxstarter;

import graphql.GraphQL;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.graphql.GraphQLHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;
import static java.util.stream.Collectors.toMap;

/**
 * Created by Leo on 2021/12/15.
 */
public class GraphQLVerticle extends AbstractVerticle
{
    private Map<String, Task> tasks;

    private Map<String, Task> initData ()
    {
        Stream<Task> stream = Stream.<Task>builder().add(new Task("Learn GraphQL")).add(new Task(
            "Build awesome GraphQL server")).add(new Task("Profit")).build();

        return stream.collect(toMap(task -> task.id, task -> task));
    }

    private GraphQL setupGraphQL ()
    {
        String schema = vertx.fileSystem().readFileBlocking("tasks.graphqls").toString();
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);
        RuntimeWiring runtimeWiring = newRuntimeWiring().type("Query",
            builder -> builder.dataFetcher("allTasks", this::allTasks)).type("Mutation",
            builder -> builder.dataFetcher("complete", this::complete)).build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry,
            runtimeWiring);

        return GraphQL.newGraphQL(graphQLSchema).build();
    }

    private List<Task> allTasks (DataFetchingEnvironment env)
    {
        boolean uncompletedOnly = env.getArgument("uncompletedOnly");
        return tasks.values().stream().filter(task -> !uncompletedOnly || task.completed).collect(
            Collectors.toList());
    }

    private boolean complete (DataFetchingEnvironment env)
    {
        String id = env.getArgument("id");
        Task task = tasks.get(id);
        if (task == null)
        {
            return false;
        }
        task.completed = true;
        return true;
    }

    @Override public void start ()
    {
//        super.start();
        tasks = initData();
        GraphQL graphQL = setupGraphQL();
        GraphQLHandler graphQLHandler = GraphQLHandler.create(graphQL);
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route("/graphql").handler(graphQLHandler);

        vertx.createHttpServer()
            .requestHandler(router).listen(8889);

    }

    public static void main (String[] args)
    {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new GraphQLVerticle());
    }
}
