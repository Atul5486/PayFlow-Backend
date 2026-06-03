package com.paypal.transaction_service.controller;

import com.paypal.transaction_service.entity.Transaction;
import com.paypal.transaction_service.repository.TransactionRepository;
import com.paypal.transaction_service.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/transaction/")
//@CrossOrigin(origins = "http://localhost:5173")
public class TransactionController {

    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService){
        this.transactionService=transactionService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTransaction(@RequestBody Transaction transaction){

        Transaction response=transactionService.createTransaction(transaction);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable int id){
         return  ResponseEntity.ok(transactionService.getTransactionsByUser(id));
    }

}
