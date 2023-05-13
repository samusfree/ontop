package com.samus.ontop.ontoptest.adapters.persistance.mapper;

import com.samus.ontop.ontoptest.adapters.persistance.entity.AccountEntity;
import com.samus.ontop.ontoptest.application.domain.Account;

public class AccountMapper {
    public static Account toAccount(AccountEntity accountEntity) {
        return new Account(accountEntity.getId(), accountEntity.getUserId(), accountEntity.getFirstName(),
                accountEntity.getLastName(), accountEntity.getRoutingNumber(), accountEntity.getNin(),
                accountEntity.getAccountNumber());
    }

    public static AccountEntity toEntity(Account account) {
        return new AccountEntity(account.id(), account.userId(), account.firstName(), account.lastName(),
                account.routingNumber(), account.nin(), account.accountNumber());
    }
}
