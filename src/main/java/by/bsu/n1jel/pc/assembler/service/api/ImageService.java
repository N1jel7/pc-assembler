package by.bsu.n1jel.pc.assembler.service.api;

import by.bsu.n1jel.pc.assembler.dto.request.image.DeleteImageRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.image.LoadImageRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.image.UploadImageRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ImageInfoResponseDto;

public interface ImageService {

    ImageInfoResponseDto delete(DeleteImageRequestDto request);

    ImageInfoResponseDto upload(UploadImageRequestDto request);

    String load(LoadImageRequestDto requestDto);
}
