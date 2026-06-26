package io.cloudNativeData.portfolio.agent.controller;

import io.cloudNativeData.portfolio.agent.service.ProposeTradeService;
import io.cloudNativeData.trading.PortfolioTradeProposal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final ProposeTradeService service;

    @GetMapping("trade/proposals")
    public Iterable<PortfolioTradeProposal> getTradeProposals() {
        return service.findAllTradeProposals();

    }

    @PutMapping
    @RequestMapping("trace/accept")
    public void acceptTradeProposal(@RequestParam String id) {
        service.acceptTradeProposalById(id);
    }

    @PutMapping
    @RequestMapping("trace/reject")
    public void rejectTradeProposal(@RequestParam String id) {

        service.rejectTradeProposalById(id);

    }
}
