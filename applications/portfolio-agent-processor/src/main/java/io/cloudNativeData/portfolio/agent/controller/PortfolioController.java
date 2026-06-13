package io.cloudNativeData.portfolio.agent.controller;

import io.cloudNativeData.portfolio.agent.repository.PortfolioTradeRepository;
import io.cloudNativeData.trading.PortfolioTradeProposal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioTradeRepository repository;

    @GetMapping("trade/proposals")
    public Iterable<PortfolioTradeProposal> getTradeProposals() {
        return repository.findAllTradeProposals();

    }
}
