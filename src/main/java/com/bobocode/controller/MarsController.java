package com.bobocode.controller;

import com.bobocode.service.MarsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MarsController {
    private final MarsService marsService;

    public MarsController(final MarsService marsService) {this.marsService = marsService;}

    @GetMapping(path = "/mars/pictures/largest", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> largestPicture(@RequestParam Integer sol, @RequestParam(required = false) String camera) {
        return ResponseEntity.ok(marsService.getLargestPicture(sol, camera));
    }
}
