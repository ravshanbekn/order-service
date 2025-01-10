package kz.orderservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    PENDING("pending"),
    CONFIRMED("confirmed"),
    CANCELLED("cancelled");

    private final String textVal;

    public static OrderStatus fromString(String text) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.textVal.equalsIgnoreCase(text)) {
                return status;
            }
        }
        return null;
    }
}
