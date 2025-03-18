package org.springframework.samples.petclinic.customers.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.samples.petclinic.customers.web.mapper.OwnerEntityMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OwnerResource.class)
@ActiveProfiles("test")
class OwnerResourceTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private OwnerRepository ownerRepository;

    @MockBean
    private OwnerEntityMapper ownerEntityMapper;

    @Autowired
    private ObjectMapper objectMapper;

    // Existing test for findOwner (provided)
    @Test
    void shouldGetOwnerById() throws Exception {
        Owner owner = new Owner();
        owner.setId(1);
        owner.setFirstName("George");
        owner.setLastName("Franklin");

        given(ownerRepository.findById(1)).willReturn(Optional.of(owner));

        mvc.perform(get("/owners/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("George"))
            .andExpect(jsonPath("$.lastName").value("Franklin"));
    }

    // Test for createOwner with valid data
    @Test
    void shouldCreateOwner() throws Exception {
        OwnerRequest ownerRequest = new OwnerRequest("John", "Doe", "123 Main St", "Anytown", "1234567890");

        Owner savedOwner = new Owner();
        savedOwner.setId(1);
        savedOwner.setFirstName("John");
        savedOwner.setLastName("Doe");
        savedOwner.setAddress("123 Main St");
        savedOwner.setCity("Anytown");
        savedOwner.setTelephone("1234567890");

        Owner mappedOwner = new Owner();
        mappedOwner.setFirstName("John");
        mappedOwner.setLastName("Doe");
        mappedOwner.setAddress("123 Main St");
        mappedOwner.setCity("Anytown");
        mappedOwner.setTelephone("1234567890");

        given(ownerEntityMapper.map(any(Owner.class), eq(ownerRequest))).willReturn(mappedOwner);
        given(ownerRepository.save(any(Owner.class))).willReturn(savedOwner);

        mvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ownerRequest))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"))
            .andExpect(jsonPath("$.address").value("123 Main St"))
            .andExpect(jsonPath("$.city").value("Anytown"))
            .andExpect(jsonPath("$.telephone").value("1234567890"));
    }

    // Test for createOwner with invalid data
    @Test
    void shouldReturnBadRequestWhenCreateInvalid() throws Exception {
        OwnerRequest ownerRequest = new OwnerRequest(null, "Doe", "123 Main St", "Anytown", "1234567890");

        mvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ownerRequest)))
            .andExpect(status().isBadRequest());
    }

    // Test for findAll with owners
    @Test
    void shouldFindAllOwners() throws Exception {
        Owner owner1 = new Owner();
        owner1.setId(1);
        owner1.setFirstName("George");
        owner1.setLastName("Franklin");

        Owner owner2 = new Owner();
        owner2.setId(2);
        owner2.setFirstName("John");
        owner2.setLastName("Doe");

        List<Owner> owners = List.of(owner1, owner2);
        given(ownerRepository.findAll()).willReturn(owners);

        mvc.perform(get("/owners").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("George"))
            .andExpect(jsonPath("$[0].lastName").value("Franklin"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].firstName").value("John"))
            .andExpect(jsonPath("$[1].lastName").value("Doe"));
    }

    // Test for findAll with no owners
    @Test
    void shouldReturnEmptyListWhenNoOwners() throws Exception {
        given(ownerRepository.findAll()).willReturn(List.of());

        mvc.perform(get("/owners").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    // Test for updateOwner with existing owner
    @Test
    void shouldUpdateOwner() throws Exception {
        int ownerId = 1;
        Owner existingOwner = new Owner();
        existingOwner.setId(ownerId);
        existingOwner.setFirstName("OldFirst");
        existingOwner.setLastName("OldLast");
        existingOwner.setAddress("Old Address");
        existingOwner.setCity("Old City");
        existingOwner.setTelephone("0987654321");

        given(ownerRepository.findById(ownerId)).willReturn(Optional.of(existingOwner));
        given(ownerRepository.save(any(Owner.class))).willReturn(existingOwner);

        OwnerRequest ownerRequest = new OwnerRequest("NewFirst", "NewLast", "New Address", "New City", "1234567890");

        mvc.perform(put("/owners/" + ownerId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ownerRequest)))
            .andExpect(status().isNoContent());
    }
    // Test for updateOwner with non-existent owner
    @Test
    void shouldReturnNotFoundWhenUpdateOwnerNotFound() throws Exception {
        int ownerId = 999;
        given(ownerRepository.findById(ownerId)).willReturn(Optional.empty());

        OwnerRequest ownerRequest = new OwnerRequest("NewFirst", "NewLast", "New Address", "New City", "1234567890");

        mvc.perform(put("/owners/" + ownerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ownerRequest)))
            .andExpect(status().isNotFound());
    }

}