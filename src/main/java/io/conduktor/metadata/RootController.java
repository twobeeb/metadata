package io.conduktor.metadata;

import io.conduktor.metadata.services.TopicStateService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;
import org.jobrunr.scheduling.JobScheduler;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;

@Controller("/")
public class RootController {

    @Inject
    private JobScheduler jobScheduler;
    @Inject
    public TopicStateService topicStateService;

    @Get
    public String test(){
        jobScheduler.schedule(Instant.now(),
                () -> System.out.println("Ah !!!!"));
        return "scheduled";
    }
    @Get("/recuring/{name}")
    public Duration other(String name) throws ExecutionException, InterruptedException {
        Instant t0 = Instant.now();
        Instant t1 = Instant.now();
        var totalTime = Duration.between(t1, t0);
        jobScheduler.scheduleRecurrently("job-"+name,
                Duration.of(10, ChronoUnit.SECONDS),
                () -> topicStateService.doWork( name));
        return totalTime;
    }
}
