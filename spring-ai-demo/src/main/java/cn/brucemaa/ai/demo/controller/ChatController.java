package cn.brucemaa.ai.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class ChatController {

    @GetMapping
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Hello, World!");
    }
}
