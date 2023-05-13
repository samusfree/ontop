package com.samus.ontop.ontoptest.application.ports.out;

import com.samus.ontop.ontoptest.application.domain.integration.WalletRequest;
import com.samus.ontop.ontoptest.application.domain.integration.WalletResponse;
import reactor.core.publisher.Mono;

public interface WalletPort {
    Mono<WalletResponse> operation(WalletRequest request);
}
