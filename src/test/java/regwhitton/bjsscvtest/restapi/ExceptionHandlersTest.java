package regwhitton.bjsscvtest.restapi;

import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import regwhitton.bjsscvtest.service.NotFoundException;
import regwhitton.bjsscvtest.service.ServiceException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.validation.Path.Node;
import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.IMAGE_JPEG;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExceptionThrowingController.class)
@AutoConfigureMockMvc
public class ExceptionHandlersTest {

    @Autowired
    private ExceptionThrowingController exceptionThrowingController;

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldSendBadRequest_onValidationException() throws Exception {
        mvc.perform(
                put("/entity/1")
                        .contentType(APPLICATION_JSON)
                        .content("{\"invalidIfNotBlank\":\"not blank\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{'error':'invalidIfNotBlank must match \"^$\"'}"));
    }

    @Test
    public void shouldSendNotFound_onNotFoundException() throws Exception {
        exceptionThrowingController.setThrowable(new NotFoundException());

        mvc.perform(
                put("/entity/34")
                        .contentType(APPLICATION_JSON)
                        .content("{\"aField\":\"aValue\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{'error':'not found'}"));
    }

    @Test
    public void shouldSendUnprocessableEntity_onOtherServiceException() throws Exception {
        exceptionThrowingController.setThrowable(new ServiceException("Account contains insufficient funds"));

        mvc.perform(
                put("/entity/1")
                        .contentType(APPLICATION_JSON)
                        .content("{\"aField\":\"aValue\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().json("{'error':'Account contains insufficient funds'}"));
    }

    @Test
    public void shouldSendBadRequest_onCorruptJsonBody() throws Exception {
        mvc.perform(
                put("/entity/1")
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\":1, "))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{'error':'Bad incoming request - please check your JSON'}"));
    }

    @Test
    public void shouldSendBadRequest_onCorruptParameters() throws Exception {
        mvc.perform(get("/entity/notANumber"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{'error':\"Cannot convert 'id' parameter from value 'notANumber'\"}"));
    }

    @Test
    public void shouldSendBadRequest_onDatabaseConstraintException() throws Exception {
        Set<ConstraintViolation<?>> violations = Sets.newLinkedHashSet(
                mockConstraintViolation("must not be blank",
                        mockPathNode(ElementKind.BEAN, "entityService"),
                        mockPathNode(ElementKind.METHOD, "createEntity"),
                        mockPathNode(ElementKind.PARAMETER, "entity"),
                        mockPathNode(ElementKind.PROPERTY, "aField")));

        exceptionThrowingController.setThrowable(new ConstraintViolationException("bad values", violations));

        mvc.perform(
                put("/entity/2")
                        .contentType(APPLICATION_JSON)
                        .content("{\"aField\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{'error':'entity.aField must not be blank'}"));
    }

    private ConstraintViolation<?> mockConstraintViolation(String message, Node... pathNodes) {
        List<Node> nodes = Lists.newArrayList(pathNodes);
        Path propertyPath = Mockito.mock(Path.class);
        given(propertyPath.iterator()).willReturn(nodes.iterator());

        ConstraintViolation<?> cv = Mockito.mock(ConstraintViolation.class);
        given(cv.getPropertyPath()).willReturn(propertyPath);
        given(cv.getMessage()).willReturn(message);
        return cv;
    }

    private Node mockPathNode(ElementKind kind, String name) {
        Node node = Mockito.mock(Node.class);
        given(node.getKind()).willReturn(kind);
        given(node.getName()).willReturn(name);
        return node;
    }

    @Test
    public void shouldSend500AndBlandMessage_onUnexpectedErrors() throws Exception {
        exceptionThrowingController.setThrowable(new RuntimeException("Message user should not see"));

        mvc.perform(
                put("/entity/2")
                        .contentType(APPLICATION_JSON)
                        .content("{\"aField\":\"aValue\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("{'error':'Internal server error'}"));
    }

    @Test
    public void shouldSendUnsupportedMedia_onUnknownMediaType() throws Exception {
        mvc.perform(
                put("/entity/1")
                        .contentType(IMAGE_JPEG)
                        .content("Unknown type of content"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().json("{'error':'Content type \\'image/jpeg\\' not supported'}"));
    }
}