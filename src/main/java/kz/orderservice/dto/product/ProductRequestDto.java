package kz.orderservice.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductRequestDto {
    @NotBlank(message = "Product Name should not be blank")
    private String name;

    @NotNull(message = "Product Price should not be null")
    @Positive(message = "Product Price should be Positive")
    private Double price;

    @NotNull(message = "Product Quantity should not be null")
    @Positive(message = "Product Quantity should be Positive")
    private Integer quantity;
}
