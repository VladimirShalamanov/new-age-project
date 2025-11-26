package app.product.property;

import lombok.Data;
import app.config.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.math.BigDecimal;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties
@PropertySource(value = "product-details.yaml", factory = YamlPropertySourceFactory.class)
public class ProductProperties {

    private List<ProductDetails> products;

    @Data
    public static class ProductDetails {

        private String name;
        private BigDecimal price;
        private String gender;
        private String description;
        private String image;
    }
}