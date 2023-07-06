package dev.vality.exporter.businessmetrics;

import dev.vality.exporter.businessmetrics.entity.payment.PaymentsMetricDto;
import dev.vality.exporter.businessmetrics.entity.payment.PaymentsTransactionCountMetricDto;
import dev.vality.exporter.businessmetrics.entity.withdrawal.WithdrawalsMetricDto;
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
        var paymentsFinalStatusesMetrics = getPaymentsFinalStatusMetricDtos();
        var paymentsTransactionCountMetrics = getPaymentsTransactionCountMetricDtos();
        var withdrawalsFinalStatusesMetrics = getWithdrawalsFinalStatusMetricDtos();
        when(paymentRepository.getPaymentsFinalStatusMetricsByInterval(any())).thenReturn(paymentsFinalStatusesMetrics);
        when(paymentRepository.getPaymentsCountMetricsByInterval(any())).thenReturn(paymentsTransactionCountMetrics);
        when(withdrawalRepository.getWithdrawalsFinalStatusMetricsByInterval(any())).thenReturn(withdrawalsFinalStatusesMetrics);
        schedulerRegisterMetricsService.registerMetricsTask();
        verify(paymentRepository, times(1)).getPaymentsFinalStatusMetricsByInterval(any());
        verify(paymentRepository, times(1)).getPaymentsCountMetricsByInterval(any());
        verify(withdrawalRepository, times(1)).getWithdrawalsFinalStatusMetricsByInterval(any());
        var mvcResult = mockMvc.perform(get("/actuator/prometheus"))
                .andReturn();
        var prometheusResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var actualMetrics = Arrays.stream(prometheusResponse.split("\n"))
                .filter(row -> row.startsWith("ebm_")).toList();
        Assertions.assertEquals(15, actualMetrics.size());
    }

    private List<PaymentsMetricDto> getPaymentsFinalStatusMetricDtos() {
        return List.of(
                new PaymentsMetricDto("1", "mts", "1", "mts rub", "1", "gucci", "rub", "rus", "kaspi jsp", "visa", "pending", "1", "280000000"),
                new PaymentsMetricDto("2", "xxx", "2", "xxx usd", "1", "kaspi", "kzt", "kz", "kaspi jsp", "visa", "captured", "1", "280000000"),
                new PaymentsMetricDto("3", "reppay", "3", "reppay kzt", "1", "kaspi", "usd", "usa", "undefined", "visa", "failed", "1", "280000000"));
    }

    private List<PaymentsTransactionCountMetricDto> getPaymentsTransactionCountMetricDtos() {
        return List.of(
                new PaymentsTransactionCountMetricDto("1", "mts", "1", "mts rub", "1", "gucci", "rub", "1"),
                new PaymentsTransactionCountMetricDto("2", "xxx", "2", "xxx usd", "1", "kaspi", "kzt", "1"),
                new PaymentsTransactionCountMetricDto("3", "reppay", "3", "reppay kzt", "1", "kaspi", "usd", "1"));
    }

    private List<WithdrawalsMetricDto> getWithdrawalsFinalStatusMetricDtos() {
        return List.of(
                new WithdrawalsMetricDto("1", "mts", "1", "mts rub", "1", "gucci", "rub", "pending", "1", "280000000"),
                new WithdrawalsMetricDto("2", "xxx", "2", "xxx usd", "1", "kaspi", "kzt", "succeeded", "1", "280000000"),
                new WithdrawalsMetricDto("3", "reppay", "3", "reppay kzt", "1", "kaspi", "usd", "failed", "1", "280000000"));
    }
}
