package com.benayn.constell.services.capricorn;

import com.benayn.constell.service.server.repository.ExamplePageFeature;
import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.service.server.service.BenaynMicroService;
import com.benayn.constell.services.capricorn.domain.User;
import com.benayn.constell.services.capricorn.domain.UserExample;
import com.benayn.constell.services.capricorn.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@Slf4j
public class CapricornApplication {

	public static void main(String[] args) {
//        BenaynMicroService app = new BenaynMicroService();
//        app.start(CapricornApplication.class, args);
//        ApplicationContext ctx = app.start(CapricornApplication.class, args);

        BenaynMicroService app = new BenaynMicroService();
        ApplicationContext ctx = app.start(CapricornApplication.class, args);
        log.trace(" ----- trace ----");
        log.debug(" ----- debug ----");
        log.info(" ----- info ----");
        log.warn(" ----- warn ----");
        log.error(" ----- error ----");
        UserRepository ur = ctx.getBean(UserRepository.class);

        log.info(ur.selectById(1l).toString());

        UserExample ex = new UserExample();
        ex.setOrderByClause("gender");
        Page<User> page = ur.selectPageBy(ex, 1, 1);
        log.info(page.getResource().size() + " " + page.toString());

        ExamplePageFeature extest = (ExamplePageFeature) ctx.getBean("extest");
        log.info(extest.toString());

    }

}
