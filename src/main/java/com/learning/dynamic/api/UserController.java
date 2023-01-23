package com.learning.dynamic.api;

import com.learning.dynamic.dto.UserDto;
import com.learning.dynamic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() throws SQLException {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
