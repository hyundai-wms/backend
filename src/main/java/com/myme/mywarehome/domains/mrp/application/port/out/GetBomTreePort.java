package com.myme.mywarehome.domains.mrp.application.port.out;

import com.myme.mywarehome.domains.mrp.application.domain.BomTree;

import java.util.List;

public interface GetBomTreePort {
    List<BomTree> findAllByApplicableEngine(String applicableEngine);
    List<BomTree> findAllByParentNumber(String parentProductNumber);
}
