package com.learning.dynamic.service;

import com.learning.dynamic.dto.UserDto;
import com.learning.dynamic.entity.User;
import com.learning.dynamic.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service("userService")
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public List<UserDto> getAllUsers() throws SQLException {
        List<UserDto> userDtos = new ArrayList<>();
        List<User> users = userRepo.getUsers();
        users.stream().forEach(user -> {
            UserDto userDto = new UserDto();
            userDto.setFirstName(user.getFirstName());
            userDto.setLastName(user.getLastName());
            userDto.setEmail(user.getEmail());
            userDto.setCompany(user.getCompany());
            userDtos.add(userDto);
        });
        return userDtos;
    }
}
