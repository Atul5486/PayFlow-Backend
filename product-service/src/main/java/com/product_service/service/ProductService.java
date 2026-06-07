package com.product_service.service;


import com.product_service.dto.ProductDto;
import com.product_service.entity.Product;

import java.util.List;

public interface ProductService {

    public Product createProduct(ProductDto productDto,String filename);

    public Product updateProduct(ProductDto productDto);

    public List<Product> getAllProduct();

    public Product getProductById(int id);

    public List<Product> getProductsByUserId(int userId);
}
