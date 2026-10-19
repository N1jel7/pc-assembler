package by.bsu.n1jel.pc.assembler.web;

import by.bsu.n1jel.pc.assembler.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.ByteArrayOutputStream;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Через настоящий Tomcat: MockMvc не применяет лимиты spring.servlet.multipart.
 * Пользователь должен увидеть понятное сообщение, а не «соединение сброшено» или белую страницу.
 */
class UploadErrorsTest extends AbstractIntegrationTest {

    private static final int MB = 1024 * 1024;

    @LocalServerPort
    private int port;

    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(new CookieManager())
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    private record FilePart(String filename, String contentType, int size) {
    }

    private HttpResponse<String> postMultipart(String path, String referer, Map<String, String> fields,
                                               List<FilePart> files) throws Exception {
        String boundary = "----test" + UUID.randomUUID();
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        for (Map.Entry<String, String> field : fields.entrySet()) {
            body.write(("--" + boundary + "\r\nContent-Disposition: form-data; name=\"" + field.getKey() + "\"\r\n\r\n"
                    + field.getValue() + "\r\n").getBytes(StandardCharsets.UTF_8));
        }
        for (FilePart file : files) {
            body.write(("--" + boundary + "\r\nContent-Disposition: form-data; name=\"images\"; filename=\""
                    + file.filename() + "\"\r\nContent-Type: " + file.contentType() + "\r\n\r\n")
                    .getBytes(StandardCharsets.UTF_8));
            body.write(new byte[file.size()]);
            body.write("\r\n".getBytes(StandardCharsets.UTF_8));
        }
        body.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()));
        if (referer != null) {
            request.header("Referer", referer);
        }
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    private String getPage(String location) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + location)).build(),
                HttpResponse.BodyHandlers.ofString()).body();
    }

    private Map<String, String> componentFields() throws Exception {
        Long producerId = createProducer(unique("Producer"), "США");
        return Map.of(
                "name", unique("GPU"),
                "producerId", producerId.toString(),
                "componentTypeId", seededComponentTypeId("Видеокарта").toString(),
                "price", "100",
                "stockQuantity", "1");
    }

    @Test
    void tooBigFileInFormRedirectsBackWithMessage() throws Exception {
        String referer = "http://localhost:" + port + "/components/create";

        HttpResponse<String> response = postMultipart("/components/create", referer, componentFields(),
                List.of(new FilePart("big.png", "image/png", 6 * MB)));

        assertThat(response.statusCode()).isEqualTo(303);
        String location = response.headers().firstValue("Location").orElseThrow();
        assertThat(location).isEqualTo("/components/create");

        String page = getPage(location);
        assertThat(page).contains("alert--error", "Файлы слишком большие", "5 МБ", "30 МБ");
        // сообщение показывается один раз
        assertThat(getPage(location)).doesNotContain("Файлы слишком большие");
    }

    @Test
    void tooBigRequestInFormRedirectsBackWithMessage() throws Exception {
        // каждый файл в лимите, но вместе больше 30 МБ
        List<FilePart> files = List.of(
                new FilePart("1.png", "image/png", 4 * MB + MB / 2),
                new FilePart("2.png", "image/png", 4 * MB + MB / 2),
                new FilePart("3.png", "image/png", 4 * MB + MB / 2),
                new FilePart("4.png", "image/png", 4 * MB + MB / 2),
                new FilePart("5.png", "image/png", 4 * MB + MB / 2),
                new FilePart("6.png", "image/png", 4 * MB + MB / 2),
                new FilePart("7.png", "image/png", 4 * MB + MB / 2));

        HttpResponse<String> response = postMultipart("/components/create",
                "http://localhost:" + port + "/components/create", componentFields(), files);

        assertThat(response.statusCode()).isEqualTo(303);
        assertThat(getPage(response.headers().firstValue("Location").orElseThrow()))
                .contains("Файлы слишком большие");
    }

    @Test
    void wrongFileTypeInFormRedirectsBackWithMessage() throws Exception {
        HttpResponse<String> response = postMultipart("/components/create",
                "http://localhost:" + port + "/components/create", componentFields(),
                List.of(new FilePart("doc.pdf", "application/pdf", 1024)));

        assertThat(response.statusCode()).isEqualTo(303);
        assertThat(getPage(response.headers().firstValue("Location").orElseThrow()))
                .contains("Можно загружать только изображения");
    }

    @Test
    void apiGetsJsonError() throws Exception {
        HttpResponse<String> response = postMultipart("/api/v1/images/upload", null,
                Map.of("objectId", "1", "objectType", "COMPONENT"),
                List.of(new FilePart("big.png", "image/png", 6 * MB)));

        assertThat(response.statusCode()).isEqualTo(413);
        assertThat(response.body()).contains("\"status\":413", "Файлы слишком большие");
    }

    @Test
    void redirectStaysOnThisSite() throws Exception {
        HttpResponse<String> foreign = postMultipart("/components/create",
                "https://evil.example/components/create?x=1", componentFields(),
                List.of(new FilePart("big.png", "image/png", 6 * MB)));
        HttpResponse<String> protocolRelative = postMultipart("/components/create",
                "https://evil.example//evil.example/path", componentFields(),
                List.of(new FilePart("big.png", "image/png", 6 * MB)));
        HttpResponse<String> noReferer = postMultipart("/components/create", null, componentFields(),
                List.of(new FilePart("big.png", "image/png", 6 * MB)));

        assertThat(foreign.headers().firstValue("Location")).hasValue("/components/create?x=1");
        assertThat(protocolRelative.headers().firstValue("Location")).hasValue("/");
        assertThat(noReferer.headers().firstValue("Location")).hasValue("/");
    }
}
