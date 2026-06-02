package com.paypal.user_service.controller;

import com.paypal.user_service.entity.User;
import com.paypal.user_service.service.UserService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users/")
public class UserController {

    private UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @GetMapping()
    public String index(){
        return "Index page";
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{id}")    
    public ResponseEntity<User> getUser(@PathVariable int id){
        System.out.println(id);
        System.out.println(userService.getUserById(id));
        return  userService.getUserById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers(){
        return  ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/getName/{userId}")
    public String getEmail(@PathVariable int userId){
        Optional<User> user=userService.getUserById(userId);
        return user.get().getName();
    }
}
