package com.paypal.user_service.controller;

import com.paypal.user_service.dto.JwtResponse;
import com.paypal.user_service.dto.LoginRequest;
import com.paypal.user_service.dto.SignupRequest;
import com.paypal.user_service.entity.User;
import com.paypal.user_service.repository.UserRepository;
import com.paypal.user_service.utils.JWTutils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    private final JWTutils jwtUtils;



    public AuthController(UserRepository userRepository,JWTutils jwtUtils){
        this.jwtUtils=jwtUtils;
        this.userRepository=userRepository;
    }
    @PostMapping("/signup")
    public ResponseEntity<?> signupRequestHandler(@RequestBody SignupRequest request){
        Optional<User> existsUser=userRepository.findByEmail(request.getEmail());
        if(existsUser.isPresent()){
            return  ResponseEntity.status(400).body("User Already Exists");
        }
        User user=new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(jwtUtils.passwordEncoder().encode(request.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);

        return ResponseEntity.status(201).body("User Created Successfully");
    }
    @PostMapping("/login")
    public  ResponseEntity<?> loginRequestHandler(@RequestBody LoginRequest request){

        Optional<User> optionalUser=userRepository.findByEmail(request.getEmail());

        if(optionalUser.isEmpty()){
            return  ResponseEntity.status(401).body("User Not found");
        }

        User user=optionalUser.get();

        if(!jwtUtils.passwordEncoder().matches(request.getPassword(),user.getPassword())){
            return  ResponseEntity.status(401).body("Invalid Credential");
        }
        Map<String,Object> claims=new HashMap<>();
        claims.put("role",user.getRole());
        claims.put("userId",user.getId());

        String token=jwtUtils.generateToken(claims,user.getEmail());

        return  ResponseEntity.ok(new JwtResponse(token));
    }

}
