package kz.orderservice.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductRequestDto {
    @NotBlank(message = "Product Name should not be blank")
    @Schema(description = "Name of the product", example = "Laptop")
    private String name;

    @NotNull(message = "Product Price should not be null")
    @Positive(message = "Product Price should be Positive")
    @Schema(description = "Price of the product", example = "1500.0")
    private Double price;

    @NotNull(message = "Product Quantity should not be null")
    @Positive(message = "Product Quantity should be Positive")
    @Schema(description = "Quantity of the product", example = "2")
    private Integer quantity;
}
