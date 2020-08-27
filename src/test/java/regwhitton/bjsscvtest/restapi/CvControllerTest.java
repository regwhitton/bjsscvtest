package regwhitton.bjsscvtest.restapi;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import regwhitton.bjsscvtest.model.Address;
import regwhitton.bjsscvtest.model.Cv;
import regwhitton.bjsscvtest.service.cv.CvService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CvController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CvControllerTest {

    @MockBean
    private CvService cvService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldCreateNewCv() throws Exception {
        Cv cv = Cv.builder().firstName("Sidney").preferredFirstName("Sid").surname("James").build();
        Cv createdCv = cv.toBuilder().id(123456789L).build();
        given(cvService.create(cv)).willReturn(createdCv);

        mvc.perform(
                post("/api/cv")
                        .contextPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Sidney\", \"preferredFirstName\":\"Sid\", \"surname\":\"James\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/cv/123456789"))
                .andExpect(content().json(
                        "{'id':123456789, 'firstName':'Sidney', 'preferredFirstName':'Sid', 'surname':'James'}"));
    }

    @Test
    public void shouldUpdateCv_ignoringIdInBody() throws Exception {
        Cv expectedCv = Cv.builder()
                .id(123456789L)
                .version(100L)
                .firstName("Sidney")
                .preferredFirstName("Sid")
                .surname("James")
                .build();
        Cv updatedCv = expectedCv.toBuilder().version(101L).build();
        given(cvService.update(expectedCv)).willReturn(updatedCv);

        mvc.perform(
                put("/api/cv/123456789")
                        .contextPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":-1, \"version\":100, \"firstName\":\"Sidney\"," +
                                " \"preferredFirstName\":\"Sid\", \"surname\":\"James\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{'id':123456789, 'version': 101, 'firstName':'Sidney', 'preferredFirstName':'Sid', 'surname':'James'}"));
    }

    @Test
    public void shouldGetCv() throws Exception {
        Cv cv = Cv.builder()
                .id(123456789L)
                .version(100L)
                .firstName("Sidney")
                .preferredFirstName("Sid")
                .surname("James")
                .build();
        given(cvService.fetch(123456789L)).willReturn(cv);

        mvc.perform(
                get("/api/cv/123456789")
                        .contextPath("/api")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{'id':123456789, 'version': 100, 'firstName':'Sidney'," +
                                " 'preferredFirstName':'Sid', 'surname':'James'}"));
    }

    @Test
    public void shouldGetAllCvs() throws Exception {
        List<Cv> cvs = Lists.list(
                Cv.builder()
                        .id(12345L)
                        .version(10L)
                        .firstName("Sidney")
                        .preferredFirstName("Sid")
                        .middleNames("Solomon Joel")
                        .surname("James")
                        .address(Address.builder()
                                .addressLine1("3 Swing Back Lane")
                                .addressLine2("Dimley")
                                .city("Hostand")
                                .county("Dinshire")
                                .postalCode("JK34 8PS")
                                .build()
                        )
                        .build(),
                Cv.builder()
                        .id(56789L)
                        .version(99L)
                        .firstName("Joseph")
                        .preferredFirstName("Joe")
                        .middleNames("Fredrick Douglas")
                        .surname("Bloggs")
                        .address(Address.builder()
                                .addressLine1("11 George Street")
                                .addressLine2("Grimley")
                                .city("Cheam")
                                .county("North Ruddles")
                                .postalCode("LD99 3JJ")
                                .build()
                        )
                        .build());
        given(cvService.fetchAll()).willReturn(cvs);

        mvc.perform(
                get("/api/cv")
                        .contextPath("/api")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "[\n" +
                                "  {\n" +
                                "    'id': 12345,\n" +
                                "    'version': 10,\n" +
                                "    'firstName': 'Sidney',\n" +
                                "    'preferredFirstName': 'Sid',\n" +
                                "    'middleNames': 'Solomon Joel',\n" +
                                "    'surname': 'James',\n" +
                                "    'dateOfBirth': null,\n" +
                                "    'email': null,\n" +
                                "    'telephone': null,\n" +
                                "    'address': {\n" +
                                "      'addressLine1': '3 Swing Back Lane',\n" +
                                "      'addressLine2': 'Dimley',\n" +
                                "      'city': 'Hostand',\n" +
                                "      'county': 'Dinshire',\n" +
                                "      'postalCode': 'JK34 8PS'\n" +
                                "    }\n" +
                                "  },\n" +
                                "  {\n" +
                                "    'id': 56789,\n" +
                                "    'version': 99,\n" +
                                "    'firstName': 'Joseph',\n" +
                                "    'preferredFirstName': 'Joe',\n" +
                                "    'middleNames': 'Fredrick Douglas',\n" +
                                "    'surname': 'Bloggs',\n" +
                                "    'dateOfBirth': null,\n" +
                                "    'email': null,\n" +
                                "    'telephone': null,\n" +
                                "    'address': {\n" +
                                "      'addressLine1': '11 George Street',\n" +
                                "      'addressLine2': 'Grimley',\n" +
                                "      'city': 'Cheam',\n" +
                                "      'county': 'North Ruddles',\n" +
                                "      'postalCode': 'LD99 3JJ'\n" +
                                "    }\n" +
                                "  }\n" +
                                "]\n"));
    }

}
