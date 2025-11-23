package com.rebuy.backend.service;

import org.springframework.stereotype.Service;
import com.rebuy.backend.model.Product;
import com.rebuy.backend.repository.ProductRepository;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product saveProduct(Product product){
        return productRepository.save(product);
    }

    public List<Product> getAll(){
        return productRepository.findAll();
    }
}
