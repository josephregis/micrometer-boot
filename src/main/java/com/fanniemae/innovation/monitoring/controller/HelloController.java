package com.fanniemae.innovation.monitoring.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("hello")
	public List<String> listPeople() throws InterruptedException {
        return Arrays.asList("Jim", "Tom", "Tim");
    }
}
