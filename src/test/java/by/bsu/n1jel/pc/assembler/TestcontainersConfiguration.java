package by.bsu.n1jel.pc.assembler;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Настоящие Postgres и MinIO в Docker — те же образы, что и в docker-compose.
 * H2 не подходит: в миграциях есть plpgsql-функция и нативные запросы.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgresContainer() {
        return new PostgreSQLContainer(DockerImageName.parse("postgres:18"));
    }

    @Bean
    MinIOContainer minioContainer() {
        // minio/minio больше не публикуется на Docker Hub — используем community-форк
        return new MinIOContainer(DockerImageName.parse("pgsty/minio:RELEASE.2026-08-04T00-00-00Z")
                .asCompatibleSubstituteFor("minio/minio"));
    }

    @Bean
    DynamicPropertyRegistrar minioProperties(MinIOContainer minio) {
        return registry -> {
            registry.add("minio.endpoint", minio::getS3URL);
            registry.add("minio.root.user", minio::getUserName);
            registry.add("minio.root.password", minio::getPassword);
        };
    }
}
