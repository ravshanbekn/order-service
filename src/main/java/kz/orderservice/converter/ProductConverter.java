package kz.orderservice.converter;

import kz.orderservice.dto.product.ProductRequestDto;
import kz.orderservice.dto.product.ProductResponseDto;
import kz.orderservice.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {

    public Product requestDtoToEntity(ProductRequestDto productRequestDto) {
        Product product = new Product();
        product.setName(productRequestDto.getName());
        product.setPrice(productRequestDto.getPrice());
        product.setQuantity(productRequestDto.getQuantity());
        return product;
    }

    public ProductResponseDto entityToResponseDto(Product product){
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setProductId(product.getProductId());
        productResponseDto.setName(product.getName());
        productResponseDto.setPrice(product.getPrice());
        productResponseDto.setQuantity(product.getQuantity());
        return productResponseDto;
    }
}
