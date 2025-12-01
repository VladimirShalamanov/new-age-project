package app.web;

import app.product.model.Product;
import app.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ModelAndView getProductsPage() {

        List<Product> allProducts = productService.getAllProducts();

        ModelAndView model = new ModelAndView("products");
        model.addObject("products", allProducts);

        return model;
    }

    @GetMapping("/men")
    public ModelAndView getMenProducts() {

        List<Product> maleProducts = productService.getAllMaleProducts();

        ModelAndView model = new ModelAndView("products");
        model.addObject("products", maleProducts);

        return model;
    }

    @GetMapping("/women")
    public ModelAndView getWomenProducts() {

        List<Product> womenProducts = productService.getAllFemaleProducts();

        ModelAndView model = new ModelAndView("products");
        model.addObject("products", womenProducts);

        return model;
    }

    @GetMapping("/{id}/description")
    public ModelAndView getProductDescriptionPage(@PathVariable UUID id) {

        Product product = productService.getById(id);

        ModelAndView model = new ModelAndView("product-description");
        model.addObject("product", product);

        return model;
    }
}
