package com.boardcamp.boardcamp.services;

import com.boardcamp.boardcamp.dtos.CustomerDTO;
import com.boardcamp.boardcamp.models.CustomerModel;
import com.boardcamp.boardcamp.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Optional<CustomerModel> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    // POST "/customers"
    public Optional<CustomerModel> createCustomer(CustomerDTO customerDTO) {
        if (customerDTO.getName() == null || customerDTO.getName().isBlank()) return Optional.empty();
        if (customerDTO.getCpf() == null || customerDTO.getCpf().length() != 11) return Optional.empty();
        if (customerDTO.getPhone() == null || !(customerDTO.getPhone().length() == 10 || customerDTO.getPhone().length() == 11))
            return Optional.empty();
        if (customerRepository.existsByCpf(customerDTO.getCpf())) return Optional.empty();

        CustomerModel customer = new CustomerModel();
        customer.setName(customerDTO.getName());
        customer.setCpf(customerDTO.getCpf());
        customer.setPhone(customerDTO.getPhone());

        customerRepository.save(customer);
        return Optional.of(customer);
    }
}
