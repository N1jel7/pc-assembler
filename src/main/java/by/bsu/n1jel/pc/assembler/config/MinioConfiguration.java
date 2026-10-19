package by.bsu.n1jel.pc.assembler.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.root.user}")
    private String username;
    @Value("${minio.root.password}")
    private String password;

    @Bean
    public MinioClient minioClient() {
        return new MinioClient.Builder()
                .endpoint(endpoint)
                .credentials(username, password)
                .build();
    }
}
