package com.ayush.ravan.controller;

import com.ayush.ravan.exceptions.ResourceNotFoundException;
import com.ayush.ravan.services.PritamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PritamController {

    @Autowired
    private PritamService pritamService;
    @Value("${source.file.name:pritamService.txt}")
    private String sourceFileName;
    @GetMapping("/pritam/{id}")
    public ResponseEntity<String> getCommentById(@PathVariable(value = "id") Long commentId) throws ResourceNotFoundException {
        System.out.println("******************** : "+sourceFileName);
        return new ResponseEntity<>(pritamService.getCommentById(commentId),HttpStatus.OK);
    }

    @DeleteMapping("/pritam/{id}")
    public ResponseEntity<?> deleteCommentById(@PathVariable(value = "id") Long commentId) throws ResourceNotFoundException {
        pritamService.deleteCommentById(commentId);
        return ResponseEntity.ok().build();
    }
}
