package searchengine.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.services.IndexationService;

@RestController
@RequestMapping("/api")
public class IndexationController {

    @Autowired
    IndexationService service;


    @GetMapping("/{buttonData}")
    public ResponseEntity start(@PathVariable String buttonData) {
        if (buttonData.equals("startIndexing")) {
            System.out.println("on");
            service.drop();
            service.listSites();
        } else if (buttonData.equals("stopIndexing")) {
            System.out.println("off");
        }
        return new ResponseEntity(HttpStatus.OK);
    }

}


