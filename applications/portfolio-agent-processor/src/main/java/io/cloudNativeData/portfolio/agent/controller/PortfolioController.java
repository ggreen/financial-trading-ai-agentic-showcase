package io.cloudNativeData.portfolio.agent.controller;

import io.cloudNativeData.portfolio.agent.service.ProposeTradeService;
import io.cloudNativeData.trading.PortfolioTradeProposal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("portfolio/trade/")
@RequiredArgsConstructor
public class PortfolioController {

    private final ProposeTradeService service;

    @GetMapping("proposals")
    public Iterable<PortfolioTradeProposal> getTradeActiveProposals() {
        return service.findActiveTradeProposals();

    }

    @PutMapping
    @RequestMapping("accept/{id}")
    public void acceptTradeProposal(@PathVariable String id) {
        service.acceptTradeProposalById(id);
    }

    @PutMapping
    @RequestMapping("reject/{id}")
    public void rejectTradeProposal(@PathVariable String id) {

        service.rejectTradeProposalById(id);

    }
}
