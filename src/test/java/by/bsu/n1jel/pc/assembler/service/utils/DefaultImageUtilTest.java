package by.bsu.n1jel.pc.assembler.service.utils;

import org.junit.jupiter.api.Test;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultImageUtilTest {

    private static final String PREFIX = "data:image/svg+xml;charset=UTF-8,";

    private static String decode(String dataUri) {
        assertThat(dataUri).startsWith(PREFIX);
        return URLDecoder.decode(dataUri.substring(PREFIX.length()), StandardCharsets.UTF_8);
    }

    @Test
    void producesValidSvgDataUri() {
        String svg = decode(DefaultImageUtil.buildAvatar("Игровой ПК"));

        assertThat(svg).startsWith("<svg").endsWith("</svg>");
        assertThat(svg).contains("BUILD", "Игровой ПК", "PC ASSEMBLER");
    }

    @Test
    void escapesXmlInUserInput() {
        String svg = decode(DefaultImageUtil.producerAvatar("A&B <script>", "\"x\""));

        assertThat(svg).doesNotContain("<script>");
        assertThat(svg).contains("A&amp;B &lt;script&gt;", "&quot;x&quot;");
    }

    @Test
    void longSubtitleIsSplitIntoSeveralLines() {
        String shortSvg = decode(DefaultImageUtil.componentAvatar("CPU", "Ryzen 5"));
        String longSvg = decode(DefaultImageUtil.componentAvatar("CPU", "AMD Ryzen 9 7950X3D Sixteen Core Processor"));

        assertThat(countTextNodes(longSvg)).isGreaterThan(countTextNodes(shortSvg));
    }

    @Test
    void nullValuesDoNotBreakGeneration() {
        assertThat(decode(DefaultImageUtil.specTypeAvatar(null))).contains("SPEC");
        assertThat(decode(DefaultImageUtil.producerAvatar("Intel", null))).contains("Intel");
    }

    private static int countTextNodes(String svg) {
        return svg.split("<text").length - 1;
    }
}
