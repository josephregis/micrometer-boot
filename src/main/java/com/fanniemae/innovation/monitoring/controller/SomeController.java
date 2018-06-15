package com.fanniemae.innovation.monitoring.controller;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;

@RestController
@Timed("people")
public class SomeController {
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
  

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

}
