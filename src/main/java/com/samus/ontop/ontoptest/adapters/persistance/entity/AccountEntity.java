package com.samus.ontop.ontoptest.adapters.persistance.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("account")
public class AccountEntity {
    @Id
    private Integer id;
    private String userId;
    private String firstName;
    private String lastName;
    private String routingNumber;
    private String nin;
    private String accountNumber;
}
