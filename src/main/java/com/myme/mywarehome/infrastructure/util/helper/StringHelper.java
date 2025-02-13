package com.myme.mywarehome.infrastructure.util.helper;

import java.security.SecureRandom;

public class StringHelper {
    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }

        return sb.toString();
    }
    // Todo: 리팩토링 필요
    public class CodeGenerator {

        private static final int CODE_LENGTH = 10;
        private static final String ID_FORMAT = "%08d";  // 8자리 숫자 포맷

        // IssuePlan 코드 생성 (IP + 8자리 숫자)
        public static String generateIssuePlanCode(Long id) {
            return String.format("IP" + ID_FORMAT, id);
        }

        // ReceiptPlan 코드 생성 (RP + 8자리 숫자)
        public static String generateReceiptPlanCode(Long id) {
            return String.format("RP" + ID_FORMAT, id);
        }

        // Receipt 코드 생성 (RZ + 8자리 숫자)
        public static String generateReceiptCode(Long id) {
            return String.format("RZ" + ID_FORMAT, id);
        }

        // Issue 코드 생성 (IZ + 8자리 숫자)
        public static String generateIssueCode(Long id) {
            return String.format("IZ" + ID_FORMAT, id);
        }

        // Stock 코드 생성 (SZ + 8자리 숫자)
        public static String generateStockCode(Long id) {
            return String.format("SZ" + ID_FORMAT, id);
        }

        // InventoryRecord 코드 생성 (IR + 8자리 숫자)
        public static String generateInventoryRecordCode(Long id) {
            return String.format("IR" + ID_FORMAT, id);
        }

        // 코드 유효성 검증 메서드
        public static boolean isValidCode(String code) {
            if (code == null || code.length() != CODE_LENGTH) {
                return false;
            }

            String prefix = code.substring(0, 2);
            String number = code.substring(2);

            return (prefix.equals("IP") || prefix.equals("RP") ||
                    prefix.equals("RZ") || prefix.equals("IZ") ||
                    prefix.equals("IR")) &&
                    number.matches("\\d{8}");  // 8자리 숫자인지 확인
        }
    }
}
