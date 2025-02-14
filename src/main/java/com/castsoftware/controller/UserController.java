package com.castsoftware.controller;

import com.castsoftware.entity.User;
import com.castsoftware.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000") // For React frontend
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        User savedUser = userService.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.findById(id)
                .map(existingUser -> {
                    user.setId(id);
                    User updatedUser = userService.save(user);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.findById(id).isPresent()) {
            userService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/sample-users")
    public ResponseEntity<String> createSampleUsers() {
        userService.createSampleUsers();
        return ResponseEntity.ok("Sample users created successfully");
    }

    @GetMapping("/display")
    public ResponseEntity<List<User>> retrieveAndDisplayUsers() {
        List<User> users = userService.retrieveAndDisplayUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/display-one-by-one")
    public ResponseEntity<List<User>> retrieveUsersOneByOne() {
        List<User> users = userService.retrieveUsersOneByOne();
        return ResponseEntity.ok(users);
    }
}