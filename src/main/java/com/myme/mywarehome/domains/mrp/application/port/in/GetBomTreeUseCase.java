package com.myme.mywarehome.domains.mrp.application.port.in;

import com.myme.mywarehome.domains.mrp.application.domain.BomTree;

import java.util.List;

public interface GetBomTreeUseCase {
    List<BomTree> getBomTreeByEngine(String applicableEngine);
}

