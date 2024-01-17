package dev.vality.exporter.businessmetrics.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CurrencyRepositoryTest {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Test
    public void getWithdrawalsMetrics() {
        var withdrawalsMetrics = currencyRepository.findAllByCurrentIsTrue();
        System.out.println(withdrawalsMetrics);
        System.out.println(withdrawalsMetrics.size());
    }
}