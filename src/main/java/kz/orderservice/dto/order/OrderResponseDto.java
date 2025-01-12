package kz.orderservice.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import kz.orderservice.dto.product.ProductResponseDto;
import kz.orderservice.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    @Schema(description = "Unique identifier of the order", example = "1")
    private Long orderId;

    @Schema(description = "Name of the customer who placed the order", example = "John")
    private String customerName;

    @Schema(description = "Current status of the order", example = "PENDING")
    private OrderStatus status;

    @Schema(description = "Total price of the order", example = "3000.0")
    private Double totalPrice;

    @Schema(description = "List of products included in the order")
    private List<ProductResponseDto> products;

    @Schema(description = "Indicates whether the order is deleted", example = "false")
    private Boolean isDeleted;
}
