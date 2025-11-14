package com.boardcamp.boardcamp.integration;

import com.boardcamp.boardcamp.dtos.CustomerDTO;
import com.boardcamp.boardcamp.models.CustomerModel;
import com.boardcamp.boardcamp.repositories.CustomerRepository;
import com.boardcamp.boardcamp.repositories.GameRepository; 
import com.boardcamp.boardcamp.repositories.RentalRepository; 
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RentalRepository rentalRepository; 
    
    @Autowired
    private GameRepository gameRepository; 

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void clean() {
        rentalRepository.deleteAll(); 
        customerRepository.deleteAll();
        gameRepository.deleteAll(); 
    }

    private CustomerDTO sample() {
        return new CustomerDTO(
                "Marina Oliveira",
                "98765432112",
                "21988887777"
        );
    }

    @Test
    void createCustomer_valid_returnsCreated() throws Exception {
        var dto = sample();
        String body = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andExpect(jsonPath("$.cpf").value(dto.getCpf()));

        Assertions.assertEquals(1, customerRepository.count());
    }

    @Test
    void createCustomer_duplicatedCpf_returnsConflict() throws Exception {
        CustomerModel model = new CustomerModel(null, "Teste", "98765432112", "21977776666");
        customerRepository.save(model);

        var dto = sample();
        String body = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isConflict());

        Assertions.assertEquals(1, customerRepository.count());
    }

    @Test
    void getCustomer_existingId_returnsOk() throws Exception {
        CustomerModel saved = customerRepository.save(
                new CustomerModel(null, "Lilian", "11122233344", "21999998888")
        );

        mockMvc.perform(get("/customers/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.cpf").value("11122233344"));
    }

    @Test
    void getCustomer_nonExisting_returnsNotFound() throws Exception {
        mockMvc.perform(get("/customers/99999"))
                .andExpect(status().isNotFound());
    }
}