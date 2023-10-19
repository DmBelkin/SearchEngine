package searchengine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.StatisticsService;

@RestController

@RequestMapping("/api")
public class ApiController {


    @Autowired
    StatisticsService service;


    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> startIndexing() {
        return ResponseEntity.ok(service.getResponse());
    }

}


