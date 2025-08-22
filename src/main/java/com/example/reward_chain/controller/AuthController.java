package com.example.reward_chain.controller;

import com.example.reward_chain.data.UserRepo;
import com.example.reward_chain.data.exceptions.*;
import com.example.reward_chain.model.User;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepo userRepo;
    public AuthController(UserRepo userRepo) { this.userRepo = userRepo; }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body)
            throws InternalErrorException, RecordNotFoundException {

        String email = body.get("email");
        String password = body.get("password");

        User u = userRepo.getUserByEmail(email); // throws if not found
        if (!u.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }

        // Return minimal “session” info (add JWT later)
        return ResponseEntity.ok(Map.of(
                "userId", u.getUserId(),
                "email", u.getEmail(),
                "firstName", u.getFirstName(),
                "lastName", u.getLastName(),
                "token", "DEV_TOKEN_" + u.getUserId()
        ));
    }
}
