package lunatech.application;

import lombok.extern.jbosslog.JBossLog;
import lunatech.application.server.HttpServerAdapter;

import java.util.List;

@JBossLog
public class Main {
    private static final List<ApplicationPort> applications = List.of(new HttpServerAdapter());

    public static void main(String[] args) {
        log.info("Launching hexa-archi-exercise application!");

        applications.forEach(app -> app.run(args));
    }

}
