package com.samus.ontop.ontoptest.application.domain;

public record Account(Integer id, String userId, String firstName, String lastName, String routingNumber, String nin,
                      String accountNumber) {
}
