package regwhitton.bjsscvtest.restapi;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public class UriUtils {

    private UriUtils() {
    }

    /**
     * Build {@link URI} that is relative to the servlet context.
     * <p>
     * Example: If this is called with the arguments "/entity/{id}" and 16, a
     * {@link URI} is returned to "/&lt;context&gt;/entity/16".
     * </p>
     *
     * @param path string that starts with "/" and includes "{id}".
     * @param id   to be placed in the path where "{id}" is found.
     */
    static public URI newResourceUri(String path, Object id) {
        String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(path)
                .buildAndExpand(id)
                .toUriString();
        return URI.create(uri);
    }
}
