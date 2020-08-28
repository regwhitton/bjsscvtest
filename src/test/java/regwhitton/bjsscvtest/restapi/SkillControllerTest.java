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
import regwhitton.bjsscvtest.model.Skill;
import regwhitton.bjsscvtest.service.SkillService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SkillController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SkillControllerTest {

    @MockBean
    private SkillService skillService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldCreateNewSkill() throws Exception {
        final long CV_ID = 123456789L;
        final long SKILL_ID = 987654321L;
        Skill skill = Skill.builder().skill("Postgres SQL").build();
        Skill createdSkill = skill.toBuilder()
                .id(SKILL_ID)
                .cv(Cv.builder().id(CV_ID).build())
                .build();
        given(skillService.create(CV_ID, skill)).willReturn(createdSkill);

        mvc.perform(
                post("/api/cv/{cvId}/skill/", CV_ID)
                        .contextPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"skill\":\"Postgres SQL\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/cv/" + CV_ID + "/skill/" + SKILL_ID))
                .andExpect(content().json(
                        "{'id':" + SKILL_ID + ", 'cvId':" + CV_ID + ", 'skill':'Postgres SQL'}"));
    }

    @Test
    public void shouldDeleteSkill() throws Exception {
        final long CV_ID = 123456789L;
        final long SKILL_ID = 987654321L;

        mvc.perform(
                delete("/api/cv/{cvId}/skill/{id}", CV_ID, SKILL_ID)
                        .contextPath("/api"))
                .andExpect(status().isNoContent());

        verify(skillService, times(1)).delete(CV_ID, SKILL_ID);
    }

    @Test
    public void shouldGetAllSkills() throws Exception {
        final long CV_ID = 123456789L;
        Cv cv = Cv.builder().id(CV_ID).build();
        List<Skill> skills = Lists.list(
                Skill.builder().id(101L).cv(cv).skill("Postgres SQL").build(),
                Skill.builder().id(102L).cv(cv).skill("Oracle SQL").build());
        given(skillService.fetchAll(CV_ID)).willReturn(skills);

        mvc.perform(
                get("/api/cv/{cvId}/skill/", CV_ID)
                        .contextPath("/api"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "[{'id':101,'cvId':" + CV_ID + ",'skill':'Postgres SQL'}," +
                                "{'id':102,'cvId':" + CV_ID + ",'skill':'Oracle SQL'}]"));
    }
}
