package by.bsu.n1jel.pc.assembler.dto.response;

public record ImageContentResponseDto(
        byte[] bytes,
        String contentType
) {
}
