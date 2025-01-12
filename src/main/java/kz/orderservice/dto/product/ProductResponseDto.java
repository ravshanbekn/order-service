package kz.orderservice.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    @Schema(description = "Unique identifier of the product", example = "1")
    private Long productId;

    @Schema(description = "Name of the product", example = "Laptop")
    private String name;

    @Schema(description = "Price of the product", example = "1500.0")
    private Double price;

    @Schema(description = "Quantity of the product available", example = "2")
    private Integer quantity;
}
