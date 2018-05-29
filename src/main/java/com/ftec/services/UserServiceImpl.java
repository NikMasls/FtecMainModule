package com.ftec.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftec.entities.User;
import com.ftec.exceptions.UserExistException;
import com.ftec.repositories.UserDAO;
import com.ftec.services.interfaces.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    @Autowired
    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public User getById(long id) {
        return userDAO.findById(id).get();
    }

    @Override
    public User registerNewUserAccount(User user) throws UserExistException {
        try {
            return userDAO.save(user);
        } catch (Exception e) {
            throw new UserExistException();
        }
    }
}
