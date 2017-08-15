package com.benayn.constell.services.capricorn.repository.bean;

import com.benayn.constell.services.capricorn.domain.User;
import com.benayn.constell.services.capricorn.domain.UserExample;
import com.benayn.constell.services.capricorn.mapper.UserMapper;
import com.benayn.constell.services.capricorn.repository.UserRepository;
import com.benayn.constell.service.server.repository.bean.GenericRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl extends GenericRepository<User, UserExample, UserMapper> implements UserRepository {

}
