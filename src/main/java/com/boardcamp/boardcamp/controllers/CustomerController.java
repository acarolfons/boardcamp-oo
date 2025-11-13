package com.boardcamp.boardcamp.controllers;

import com.boardcamp.boardcamp.dtos.CustomerDTO;
import com.boardcamp.boardcamp.models.CustomerModel;
import com.boardcamp.boardcamp.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerModel>> getAllCustomers() {
        List<CustomerModel> customers = customerService.getAllCustomers();
        return ResponseEntity.status(HttpStatus.OK).body(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCustomerById(@PathVariable Long id) {
        Optional<CustomerModel> customer = customerService.getCustomerById(id);

        if (!customer.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(customer.get());
    }

    @PostMapping
    public ResponseEntity<Object> createCustomer(@RequestBody CustomerDTO customerDTO) {
        Optional<CustomerModel> customer = customerService.createCustomer(customerDTO);

        if (!customer.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid customer data or CPF already exists");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(customer.get());
    }
}
