package com.benayn.constell.services.capricorn.repository;

import com.benayn.constell.service.server.repository.Repository;
import com.benayn.constell.services.capricorn.repository.domain.Account;
import com.benayn.constell.services.capricorn.repository.domain.AccountExample;
import java.util.List;

public interface AccountRepository extends Repository<Account, AccountExample> {

    Account getByEmail(String email);

    List<Long> getRoleOwnerIdsBy(Long roleId, List<Long> accountIds, Integer pageNo, Integer pageSize);
}
