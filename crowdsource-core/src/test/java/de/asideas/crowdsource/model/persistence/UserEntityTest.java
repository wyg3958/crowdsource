package de.asideas.crowdsource.model.persistence;

import org.junit.Before;
import org.junit.Test;

public class UserEntityTest {

    private UserEntity userEntity;

    @Before
    public void setUp() {
        userEntity = new UserEntity();
        userEntity.setBudget(10);
    }

    @Test
    public void testReduceBudget() throws Exception {
        userEntity.reduceBudget(10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReduceBudget_exceed() throws Exception {
        userEntity.reduceBudget(11);
    }
}