package com.boardcamp.boardcamp.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.boardcamp.boardcamp.dtos.CustomerDTO;
import com.boardcamp.boardcamp.exceptions.ConflictException;
import com.boardcamp.boardcamp.exceptions.NotFoundException;
import com.boardcamp.boardcamp.models.CustomerModel;
import com.boardcamp.boardcamp.repositories.CustomerRepository;
import com.boardcamp.boardcamp.services.CustomerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerUnitTest {

    @InjectMocks
    CustomerService customerService;

    @Mock
    CustomerRepository customerRepository;

    CustomerDTO customerDTO;
    CustomerModel customerModel;

    @BeforeEach
    void setup() {
        customerDTO = new CustomerDTO("Amanda", "12345677654", "21999999999");

        customerModel = new CustomerModel();
        customerModel.setId(1L);
        customerModel.setName(customerDTO.getName());
        customerModel.setCpf(customerDTO.getCpf());
        customerModel.setPhone(customerDTO.getPhone());
    }

    @Test
    void givenValidCustomer_whenCreating_thenReturnsCustomer() {
        when(customerRepository.existsByCpf(customerDTO.getCpf())).thenReturn(false);
        when(customerRepository.save(any())).thenReturn(customerModel);

        CustomerModel result = customerService.createCustomer(customerDTO);

        assertNotNull(result);
        assertEquals(customerDTO.getName(), result.getName());
    }

    @Test
    void givenExistingCpf_whenCreating_thenThrowsConflictException() {
        when(customerRepository.existsByCpf(customerDTO.getCpf())).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            customerService.createCustomer(customerDTO);
        });
    }

    @Test
    void givenNonExistingId_whenFindingById_thenThrowsNotFoundException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customerService.getCustomerById(99L));
    }

    @Test
    void givenExistingId_whenFindingById_thenReturnsCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerModel));

        CustomerModel result = customerService.getCustomerById(1L);

        assertEquals(customerModel.getId(), result.getId());
    }

    @Test
    void whenGettingAllCustomers_thenReturnsList() {
        when(customerRepository.findAll()).thenReturn(List.of(customerModel));

        List<CustomerModel> result = customerService.getAllCustomers();

        assertEquals(1, result.size());
    }
}
