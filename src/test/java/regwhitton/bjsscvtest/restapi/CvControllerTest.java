package regwhitton.bjsscvtest.restapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import regwhitton.bjsscvtest.model.Cv;
import regwhitton.bjsscvtest.service.cv.CvService;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CvController.class)
@AutoConfigureMockMvc
class CvControllerTest {

    @MockBean
    private CvService cvService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldCreateNewCv() throws Exception {
        Cv cv = Cv.builder().firstName("Reginald").preferredFirstName("Reg").surname("Whitton").build();
        Cv createdCv = cv.toBuilder().id(123456789L).build();
        given(cvService.create(cv)).willReturn(createdCv);

        mvc.perform(
                post("/api/cv")
                        .contextPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Reginald\", \"preferredFirstName\":\"Reg\", \"surname\":\"Whitton\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/cv/123456789"))
                .andExpect(content().json(
                        "{'id':123456789, 'firstName':'Reginald', 'preferredFirstName':'Reg', 'surname':'Whitton'}"));
    }
}