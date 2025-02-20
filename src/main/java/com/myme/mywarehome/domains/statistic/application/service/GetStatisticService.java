package com.myme.mywarehome.domains.statistic.application.service;
import com.myme.mywarehome.domains.statistic.adapter.in.web.response.GetStatisticResponse;
import com.myme.mywarehome.domains.statistic.application.port.in.GetStatisticUseCase;
import com.myme.mywarehome.domains.statistic.application.port.out.GetStatisticPort;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetStatisticService implements GetStatisticUseCase {
   private final GetStatisticPort getStatisticPort;

    @Override
    public GetStatisticResponse getStatistic(LocalDate selectedDate) {
        return GetStatisticResponse.of(
                getStatisticPort.countTodayPlanReceipts(selectedDate),
                getStatisticPort.countTodayReceipts(selectedDate),
                getStatisticPort.countTodayPlanIssues(selectedDate),
                getStatisticPort.countTodayIssues(selectedDate),
                getStatisticPort.countOccupiedBays(),
                3200,
                getStatisticPort.getDefaultPNCount(),
                getStatisticPort.getEngineCount(),
                getStatisticPort.getLastSevenMonthsWorkCount(selectedDate),
                getStatisticPort.getLastSevenMonthsReturnCount(selectedDate),
                getStatisticPort.getRecentReturnWorks()
        );
    }
}
