package com.thacbao.codeSphere.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    pending, completed, failed, refunded;
}
