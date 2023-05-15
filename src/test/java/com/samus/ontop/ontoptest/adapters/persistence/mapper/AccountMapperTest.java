package com.samus.ontop.ontoptest.adapters.persistence.mapper;

import com.samus.ontop.ontoptest.adapters.persistence.entity.AccountEntity;
import com.samus.ontop.ontoptest.application.domain.Account;
import com.samus.ontop.ontoptest.common.CommonObjects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountMapperTest {
    @Test
    public void testToEntity() {
        Account account = CommonObjects.getAccount();

        AccountEntity accountEntity = AccountMapper.toEntity(account);

        Assertions.assertEquals(account.userId(), accountEntity.getUserId());
    }
}
