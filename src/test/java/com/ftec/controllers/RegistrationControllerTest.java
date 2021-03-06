package com.ftec.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftec.configs.ApplicationConfig;
import com.ftec.repositories.UserDAO;
import com.ftec.resources.Resources;
import com.ftec.services.Implementations.UserServiceImpl;
import com.ftec.utils.EntityGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.Assert.*;

@ActiveProfiles(value = "jenkins-tests,test", inheritProfiles = false)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ApplicationConfig.class)
@AutoConfigureMockMvc
public class RegistrationControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserDAO userDAO;

    @Autowired
    UserServiceImpl userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void createValidUser() throws Exception {
        RegistrationController.UserRegistration userRegistration = EntityGenerator.getNewRegisrtUser();

        mvc.perform(MockMvcRequestBuilders.post("/registration").
                content(objectMapper.writeValueAsString(userRegistration)).contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON))
                .andDo(Resources.doPrintStatic ? print() : (ResultHandler) result -> {}).andExpect(status().is(200));

        assertTrue(userDAO.findByUsername(userRegistration.getUsername()).isPresent());
    }

    @Test
    public void registerTwoValidUsers() throws Exception {

        RegistrationController.UserRegistration userRegistration1 = EntityGenerator.getNewRegisrtUser();
        RegistrationController.UserRegistration userRegistration2 = EntityGenerator.getNewRegisrtUser();

        assertFalse(userDAO.findByUsername(userRegistration1.getUsername()).isPresent());
        assertFalse(userDAO.findByUsername(userRegistration2.getUsername()).isPresent());

        mvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/registration").
                content(objectMapper.writeValueAsString(userRegistration1)).contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));

        mvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/registration").
                content(objectMapper.writeValueAsString(userRegistration2)).contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));

        assertTrue(userDAO.findByUsername(userRegistration1.getUsername()).isPresent());
        assertTrue(userDAO.findByUsername(userRegistration2.getUsername()).isPresent());

    }

    @Test
    public void trySaveWithNullUsername() throws Exception {
        RegistrationController.UserRegistration userRegistration = new RegistrationController.UserRegistration();
        userRegistration.setUsername(null);
        userRegistration.setPassword("NullUser_Pass_228");
        userRegistration.setEmail("NullUsername_@gmail.com");

        //should be status 400
        mvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/registration").
                content(objectMapper.writeValueAsString(userRegistration)).contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON))
                .andDo(Resources.doPrintStatic ? print() : (ResultHandler) result -> {
                }).andExpect(status().is(400));

        assertFalse(userDAO.findByEmail(userRegistration.getEmail()).isPresent());
    }

    @Test
    public void trySaveWithShortUsername() throws Exception {
        RegistrationController.UserRegistration userRegistration = new RegistrationController.UserRegistration();
        userRegistration.setUsername("Lol");
        userRegistration.setPassword("ShortUser_Pass_228");
        userRegistration.setEmail("ShortUsername_@gmail.com");

        //should be status 400
        mvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/registration").
                content(objectMapper.writeValueAsString(userRegistration)).contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON))
                .andDo(Resources.doPrintStatic ? print() : (ResultHandler) result -> {
                }).andExpect(status().is(400));

        assertFalse(userDAO.findByUsername(userRegistration.getUsername()).isPresent());
    }

    @Test
    public void trySaveDuplicateUsername() throws Exception {
        RegistrationController.UserRegistration userRegistration = EntityGenerator.getNewRegisrtUser();
        String userName = userRegistration.getUsername();

        mvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/registration").
                content(objectMapper.writeValueAsString(userRegistration)).contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON))
                .andDo(Resources.doPrintStatic ? print() : (ResultHandler) result -> {
                }).andExpect(status().is(200));

        //should be status 400
        mvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/registration").
                content(objectMapper.writeValueAsString(userRegistration)).contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON))
                .andDo(Resources.doPrintStatic ? print() : (ResultHandler) result -> {
                }).andExpect(status().is(400));

        assertTrue(userService.isDuplicateUserName(userName));
    }

    @Test
    public void trySaveWithNullPassword() throws Exception {
        RegistrationController.UserRegistration userRegistration = new RegistrationController.UserRegistration();
        userRegistration.setUsername("NullPasswordUser");
        userRegistration.setPassword(null);
        userRegistration.setEmail("NullPassword_@gmail.com");

        //should be status 400
        mvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/registration").
                content(objectMapper.writeValueAsString(userRegistration)).contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON))
                .andDo(Resources.doPrintStatic ? print() : (ResultHandler) result -> {
                }).andExpect(status().is(400));

        assertFalse(userDAO.findByUsername(userRegistration.getUsername()).isPresent());
    }

    @Test
    public void trySaveWithShortPassword() throws Exception {
        RegistrationController.UserRegistration userRegistration = new RegistrationController.UserRegistration();
        userRegistration.setUsername("ShortPasswordUser");
        userRegistration.setPassword("lol");
        userRegistration.setEmail("ShortPassword_@gmail.com");

        //should be status 400
        mvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/registration").
                content(objectMapper.writeValueAsString(userRegistration)).contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON))
                .andDo(Resources.doPrintStatic ? print() : (ResultHandler) result -> {
                }).andExpect(status().is(400));

        assertFalse(userDAO.findByUsername(userRegistration.getUsername()).isPresent());
    }

    @Test
    public void trySaveWithWrongPasswordPattern() throws Exception {
        RegistrationController.UserRegistration userRegistration = new RegistrationController.UserRegistration();
        userRegistration.setUsername("WrongPassPatternUser");
        userRegistration.setPassword("password");
        userRegistration.setEmail("WrongPatternPass_@gmail.com");

        //should be status 400
        mvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/registration").
                content(objectMapper.writeValueAsString(userRegistration)).contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON))
                .andDo(Resources.doPrintStatic ? print() : (ResultHandler) result -> {
                }).andExpect(status().is(400));

        assertFalse(userDAO.findByUsername(userRegistration.getUsername()).isPresent());
    }

    @Test
    public void trySaveWithNullEmail() throws Exception {
        RegistrationController.UserRegistration userRegistration = new RegistrationController.UserRegistration();
        userRegistration.setUsername("NullEmailUser");
        userRegistration.setPassword("Strongpass1");
        userRegistration.setEmail(null);

        //should be status 400
        mvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/registration").
                content(objectMapper.writeValueAsString(userRegistration)).contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON))
                .andDo(Resources.doPrintStatic ? print() : (ResultHandler) result -> {
                }).andExpect(status().is(400));

        assertFalse(userDAO.findByUsername(userRegistration.getUsername()).isPresent());
    }

    @Test
    public void trySaveWithLongEmail() throws Exception {
        RegistrationController.UserRegistration userRegistration = new RegistrationController.UserRegistration();
        userRegistration.setUsername("LongEmailUser");
        userRegistration.setPassword("Strongpass1");
        userRegistration.setEmail("VeryVeryVeryVeryVeryVeryVeryLong@gmail.com");

        //should be status 400
        mvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/registration").
                content(objectMapper.writeValueAsString(userRegistration)).contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON))
                .andDo(Resources.doPrintStatic ? print() : (ResultHandler) result -> {
                }).andExpect(status().is(400));

        assertFalse(userDAO.findByUsername(userRegistration.getUsername()).isPresent());
    }

    @Test
    public void trySaveDuplicateEmail() throws Exception {
        String email = "trySaveDuplicate@mail.com";

        RegistrationController.UserRegistration userRegistration1 = new RegistrationController.UserRegistration();
        userRegistration1.setUsername("OriginalEmailUser");
        userRegistration1.setPassword("Strongpass1");
        userRegistration1.setEmail(email);

        RegistrationController.UserRegistration userRegistration2 = new RegistrationController.UserRegistration();
        userRegistration1.setUsername("DuplicateEmailUser");
        userRegistration1.setPassword("Strongpass1");
        userRegistration1.setEmail(email);

        mvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/registration").
                content(objectMapper.writeValueAsString(userRegistration1)).contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON))
                .andDo(Resources.doPrintStatic ? print() : (ResultHandler) result -> {
                }).andExpect(status().is(200));

        //should be status 400
        mvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/registration").
                content(objectMapper.writeValueAsString(userRegistration2)).contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON))
                .andDo(Resources.doPrintStatic ? print() : (ResultHandler) result -> {
                }).andExpect(status().is(400));

        assertTrue(userDAO.findByUsername(userRegistration1.getUsername()).isPresent());
        assertFalse(userDAO.findByUsername(userRegistration2.getUsername()).isPresent());
    }

}
