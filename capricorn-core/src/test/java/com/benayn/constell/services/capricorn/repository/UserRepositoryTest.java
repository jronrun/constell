package com.benayn.constell.services.capricorn.repository;

import com.benayn.constell.services.capricorn.domain.User;
import java.util.Date;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUserRepository() {
        System.out.println(userRepository.selectById(1l).toString());

        Random r = new Random();
        int num = r.nextInt(10000);

        User user = new User();
        user.setCreateTime(new Date());
        user.setLastModifyTime(new Date());
        user.setEmail(num + "@test.com");
        user.setName(num + "name");
        user.setPasswd(String.valueOf(num));
        user.setGender((short)1);
        user.setStatus((short)2);

        userRepository.insert(user);
        System.out.println(user.getId() + " after insert");

    }
}
