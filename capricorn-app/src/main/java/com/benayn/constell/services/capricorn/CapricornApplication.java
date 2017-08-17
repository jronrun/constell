package com.benayn.constell.services.capricorn;

import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.service.server.service.BenaynMicroService;
import com.benayn.constell.services.capricorn.repository.domain.Account;
import com.benayn.constell.services.capricorn.repository.domain.AccountExample;
import com.benayn.constell.services.capricorn.repository.AccountRepository;
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
        AccountRepository ur = ctx.getBean(AccountRepository.class);

        AccountExample ex = new AccountExample();
        ex.setOrderByClause("gender");
        Page<Account> page = ur.selectPageBy(ex, 1, 1);
        log.info(page.getResource().size() + " " + page.toString());

        log.info(ur.selectById(page.getResource().get(0).getId()).toString());


    }

}
