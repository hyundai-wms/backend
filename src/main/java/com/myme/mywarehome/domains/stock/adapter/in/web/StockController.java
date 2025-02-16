package com.myme.mywarehome.domains.stock.adapter.in.web;

import com.myme.mywarehome.domains.stock.adapter.in.web.request.GetAllStockRequest;
import com.myme.mywarehome.domains.stock.adapter.in.web.response.GetAllStockLocationResponse;
import com.myme.mywarehome.domains.stock.adapter.in.web.response.GetAllStockResponse;
import com.myme.mywarehome.domains.stock.adapter.in.web.response.GetSpecificStockResponse;
import com.myme.mywarehome.domains.stock.adapter.in.web.response.GetStockLocationByProductNumberResponse;
import com.myme.mywarehome.domains.stock.adapter.in.web.response.GetStockResponse;
import com.myme.mywarehome.domains.stock.application.exception.StockNotFoundException;
import com.myme.mywarehome.domains.stock.application.port.in.GetAllStockLocationUseCase;
import com.myme.mywarehome.domains.stock.application.port.in.GetAllStockUseCase;
import com.myme.mywarehome.domains.stock.application.port.in.GetSpecificStockUseCase;
import com.myme.mywarehome.domains.stock.application.port.in.GetStockLocationUseCase;
import com.myme.mywarehome.domains.stock.application.port.in.GetStockUseCase;
import com.myme.mywarehome.infrastructure.common.request.SelectedDateRequest;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/storages/inventories")
@RequiredArgsConstructor
public class StockController {
    private final GetAllStockUseCase getAllStockUseCase;
    private final GetStockUseCase getStockUseCase;
    private final GetAllStockLocationUseCase getAllStockLocationUseCase;
    private final GetStockLocationUseCase getStockLocationUseCase;
    private final GetSpecificStockUseCase getSpecificStockUseCase;

    @GetMapping
    public CommonResponse<GetAllStockResponse> getAllStock(
        @Valid GetAllStockRequest request,
        @PageableDefault(sort = "productNumber", direction = Direction.ASC) Pageable pageable,
        @Valid @RequestBody(required = false) SelectedDateRequest selectedDateRequest
    ) {
        return CommonResponse.from(
                GetAllStockResponse.from(
                        getAllStockUseCase.getAllStockList(request.toCommand(), pageable, SelectedDateRequest.toLocalDate(selectedDateRequest))
                )
        );
    }

    @GetMapping("/{productNumber}/details")
    public CommonResponse<GetStockResponse> getStock(
            @PathVariable("productNumber") String productNumber,
            @Valid @RequestBody(required = false) SelectedDateRequest selectedDateRequest,
            @PageableDefault Pageable pageable
    ) {
        return CommonResponse.from(
                GetStockResponse.from(
                        getStockUseCase.getStockList(productNumber, pageable,
                                SelectedDateRequest.toLocalDate(selectedDateRequest))
                )
        );
    }

    @GetMapping("/locations")
    public CommonResponse<GetAllStockLocationResponse> getAllStockLocation(
            @PageableDefault(size = 800) Pageable pageable
    ) {
        return CommonResponse.from(
                GetAllStockLocationResponse.from(
                        getAllStockLocationUseCase.getAllBayList(pageable)
                )
        );
    }

    @GetMapping("/{productNumber}/locations")
    public CommonResponse<GetStockLocationByProductNumberResponse> getStockLocationByProductNumber(
        @PathVariable("productNumber") String productNumber
    ) {
        return CommonResponse.from(
                GetStockLocationByProductNumberResponse.from(
                        getStockLocationUseCase.getBayList(productNumber)
                )
        );
    }

    @GetMapping("/items/{itemId}")
    public CommonResponse<GetSpecificStockResponse> getSpecificStock(
            @PathVariable("itemId") Long stockId
    ) {
        return CommonResponse.from(
                getSpecificStockUseCase.getSpecificStock(stockId)
                        .map(GetSpecificStockResponse::from)
                        .orElseThrow(StockNotFoundException::new)
        );
    }

}
