package com.benayn.constell.services.capricorn.service.bean;

import com.benayn.constell.services.capricorn.domain.User;
import com.benayn.constell.services.capricorn.domain.UserExample;
import com.benayn.constell.services.capricorn.enums.UserStatus;
import com.benayn.constell.services.capricorn.repository.UserRepository;
import com.benayn.constell.services.capricorn.service.UserService;
import com.benayn.constell.service.enums.Gender;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceBean implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByEmail(String email) {
        UserExample example = new UserExample();
        example.createCriteria().andEmailEqualTo(email);
        return userRepository.selectOne(example);
    }

    @Override
    public User create(String email, String password, String name, Gender gender) {
        User user = new User();
        Date now = new Date();

        user.setEmail(email);
        user.setPasswd(password);
        user.setName(name);
        user.setGender(gender.getValue());
        user.setStatus(UserStatus.USING.getValue());
        user.setCreateTime(now);
        user.setLastModifyTime(now);

        userRepository.insertAll(user);
        return user;
    }

}
