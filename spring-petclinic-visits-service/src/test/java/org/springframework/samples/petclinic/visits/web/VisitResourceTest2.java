package org.springframework.samples.petclinic.visits.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.petclinic.visits.model.Visit;
import org.springframework.samples.petclinic.visits.model.VisitRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitResource.class)
@ActiveProfiles("test")
class VisitResourceTest2 {

    @Autowired
    MockMvc mvc;

    @MockBean
    VisitRepository visitRepository;

    @Test
    void shouldCreateVisit() throws Exception {
        // Chuẩn bị dữ liệu đầu vào
        String visitJson = "{\"date\": \"2025-03-17\", \"description\": \"Test visit\", \"petId\": 7}";

        // Tạo đối tượng Visit để mock kết quả từ repository
        Visit savedVisit = new Visit();
        savedVisit.setId(3);
        savedVisit.setPetId(7);
        savedVisit.setDescription("Test visit");

        // Sử dụng SimpleDateFormat với múi giờ UTC để parse ngày tháng
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        savedVisit.setDate(sdf.parse("2025-03-17"));

        // Mock hành vi của repository
        given(visitRepository.save(any(Visit.class))).willReturn(savedVisit);

        // Gửi yêu cầu POST và kiểm tra kết quả
        mvc.perform(post("/owners/1/pets/7/visits")
                .contentType(APPLICATION_JSON)
                .content(visitJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.petId").value(7))
            .andExpect(jsonPath("$.description").value("Test visit"))
            .andExpect(jsonPath("$.date").value("2025-03-17"));
    }

    @Test
    void shouldFetchVisitsForSinglePet() throws Exception {
        // Chuẩn bị danh sách Visits để mock, dựa trên dữ liệu từ DB
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Visit visit1 = Visit.VisitBuilder.aVisit()
            .id(1)
            .petId(7)
            .description("rabies shot")
            .date(sdf.parse("2010-03-04"))
            .build();

        Visit visit2 = Visit.VisitBuilder.aVisit()
            .id(4)
            .petId(7)
            .description("spayed")
            .date(sdf.parse("2008-09-04"))
            .build();

        // Mock hành vi của repository
        given(visitRepository.findByPetId(7)).willReturn(Arrays.asList(visit1, visit2));

        // Gửi yêu cầu GET và kiểm tra kết quả
        mvc.perform(get("/owners/1/pets/7/visits"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].petId").value(7))
            .andExpect(jsonPath("$[0].description").value("rabies shot"))
            .andExpect(jsonPath("$[0].date").value("2010-03-04"))
            .andExpect(jsonPath("$[1].id").value(4))
            .andExpect(jsonPath("$[1].petId").value(7))
            .andExpect(jsonPath("$[1].description").value("spayed"))
            .andExpect(jsonPath("$[1].date").value("2008-09-04"));
    }
}