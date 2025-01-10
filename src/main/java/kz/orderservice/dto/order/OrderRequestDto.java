package kz.orderservice.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import kz.orderservice.dto.product.ProductRequestDto;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {
    @NotBlank
    private String customerName;

    @NotEmpty
    private List<ProductRequestDto> products;
}