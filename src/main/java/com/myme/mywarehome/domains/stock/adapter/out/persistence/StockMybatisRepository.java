package com.myme.mywarehome.domains.stock.adapter.out.persistence;

import com.myme.mywarehome.domains.stock.application.port.in.command.StockSummaryCommand;
import com.myme.mywarehome.domains.stock.application.port.in.result.StockSummaryResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StockMybatisRepository {
    List<StockSummaryResult> findStockSummaryList(
            @Param("command") StockSummaryCommand command,
            @Param("pageable") Pageable pageable,
            @Param("selectedDate") LocalDate selectedDate
    );

    Long countStockSummaries(@Param("command") StockSummaryCommand command);

    default Page<StockSummaryResult> findStockSummaries(
            StockSummaryCommand command,
            Pageable pageable,
            LocalDate selectedDate
    ) {
        List<StockSummaryResult> content = findStockSummaryList(command, pageable, selectedDate);
        Long total = countStockSummaries(command);
        return new PageImpl<>(content, pageable, total);
    }
}
