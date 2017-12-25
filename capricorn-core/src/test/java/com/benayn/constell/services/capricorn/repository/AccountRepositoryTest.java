package com.benayn.constell.services.capricorn.repository;

import com.benayn.constell.services.capricorn.repository.domain.Account;
import java.util.Date;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository userRepository;

    @Test
    public void testUserRepository() {
        System.out.println(userRepository.selectById(30L));;
    }
}
