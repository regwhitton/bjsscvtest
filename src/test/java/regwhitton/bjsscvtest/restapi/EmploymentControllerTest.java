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
import regwhitton.bjsscvtest.model.Cv;
import regwhitton.bjsscvtest.model.Employment;
import regwhitton.bjsscvtest.service.EmploymentService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmploymentController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmploymentControllerTest {

    @MockBean
    private EmploymentService employmentService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldCreateNewEmployment() throws Exception {
        final long CV_ID = 123456789L;
        final long EMP_ID = 987654321L;
        Employment emp = Employment.builder()
                .fromDate(LocalDate.of(1994, 8, 31))
                .untilDate(LocalDate.of(1998, 4, 15))
                .company("Waitrose")
                .position("Security Guard")
                .summary("Responsible for patrolling the premises overnight and monitoring CCTV.")
                .build();
        Employment createdEmp = emp.toBuilder()
                .id(EMP_ID)
                .cv(Cv.builder().id(CV_ID).build())
                .version(0L)
                .build();
        given(employmentService.create(CV_ID, emp)).willReturn(createdEmp);

        mvc.perform(
                post("/api/cv/{cvId}/employment/", CV_ID)
                        .contextPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromDate\":\"1994-08-31\", \"untilDate\":\"1998-04-15\", \"company\":\"Waitrose\"," +
                                "\"position\":\"Security Guard\", \"summary\":\"Responsible for patrolling the premises overnight and monitoring CCTV.\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/cv/" + CV_ID + "/employment/" + EMP_ID))
                .andExpect(content().json("{\"id\":" + EMP_ID + ", \"cvId\":" + CV_ID + ", \"version\":0, " +
                        "\"fromDate\":\"1994-08-31\", \"untilDate\":\"1998-04-15\", \"company\":\"Waitrose\"," +
                        "\"position\":\"Security Guard\", \"summary\":\"Responsible for patrolling the premises overnight and monitoring CCTV.\"}"));
    }

    @Test
    public void shouldUpdateEmployment_ignoringIdsInBody() throws Exception {
        final long CV_ID = 123456789L;
        final long EMP_ID = 987654321L;
        Employment expectedEmp = Employment.builder()
                .id(EMP_ID)
                .version(1L)
                .fromDate(LocalDate.of(1994, 8, 31))
                .untilDate(LocalDate.of(1998, 4, 15))
                .company("Waitrose")
                .position("Security Guard")
                .summary("Responsible for patrolling the premises overnight and monitoring CCTV.")
                .build();
        Employment updatedEmp = expectedEmp.toBuilder()
                .cv(Cv.builder().id(CV_ID).build())
                .version(2L)
                .build();
        given(employmentService.update(CV_ID, expectedEmp))
                .willReturn(updatedEmp);

        mvc.perform(
                put("/api/cv/{cvId}/employment/{id}", CV_ID, EMP_ID)
                        .contextPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":-1, \"cvId\":-1, " + // Ignored
                                "\"version\":1, " +
                                "\"fromDate\":\"1994-08-31\", \"untilDate\":\"1998-04-15\", \"company\":\"Waitrose\"," +
                                "\"position\":\"Security Guard\", \"summary\":\"Responsible for patrolling the premises overnight and monitoring CCTV.\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":" + EMP_ID + ", \"cvId\":" + CV_ID + ", \"version\":2, " +
                        "\"fromDate\":\"1994-08-31\", \"untilDate\":\"1998-04-15\", \"company\":\"Waitrose\", " +
                        "\"position\":\"Security Guard\", \"summary\":\"Responsible for patrolling the premises overnight and monitoring CCTV.\"}"));
    }

    @Test
    public void shouldDeleteEmployment() throws Exception {
        final long CV_ID = 123456789L;
        final long EMP_ID = 987654321L;

        mvc.perform(
                delete("/api/cv/{cvId}/employment/{id}", CV_ID, EMP_ID)
                        .contextPath("/api"))
                .andExpect(status().isNoContent());

        verify(employmentService, times(1)).delete(CV_ID, EMP_ID);
    }

    @Test
    public void shouldGetAllEmploymentsOnCv() throws Exception {
        final long CV_ID = 123456789L;
        Cv cv = Cv.builder().id(CV_ID).build();
        List<Employment> employments = Lists.list(
                Employment.builder()
                        .id(450L)
                        .cv(cv)
                        .version(1L)
                        .fromDate(LocalDate.of(1994, 8, 31))
                        .untilDate(LocalDate.of(1998, 4, 15))
                        .company("Waitrose")
                        .position("Security Guard")
                        .summary("Responsible for patrolling the premises overnight and monitoring CCTV.")
                        .build(),
                Employment.builder()
                        .id(451L)
                        .cv(cv)
                        .version(2L)
                        .fromDate(LocalDate.of(1998, 4, 16))
                        .untilDate(LocalDate.of(2002, 8, 1))
                        .company("Asda")
                        .position("Customer Service Assistant")
                        .summary("Dealing with customers' queries and complaints.")
                        .build());
        given(employmentService.fetchAll(CV_ID))
                .willReturn(employments);

        mvc.perform(
                get("/api/cv/{cvId}/employment/", CV_ID)
                        .contextPath("/api"))
                .andExpect(status().isOk())
                .andExpect(content().json("[" +
                        "{'id':450,'cvId':" + CV_ID + ",'version':1, 'fromDate':'1994-08-31', 'untilDate':'1998-04-15', " +
                        "'company':'Waitrose', 'position':'Security Guard', " +
                        "'summary':'Responsible for patrolling the premises overnight and monitoring CCTV.'}," +
                        "{'id':451,'cvId':" + CV_ID + ",'version':2, 'fromDate':'1998-04-16', 'untilDate':'2002-08-01', " +
                        "'company':'Asda', 'position':'Customer Service Assistant', " +
                        "'summary':'Dealing with customers\\' queries and complaints.'}]"));
    }
}
