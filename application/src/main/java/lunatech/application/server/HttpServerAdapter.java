package lunatech.application.server;

import io.quarkus.arc.DefaultBean;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Produces;
import lombok.extern.jbosslog.JBossLog;
import lunatech.application.ApplicationPort;
import lunatech.domain.auth.AuthRepositoryPort;
import lunatech.domain.auth.AuthServiceAdapter;
import lunatech.domain.auth.AuthServicePort;
import lunatech.domain.todos.TodoRepositoryPort;
import lunatech.domain.todos.TodoServiceAdapter;
import lunatech.domain.todos.TodoServicePort;
import lunatech.infra.auth.AuthRepositoryAdapter;
import lunatech.infra.todos.TodoRepositoryAdapter;

import java.util.Arrays;

@JBossLog
public class HttpServerAdapter implements ApplicationPort {

    private final static AuthRepositoryPort authRepository = new AuthRepositoryAdapter();
    private final static TodoRepositoryPort todoRepository = new TodoRepositoryAdapter();
    private final static AuthServicePort authService = new AuthServiceAdapter(authRepository);
    private final static TodoServicePort todoService = new TodoServiceAdapter(authService, todoRepository);

    @Produces
    @DefaultBean
    public AuthServicePort authPort() {
        return authService;
    }

    @Produces
    @DefaultBean
    public TodoServicePort todosPort() {
        return todoService;
    }

    @Override
    public void run(String[] args) {

        log.info(String.format("Starting HTTP Server with args %s", Arrays.toString(args)));
        Quarkus.run(args);
    }

    @Startup
    void onStartup(@Observes StartupEvent ev) {
        authService.init();
    }
}
