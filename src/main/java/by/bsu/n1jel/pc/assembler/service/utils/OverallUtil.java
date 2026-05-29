package by.bsu.n1jel.pc.assembler.service.utils;

public class OverallUtil {
    private static final Integer PAGE_SIZE = 12;
    private static final String PROJECT_NAME = "PC ASSEMBLER";
    private static final Integer LATEST_OBJECT_SIZE = 4;
    private static final Integer MAX_IMAGES = 5;


    public static Integer getMaxImagesSize() {
        return MAX_IMAGES;
    }

    public static Integer getPageSize() {
        return PAGE_SIZE;
    }

    public static String getProjectName() {
        return PROJECT_NAME;
    }

    public static Integer getLatestObjectSize() {
        return LATEST_OBJECT_SIZE;
    }
}
