package com.example.facturacionPrimeraEntregaNosicoski.Service;

import com.example.facturacionPrimeraEntregaNosicoski.Exception.ResourceNotFoundException;
import com.example.facturacionPrimeraEntregaNosicoski.Model.Product;
import com.example.facturacionPrimeraEntregaNosicoski.Repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService { private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + id));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Integer id, Product productDetails) {
        Product product = getProductById(id);
        product.setDescription(productDetails.getDescription());
        product.setCode(productDetails.getCode());
        product.setStock(productDetails.getStock());
        product.setPrice(productDetails.getPrice());
        return productRepository.save(product);
    }

    public void deleteProduct(Integer id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
}