package com.fanniemae.innovation.monitoring.controller;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.annotation.Timed;
import org.springframework.metrics.instrument.Counter;
import org.springframework.metrics.instrument.MeterRegistry;
import org.springframework.metrics.instrument.Tag;
import org.springframework.metrics.instrument.Timer;
import org.springframework.metrics.instrument.stats.hist.CumulativeHistogram;
import static org.springframework.metrics.instrument.stats.hist.CumulativeHistogram.linear;
@RestController
@Timed("people")
public class SomeController {
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static final Logger LOG = LoggerFactory.getLogger(SomeController.class);
	  private final TaskExecutor executor;
	  private final TaskScheduler sched;
	  List<String> people = new ArrayList<>();
	  Counter steveCounter;
	  Timer findPersonTimer;
	  
	  public SomeController(MeterRegistry registry,
              @Qualifier("tpExec") TaskExecutor executor,
              @Qualifier("tpSched") TaskScheduler sched) {
		this.executor = executor;
		this.sched = sched;

		// registers a gauge to observe the size of the population
		registry.collectionSize(people,"population", Arrays.asList(Tag.of("sometag", "tag.value")));
		
		// register a counter of questionable usefulness
		steveCounter = registry.counter("find_steve");
		
		// register a timer -- though for request timing it is easier to use @Timed
		findPersonTimer = registry.timer("http_requests", "method", "GET");
		registry.summaryBuilder("hist")
        .histogram(CumulativeHistogram.buckets(linear(0, 10, 20)))
        .create();
	  }

	  @Scheduled(fixedRate = 5000)
	  @Timed(value = "sample_bob_scheduled", extraTags = {"name", "bob"})
	  public void runMe() {
	    LOG.info("Running the runMe");
	  }

    @GetMapping("/people")
    @Timed(value = "people.all", longTask = true)
    public List<String> listPeople() throws InterruptedException {
        int seconds2Sleep = SECURE_RANDOM.nextInt(500);
        System.out.println(seconds2Sleep);
        TimeUnit.MILLISECONDS.sleep(seconds2Sleep);
        return Arrays.asList("Jim", "Tom", "Tim");
    }

    @PostMapping("/people")
    @Timed(value = "people.update", longTask = true)
    public List<String> putPeople() throws InterruptedException {
        int seconds2Sleep = SECURE_RANDOM.nextInt(1000);
        System.out.println(seconds2Sleep);
        TimeUnit.MILLISECONDS.sleep(seconds2Sleep);
        return Arrays.asList("Jim", "Tom", "Tim");
    }

    @GetMapping("/asset")
    @Timed(value = "people.asset", longTask = true)
    public void test() throws Exception {
        throw new Exception("error!");
    }

    @GetMapping("/property")
    @Timed(value = "people.property", longTask = true)
    public void property(HttpServletResponse response) throws IOException {
        response.sendRedirect("/asset");
    }
    @GetMapping("/api/person")
    public String findPerson(@RequestParam String q) throws Throwable {
      for (int i = 0; i < 30; i++) {
        final int j = i;
        sched.scheduleAtFixedRate(() -> {
          LOG.info("Fixed rate: " + j);
        }, 1000);
      }

      for (int i = 0; i < 30; i++) {
        final int j = i;
        executor.execute(() -> {
          LOG.info("Execute: " + j);
        });
      }

      return findPersonTimer.record(() -> { // use the timer!
        if (q.toLowerCase().contains("steve")) {
          steveCounter.increment(); // use the counter
        }

        return people.stream().filter(p -> q.equals(p)).findAny().orElse(null);
      });
    }
}
