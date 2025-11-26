package app.product.service;

import app.product.model.Product;
import app.product.model.ProductGender;
import app.product.property.ProductProperties.ProductDetails;
import app.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void createInit(ProductDetails product) {

        // createInit may Rename => @PostMapping, Custom exe - existsByName()
        Optional<Product> optionalProduct = productRepository.findByName(product.getName());
        if (optionalProduct.isPresent()) {

            log.info("Product [%s] is Present.".formatted(product.getName()));
            return;
        }

        Product newProduct = Product.builder()
                .name(product.getName())
                .price(product.getPrice())
                .gender(product.getGender().equalsIgnoreCase(ProductGender.MALE.toString())
                        ? ProductGender.MALE
                        : ProductGender.FEMALE)
                .description(product.getDescription())
                .image(product.getImage())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        productRepository.save(newProduct);

        log.info("Product [%s] was created.".formatted(product.getName()));
    }

    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }
}
