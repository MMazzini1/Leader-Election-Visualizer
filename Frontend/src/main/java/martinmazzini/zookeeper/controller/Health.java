package martinmazzini.zookeeper.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Health {


    @GetMapping("/health")
    ResponseEntity<String> heatlth() {
        return ResponseEntity.ok("Healthy1 updated  eee");
    }


}
