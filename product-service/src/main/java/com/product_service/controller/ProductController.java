package com.product_service.controller;

import com.product_service.dto.ProductDto;
import com.product_service.entity.Product;
import com.product_service.repository.ProductRepository;
import com.product_service.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products")
//@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private RestTemplate restTemplate;

    public ProductController(ProductService productService, ProductRepository productRepository, RestTemplate restTemplate) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.restTemplate = restTemplate;
    }
    @GetMapping
    public String index(){
        return  "Index file";
    }

    @PostMapping("/create-product")
    public ResponseEntity<Product> createProduct(@ModelAttribute ProductDto productDto){
        try{
            MultipartFile file = productDto.getImage();
            String fileName = System.currentTimeMillis()+file.getOriginalFilename();
            return new ResponseEntity<Product>(productService.createProduct(productDto,fileName),HttpStatus.CREATED);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable int id){
        try{
            return new ResponseEntity<Product>(productService.getProductById(id),HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/all-products")
    public ResponseEntity<List<Product>> getProduct(){
        return new ResponseEntity<List<Product>>(productService.getAllProduct(),HttpStatus.OK);
    }

        @GetMapping("/user-products/{userId}")
    public ResponseEntity<List<Product>> getProductByUser(@PathVariable("userId")  int userId){
        return new ResponseEntity<List<Product>>(productRepository.findByUserId(userId),HttpStatus.OK);
    }
    

}
