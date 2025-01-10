package kz.orderservice.dto.order;

import kz.orderservice.dto.product.ProductResponseDto;
import kz.orderservice.entity.OrderStatus;
import lombok.Data;

import java.util.List;

@Data
public class OrderResponseDto {
    private Long orderId;
    private String customerName;
    private OrderStatus status;
    private Double totalPrice;
    private List<ProductResponseDto> products;
    private Boolean isDeleted;
}
