package com.boardcamp.boardcamp.services;

import com.boardcamp.boardcamp.dtos.CustomerDTO;
import com.boardcamp.boardcamp.exceptions.BadRequestException;
import com.boardcamp.boardcamp.exceptions.ConflictException;
import com.boardcamp.boardcamp.exceptions.NotFoundException;
import com.boardcamp.boardcamp.models.CustomerModel;
import com.boardcamp.boardcamp.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // GET "/customers"
    public List<CustomerModel> getAllCustomers() {
        return customerRepository.findAll();
    }

    // GET "/customers/:id"
    public CustomerModel getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    // POST "/customers"
    public CustomerModel createCustomer(CustomerDTO customerDTO) {
        if (customerDTO.getName() == null || customerDTO.getName().isBlank())  throw new BadRequestException("Name is required");
        if (customerDTO.getCpf() == null || customerDTO.getCpf().length() != 11) throw new BadRequestException("Invalid CPF");
        if (customerDTO.getPhone() == null || !(customerDTO.getPhone().length() == 10 || customerDTO.getPhone().length() == 11))
            throw new BadRequestException("Invalid phone");
        if (customerRepository.existsByCpf(customerDTO.getCpf())) throw new ConflictException("CPF already registered");

        CustomerModel customer = new CustomerModel();
        customer.setName(customerDTO.getName());
        customer.setCpf(customerDTO.getCpf());
        customer.setPhone(customerDTO.getPhone());

        return customerRepository.save(customer);
    }
}
