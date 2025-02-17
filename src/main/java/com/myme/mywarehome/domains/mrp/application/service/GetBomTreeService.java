package com.myme.mywarehome.domains.mrp.application.service;

import com.myme.mywarehome.domains.mrp.application.domain.BomTree;
import com.myme.mywarehome.domains.mrp.application.port.in.GetBomTreeUseCase;
import com.myme.mywarehome.domains.mrp.application.port.out.GetBomTreePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetBomTreeService implements GetBomTreeUseCase {
    private final GetBomTreePort getBomTreePort;

    // 1. BOM 조회
    @Override
    public List<BomTree> getBomTreeByEngine(String applicableEngine) {
        return getBomTreePort.findAllByApplicableEngine(applicableEngine);
    }
}
