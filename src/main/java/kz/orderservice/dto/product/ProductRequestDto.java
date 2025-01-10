package kz.orderservice.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductRequestDto {
    @NotBlank
    private String name;

    @NotNull
    @Positive
    private Double price;

    @NotNull
    @Positive
    private Integer quantity;
}
