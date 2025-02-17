package com.myme.mywarehome.domains.mrp.application.domain;

import com.myme.mywarehome.domains.mrp.application.exception.EngineNotFoundException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum EngineType {
    KAPPA("Kappa", "Kappa 엔진"),
    GAMMA("Gamma", "Gamma 엔진"),
    NU("Nu", "Nu 엔진"),
    THETA("Theta", "Theta 엔진");

    private final String pathName;
    private final String dbName;

    EngineType(String pathName, String dbName) {
        this.pathName = pathName;
        this.dbName = dbName;
    }

    public static String convertToDbName(String pathName) {
        return Arrays.stream(values())
                .filter(engine -> engine.pathName.equalsIgnoreCase(pathName))
                .findFirst()
                .map(engine -> engine.dbName)
                .orElseThrow(EngineNotFoundException::new);
    }

    // 필요한 경우 DB 이름으로 enum을 찾는 메서드도 추가할 수 있습니다
    public static EngineType findByDbName(String dbName) {
        return Arrays.stream(values())
                .filter(engine -> engine.dbName.equals(dbName))
                .findFirst()
                .orElseThrow(EngineNotFoundException::new);
    }
}
