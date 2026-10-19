package by.bsu.n1jel.pc.assembler.service.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class UploadUtil {

    /**
     * Браузер присылает пустую часть multipart, если файлы не выбраны, — такие отбрасываем.
     */
    public static List<MultipartFile> nonEmpty(List<MultipartFile> files) {
        if (files == null) {
            return List.of();
        }
        return files.stream().filter(file -> file != null && !file.isEmpty()).toList();
    }

    public static MultipartFile firstNonEmpty(List<MultipartFile> files) {
        return nonEmpty(files).stream().findFirst().orElse(null);
    }
}
