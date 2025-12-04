package app.product.service;

import app.exception.ProductAlreadyExistException;
import app.exception.ProductNotFoundException;
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
import java.util.UUID;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void createInit(ProductDetails product) {

        Optional<Product> optionalProduct = productRepository.findByName(product.getName());
        if (optionalProduct.isPresent()) {
            throw new ProductAlreadyExistException("Product with [%s] already exist.".formatted(product.getName()));
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

        log.info("---Product [%s] was created.".formatted(product.getName()));
    }

    public Product getById(UUID id) {

        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with [%s] id not found.".formatted(id)));
    }

    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }

    public List<Product> getAllMaleProducts() {

        List<Product> products = productRepository.findAll();

        return products
                .stream()
                .filter(p -> p.getGender() == ProductGender.MALE)
                .toList();
    }

    public List<Product> getAllFemaleProducts() {

        List<Product> products = productRepository.findAll();

        return products
                .stream()
                .filter(p -> p.getGender() == ProductGender.FEMALE)
                .toList();
    }
}