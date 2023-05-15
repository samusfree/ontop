package com.samus.ontop.ontoptest.adapters.external;

import com.samus.ontop.ontoptest.application.domain.integration.WalletRequest;
import com.samus.ontop.ontoptest.application.domain.integration.WalletResponse;
import com.samus.ontop.ontoptest.application.ports.out.WalletPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WalletExternalAdapter implements WalletPort {
    @Autowired
    private WebClient webClient;

    @Override
    public Mono<WalletResponse> operation(WalletRequest request) {
        return webClient.post().uri("/wallets/transactions").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request).retrieve().bodyToMono(WalletResponse.class);
    }
}
