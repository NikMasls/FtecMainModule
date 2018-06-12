package com.ftec.services;

import com.ftec.configs.ApplicationConfig;
import com.ftec.controllers.RestoreController;
import com.ftec.entities.RestoreData;
import com.ftec.entities.User;
import com.ftec.exceptions.InvalidUserDataException;
import com.ftec.repositories.RestoreDataDAO;
import com.ftec.repositories.UserDAO;
import com.ftec.resources.Resources;
import com.ftec.services.interfaces.RegistrationService;
import com.ftec.services.interfaces.RestoreDataService;
import com.ftec.services.interfaces.TokenService;
import com.ftec.utils.EntityGenerator;
import com.ftec.utils.PasswordUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(value = "jenkins-tests,test", inheritProfiles = false)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ApplicationConfig.class)
@AutoConfigureMockMvc
public class RestoreServiceTest {

    @Autowired
    RestoreDataDAO restoreDataDAO;

    @Autowired
    RestoreDataService restoreDataService;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    MockMvc mvc;

    @Autowired
    UserDAO userDAO;

    @Autowired
    TokenService tokenService;

    @Test
    public void publicMethodsTest() throws Exception, InvalidUserDataException {
      User u = EntityGenerator.getNewUser();
      registrationService.registerNewUserAccount(u);
      restoreDataService.sendRestorePassUrl(u.getEmail());

      String hash = restoreDataDAO.findById(u.getId()).get().getHash();

      restoreDataService.processChangingPass(hash, "new_strong_pasS123");
      assertEquals(PasswordUtils.generateSecurePassword("new_strong_pasS123", u.getSalt()),userDAO.findById(u.getId()).get().getPassword());
    }

    @Test
    public void controllerRestorePassUrlByUsernameTest() throws Exception {
        User user = EntityGenerator.getNewUser();

        registrationService.registerNewUserAccount(user);

        mvc.perform(post(RestoreController.SEND_RESTORE_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("data", user.getUsername()))
                .andExpect(status().is(200));

        mvc.perform(post(RestoreController.SEND_RESTORE_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("data", "invalid_username"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void nullDataTest() throws Exception {
        mvc.perform(post(RestoreController.SEND_RESTORE_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void controllerRestorePassUrlByEmailTest() throws Exception {
        User user = EntityGenerator.getNewUser();
        registrationService.registerNewUserAccount(user);

        mvc.perform(post(RestoreController.SEND_RESTORE_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("data", user.getEmail()))
                .andExpect(status().is(200));

        mvc.perform(post(RestoreController.SEND_RESTORE_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("data", "invalid_email"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteOldHashTest() throws Exception {
        User user = EntityGenerator.getNewUser();
        user.setEmail(Resources.sendToStatic == null ? "wda**wda2D@gmail.com" : Resources.sendToStatic);
        registrationService.registerNewUserAccount(user);

        assertFalse(restoreDataDAO.findById(user.getId()).isPresent());

        mvc.perform(post(RestoreController.SEND_RESTORE_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("data", user.getEmail()))
                .andExpect(status().is(200));

        assertTrue(restoreDataDAO.findById(user.getId()).isPresent());
        RestoreData data1 = restoreDataDAO.findById(user.getId()).get();

        mvc.perform(post(RestoreController.SEND_RESTORE_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("data", user.getEmail()))
                .andExpect(status().is(200));
        RestoreData data2 = restoreDataDAO.findById(user.getId()).get();

        assertNotEquals(data1.getHash(),data2.getHash());


    }


    @Test
    public void changePassByHashTest() throws Exception {
        User u = EntityGenerator.getNewUser();
        registrationService.registerNewUserAccount(u);

        RestoreData test_data = new RestoreData();
        String test_hash = "awdmlawnfjakwd";
        test_data.setUserId(u.getId());
        test_data.setUrlExpiredDate(new Date(new Date().getTime() + 7200000));
        test_data.setHash(test_hash);

        restoreDataDAO.save(test_data);

        //uncomment this if test does not works Thread.sleep(1500);

                mvc.perform(post("/changePass")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("hash", test_hash)
                .param("new_pass", "newStrongPass123"))
                .andExpect(status().is(200));

    }


}
