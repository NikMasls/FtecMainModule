package com.ftec.configs.enums;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.ftec.entities.User;

@RunWith(SpringRunner.class)
public class EnumTest {
    @Test
    public void setNextStepTest() {
        User u = new User();
        u.setCurrentStep(TutorialSteps.FIRST); //should be by default
        TutorialSteps.setNextStep(u);

        assertTrue(TutorialSteps.SECOND.equals(u.getCurrentStep()));
    }

    @Test
    public void getNextStepTest() {
        assertTrue(TutorialSteps.getNextStep(TutorialSteps.FIRST).equals(TutorialSteps.SECOND));
    }

    @Test
    public void returnsNullTest() {
        assertTrue(TutorialSteps.getNextStep(TutorialSteps.THIRD) == null);
    }
}