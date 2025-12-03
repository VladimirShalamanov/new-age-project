package app.web;

import app.product.model.Product;
import app.product.model.ProductGender;
import app.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerApiTest {

    @MockitoBean
    ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getProductsPage_shouldReturnProductsViewWithAllProducts() throws Exception {

        List<Product> products = List.of(aRandomProduct(), aRandomProduct());

        when(productService.getAllProducts()).thenReturn(products);

        MockHttpServletRequestBuilder httpRequest = get("/products");

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attribute("products", products));
    }

    @Test
    void getMenProducts_shouldReturnProductsViewWithMaleProducts() throws Exception {

        List<Product> maleProducts = List.of(aRandomProduct());

        when(productService.getAllMaleProducts()).thenReturn(maleProducts);

        MockHttpServletRequestBuilder httpRequest = get("/products/men");

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attribute("products", maleProducts));
    }

    @Test
    void getWomenProducts_shouldReturnProductsViewWithFemaleProducts() throws Exception {

        List<Product> femaleProducts = List.of(aRandomProduct());

        when(productService.getAllFemaleProducts()).thenReturn(femaleProducts);

        MockHttpServletRequestBuilder httpRequest = get("/products/women");

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attribute("products", femaleProducts));
    }

    @Test
    void getProductDescriptionPage_shouldReturnProductDescriptionView() throws Exception {

        Product product = aRandomProduct();

        when(productService.getById(product.getId())).thenReturn(product);

        MockHttpServletRequestBuilder httpRequest = get("/products/{id}/description", product.getId());

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("product-description"))
                .andExpect(model().attribute("product", product));
    }

    public static Product aRandomProduct() {

        return Product.builder()
                .id(UUID.randomUUID())
                .name("prod")
                .price(new BigDecimal("25.00"))
                .gender(ProductGender.MALE)
                .description("some init test desc")
                .image("/images/testImg")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }
}