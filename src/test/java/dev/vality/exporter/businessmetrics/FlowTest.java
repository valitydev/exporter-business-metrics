package dev.vality.exporter.businessmetrics;

import dev.vality.exporter.businessmetrics.entity.PaymentsMetricDto;
import dev.vality.exporter.businessmetrics.entity.WithdrawalsMetricDto;
import dev.vality.exporter.businessmetrics.repository.PaymentRepository;
import dev.vality.exporter.businessmetrics.repository.WithdrawalRepository;
import dev.vality.exporter.businessmetrics.service.SchedulerRegisterMetricsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMetrics
@AutoConfigureMockMvc
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"exporter-business-metrics.cron=-", //disables scheduled execution
                "management.server.port="})
@SuppressWarnings("LineLength")
public class FlowTest {

    @MockBean
    private PaymentRepository paymentRepository;

    @MockBean
    private WithdrawalRepository withdrawalRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SchedulerRegisterMetricsService schedulerRegisterMetricsService;

    private AutoCloseable mocks;

    private Object[] preparedMocks;

    @BeforeEach
    public void init() {
        mocks = MockitoAnnotations.openMocks(this);
        preparedMocks = new Object[]{paymentRepository, withdrawalRepository};
    }

    @AfterEach
    public void clean() throws Exception {
        verifyNoMoreInteractions(preparedMocks);
        mocks.close();
    }

    @Test
    public void metricsHaveBeenRegisteredTest() throws Exception {
        var paymentsMetrics = getPaymentsMetricDtos();
        var withdrawalsMetrics = getWithdrawalsMetricDto();
        when(paymentRepository.getPaymentsMetricsByInterval(any())).thenReturn(paymentsMetrics);
        when(withdrawalRepository.getWithdrawalsMetricsByInterval(any())).thenReturn(withdrawalsMetrics);
        schedulerRegisterMetricsService.registerMetricsTask();
        verify(paymentRepository, times(1)).getPaymentsMetricsByInterval(any());
        verify(withdrawalRepository, times(1)).getWithdrawalsMetricsByInterval(any());
        var mvcResult = mockMvc.perform(get("/actuator/prometheus"))
                .andReturn();
        var prometheusResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var actualMetrics = Arrays.stream(prometheusResponse.split("\n"))
                .filter(row -> row.startsWith("ebm_")).toList();
        Assertions.assertEquals(paymentsMetrics.size() + withdrawalsMetrics.size(), actualMetrics.size());
    }

    private List<PaymentsMetricDto> getPaymentsMetricDtos() {
        return List.of(
                new PaymentsMetricDto("1", "mts", "1", "mts rub", "1", "gucci", "rub", "rus", "kaspi jsp", "visa", "pending", "1"),
                new PaymentsMetricDto("2", "xxx", "2", "xxx usd", "1", "kaspi", "kzt", "kz", "kaspi jsp", "visa", "captured", "1"),
                new PaymentsMetricDto("3", "reppay", "3", "reppay kzt", "1", "kaspi", "usd", "usa", "undefined", "visa", "failed", "1"));
    }

    private List<WithdrawalsMetricDto> getWithdrawalsMetricDto() {
        return List.of(
                new WithdrawalsMetricDto("1", "mts", "1", "mts rub", "1", "gucci", "rub", "pending", "1"),
                new WithdrawalsMetricDto("2", "xxx", "2", "xxx usd", "1", "kaspi", "kzt", "succeeded", "1"),
                new WithdrawalsMetricDto("3", "reppay", "3", "reppay kzt", "1", "kaspi", "usd", "failed", "1"));
    }
}
