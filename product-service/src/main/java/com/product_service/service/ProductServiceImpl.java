package com.product_service.service;

import com.product_service.dto.ProductDto;
import com.product_service.entity.Product;
import com.product_service.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{

    private ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(ProductDto productDto,String filename) {
        Product product=new Product();
        product.setName(productDto.getName());
        product.setPoints(productDto.getPoints());
        product.setDescription(productDto.getDescription());
        product.setImage(filename);
        product.setUserId(productDto.getUserId());
        product.setCategory(productDto.getCategory());
        return   productRepository.save(product);
    }

    @Override
    public Product updateProduct(ProductDto productDto) {
        return null;
    }

    @Override
    public List<Product> getAllProduct() {
        return  productRepository.findAll();
    }

    @Override
    public Product getProductById(int id) {
        Optional<Product> product=productRepository.findById(id);
        if(product.isEmpty()){
            return null;
        }
        Product productObj=product.get();
        return productObj;
    }

    @Override
    public List<Product> getProductsByUserId(int userId) {
        return List.of();
    }
}
