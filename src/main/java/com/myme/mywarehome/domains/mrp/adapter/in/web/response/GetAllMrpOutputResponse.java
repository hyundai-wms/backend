package com.myme.mywarehome.domains.mrp.adapter.in.web.response;

import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;

public record GetAllMrpOutputResponse(
        List<MrpOutputInfo> content,
        Integer pageNumber,
        Integer pageSize,
        Long totalElements,
        Integer totalPages,
        Boolean isFirst,
        Boolean isLast

) {
    public record MrpOutputInfo(
            Long mrpOutputId,
            String mrpOutputCode,
            String createdDate,
            String dueDate,
            String orderedDate,
            Boolean isOrdered,
            Boolean canOrder,
            String orderPlanReportDownloadLink,
            String productPlanReportDownloadLink,
            String exceptionReportDownloadLink,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {

        public static MrpOutputInfo from(MrpOutput output) {
            return new MrpOutputInfo(
                    output.getMrpOutputId(),
                    output.getMrpOutputCode(),
                    DateFormatHelper.formatDate(output.getCreatedDate()),
                    DateFormatHelper.formatDate(output.getDueDate()),
                    DateFormatHelper.formatDate(output.getOrderedDate()),
                    output.getIsOrdered(),
                    output.getCanOrder(),
                    output.getPurchaseOrderReportLink(),
                    output.getProductionPlanningReportLink(),
                    output.getMrpExceptionReportLink(),
                    output.getCreatedAt(),
                    output.getUpdatedAt()
            );
        }
    }
    public static GetAllMrpOutputResponse from(Page<MrpOutput> page) {
        return new GetAllMrpOutputResponse(
                page.getContent().stream()
                        .map(MrpOutputInfo::from)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }


}
