package org.springframework.samples.petclinic.vets.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.vets.model.Specialty;
import org.springframework.samples.petclinic.vets.model.Vet;
import org.springframework.samples.petclinic.vets.model.VetRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Arrays.asList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VetResource.class)
@ActiveProfiles("test")
class VetResourceTest2 {

    @Autowired
    MockMvc mvc;

    @MockBean
    VetRepository vetRepository;

    @Test
    void shouldReturnEmptyListWhenNoVets() throws Exception {
        given(vetRepository.findAll()).willReturn(asList());

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }
    

     @Test
    public void shouldGetVetsWithDetails() throws Exception {
        // Create specialties
        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        Specialty surgery = new Specialty();
        surgery.setId(2);
        surgery.setName("surgery");

        // Create vets
        Vet vet1 = new Vet();
        vet1.setId(1);
        vet1.setFirstName("James");
        vet1.setLastName("Carter");
        vet1.addSpecialty(radiology);
        vet1.addSpecialty(surgery);

        Vet vet2 = new Vet();
        vet2.setId(2);
        vet2.setFirstName("Helen");
        vet2.setLastName("Leary");

        // Mock repository
        given(vetRepository.findAll()).willReturn(Arrays.asList(vet1, vet2));

        // Perform request
        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("James"))
            .andExpect(jsonPath("$[0].lastName").value("Carter"))
            .andExpect(jsonPath("$[0].specialties.length()").value(2))
            .andExpect(jsonPath("$[0].specialties[?(@.id == 1)]").exists())
            .andExpect(jsonPath("$[0].specialties[?(@.name == 'radiology')]").exists())
            .andExpect(jsonPath("$[0].specialties[?(@.id == 2)]").exists())
            .andExpect(jsonPath("$[0].specialties[?(@.name == 'surgery')]").exists())
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].firstName").value("Helen"))
            .andExpect(jsonPath("$[1].lastName").value("Leary"))
            .andExpect(jsonPath("$[1].specialties.length()").value(0));
    }

    @Test
    public void shouldGetEmptyListWhenNoVets() throws Exception {
        // Mock repository to return an empty list
        given(vetRepository.findAll()).willReturn(Arrays.asList());

        // Perform request
        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }
}
