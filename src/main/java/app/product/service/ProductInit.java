package app.product.service;

import app.product.model.Product;
import app.product.property.ProductProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ProductInit implements ApplicationRunner {

    private final ProductService productService;
    private final ProductProperties productProperties;

    @Autowired
    public ProductInit(ProductService productService, ProductProperties productProperties) {
        this.productService = productService;
        this.productProperties = productProperties;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<Product> products = productService.getAllProducts();

        productProperties.getProducts().forEach(product -> {

            boolean initProductDoesNotExist = products
                    .stream()
                    .noneMatch(p -> p.getName().equals(product.getName()));

            if (initProductDoesNotExist) {
                productService.createInit(product);
            } else {
                log.info("---Product (you tried to seed) [%s] is Present.".formatted(product.getName()));
            }
        });
    }
}