package by.bsu.n1jel.pc.assembler.service.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class DefaultImageUtil {
    public static String componentAvatar(String typeLabel, String name) {
        return svgAvatar(typeLabel, name, "#1a2434", "#0c1118", "#6ee7ff");
    }

    public static String producerAvatar(String name, String country) {
        return svgAvatar(name, country, "#1a2230", "#0c1016", "#8b5cf6");
    }

    public static String buildAvatar(String name) {
        return svgAvatar("BUILD", name, "#111827", "#090d13", "#22c55e");
    }

    public static String specTypeAvatar(String name) {
        return svgAvatar("SPEC", name, "#172033", "#0b1018", "#f59e0b");
    }

    private static String svgAvatar(String title, String subtitle, String a, String b, String accent) {
        // Разбиваем подзаголовок на строки
        String[] lines = splitIntoLines(subtitle, 19);

        StringBuilder subtitleSvg = new StringBuilder();
        int yStart = 210;
        int lineHeight = 72;

        for (int i = 0; i < lines.length; i++) {
            int y = yStart + (i * lineHeight);
            subtitleSvg.append(String.format(
                    "<text x=\"60\" y=\"%d\" fill=\"#ffffff\" font-size=\"72\" font-weight=\"700\" font-family=\"Inter,Arial,sans-serif\">%s</text>\n",
                    y, escapeXml(lines[i])
            ));
        }

        // Сдвигаем PC ASSEMBLER вниз, если есть перенос
        int pcAssemblerY = 210 + (lines.length - 1) * 72 + 58; // 58 - отступ

        String svg = String.format("""
        <svg xmlns="http://www.w3.org/2000/svg" width="900" height="600" viewBox="0 0 900 600">
          <defs>
            <linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
              <stop offset="0%%" stop-color="%s"/>
              <stop offset="100%%" stop-color="%s"/>
            </linearGradient>
          </defs>
          <rect width="900" height="600" rx="36" fill="url(#g)"/>
          <circle cx="720" cy="110" r="110" fill="%s" opacity=".12"/>
          <circle cx="120" cy="500" r="170" fill="%s" opacity=".08"/>
          <rect x="36" y="36" width="828" height="528" rx="26" fill="none" stroke="rgba(255,255,255,.14)" stroke-width="2"/>
          <text x="60" y="120" fill="rgba(255,255,255,.68)" font-size="30" font-family="Inter,Arial,sans-serif">%s</text>
          %s
          <text x="60" y="%d" fill="rgba(255,255,255,.72)" font-size="24" font-family="Inter,Arial,sans-serif">PC ASSEMBLER</text>
          <rect x="60" y="%d" width="220" height="10" rx="5" fill="%s" opacity=".9"/>
          <rect x="60" y="%d" width="140" height="10" rx="5" fill="rgba(255,255,255,.32)"/>
        </svg>
        """,
                a, b, accent, accent,
                escapeXml(title),
                subtitleSvg.toString(),
                pcAssemblerY,
                pcAssemblerY + 62,  // полоска 1
                accent,
                pcAssemblerY + 92   // полоска 2
        );

        String encoded = URLEncoder.encode(svg, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("%0A", "")
                .replace("%0D", "");

        return "data:image/svg+xml;charset=UTF-8," + encoded;
    }

    private static String[] splitIntoLines(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return new String[]{text};
        }

        java.util.List<String> lines = new java.util.ArrayList<>();
        String remaining = text;

        while (remaining.length() > maxLength) {
            // Ищем последний пробел в пределах maxLength
            int lastSpace = remaining.lastIndexOf(' ', maxLength);
            if (lastSpace == -1) {
                lastSpace = maxLength;
            }
            lines.add(remaining.substring(0, lastSpace));
            remaining = remaining.substring(lastSpace).trim();
        }
        lines.add(remaining);

        return lines.toArray(new String[0]);
    }

    private static String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}