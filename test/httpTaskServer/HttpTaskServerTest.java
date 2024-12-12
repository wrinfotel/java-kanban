package httpTaskServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.handlers.adapters.DurationAdapter;
import http.handlers.adapters.LocalDateTimeAdapter;
import http.server.HttpTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import taskmanager.InMemoryTaskManager;
import taskmanager.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServerTest {

    protected TaskManager taskManager;
    protected HttpTaskServer httpTaskServer;
    protected Gson gson;

    @BeforeEach
    void beforeEach() throws IOException {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = gsonBuilder.create();
    }

    @AfterEach
    void afterEach() {
        httpTaskServer.stop();
    }
}
