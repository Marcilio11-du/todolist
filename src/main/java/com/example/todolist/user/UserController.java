package com.example.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel userModel) {
        UserModel user = userRepository.findByUserName(userModel.getUserName()); //Procura por um utilizador que já obtenha certo username no banco de dados

        if(user != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username já existente!!");

        String hashedPassword = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
        userModel.setPassword(hashedPassword);
        this.userRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }
}
