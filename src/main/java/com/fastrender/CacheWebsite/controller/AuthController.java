package com.fastrender.CacheWebsite.controller;


import com.fastrender.CacheWebsite.Services.AuthentaicationService;
import com.fastrender.CacheWebsite.model.AuthenticationResponse;
import com.fastrender.CacheWebsite.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthentaicationService authentaicationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody User user){
        return ResponseEntity.ok(authentaicationService.register(user));
    }


    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody User user){
        return ResponseEntity.ok(authentaicationService.authenticate(user));
    }

}
