package by.bsu.n1jel.pc.assembler.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

    private static final String ENDPOINT = "http://localhost:9000";
    @Value("${minio.root.user}")
    private String username;
    @Value("${minio.root.password}")
    private String password;

    @Bean
    public MinioClient minioClient() {
        return new MinioClient.Builder()
                .endpoint(ENDPOINT)
                .credentials(username, password)
                .build();
    }
}
