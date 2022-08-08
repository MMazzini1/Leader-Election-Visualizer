package martinmazzini.frontend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {


    @GetMapping("/health")
    ResponseEntity<String> heatlth() {
        return ResponseEntity.ok("Healthy instance");
    }


}
