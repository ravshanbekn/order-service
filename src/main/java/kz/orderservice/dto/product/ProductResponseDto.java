package kz.orderservice.dto.product;

import lombok.Data;

@Data
public class ProductResponseDto {
    private Long productId;
    private String name;
    private Double price;
    private Integer quantity;
}
