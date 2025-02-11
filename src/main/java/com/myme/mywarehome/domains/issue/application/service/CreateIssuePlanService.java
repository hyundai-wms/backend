package com.myme.mywarehome.domains.issue.application.service;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.CreateIssuePlanUseCase;
import com.myme.mywarehome.domains.issue.application.port.out.CreateIssuePlanPort;
import com.myme.mywarehome.infrastructure.util.helper.StringHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateIssuePlanService implements CreateIssuePlanUseCase {
    private final CreateIssuePlanPort createIssuePlanPort;

    @Override
    @Transactional
    public IssuePlan create(IssuePlan issuePlan) {

        IssuePlan savedIssuePlan = createIssuePlanPort.create(issuePlan);
        // ID를 이용해서 코드 생성
        String code = StringHelper.CodeGenerator.generateIssuePlanCode(savedIssuePlan.getIssuePlanId());

        savedIssuePlan.setIssuePlanCode(code);

        return savedIssuePlan;
    }

    @Override
    @Transactional
    public List<IssuePlan> createBulk(List<IssuePlan> issuePlanList) {
        // 1. 대량 저장
        List<IssuePlan> savedIssuePlanList = createIssuePlanPort.createBulk(issuePlanList);

        // 2. 저장된 ID를 기반으로 코드 생성 및 업데이트
        savedIssuePlanList.forEach(plan -> {
            String code = StringHelper.CodeGenerator.generateIssuePlanCode(plan.getIssuePlanId());
            plan.setIssuePlanCode(code);
        });

        return savedIssuePlanList;
    }

}
