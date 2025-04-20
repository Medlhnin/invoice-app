package com.example.demo.FACTORIES;

import com.example.demo.INTERFACES.InvoiceTriggerStrategy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class InvoiceTriggerFactory {
    private final Map<String, InvoiceTriggerStrategy> strategies;

    public InvoiceTriggerFactory(List<InvoiceTriggerStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        s -> s.getClass().getAnnotation(Service.class).value(),
                        Function.identity()
                ));
    }

    public InvoiceTriggerStrategy getStrategy(String mode) {
        return strategies.getOrDefault(mode, strategies.get("manual")); // fallback
    }
}
