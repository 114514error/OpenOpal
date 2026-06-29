package wtf.opal.client.renderer.repository;

import net.fabricmc.loader.impl.launch.knot.Knot;
import wtf.opal.client.renderer.text.NVGTextRenderer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FontRepository {

    private static final HashMap<String, NVGTextRenderer> TEXT_RENDERER_MAP = new HashMap<>();
    private static final String CJK_FALLBACK_NAME = "__opal_cjk_fallback__";
    private static final String[] WINDOWS_CJK_FALLBACK_PATHS = {
            "C:\\Windows\\Fonts\\msyh.ttc",
            "C:\\Windows\\Fonts\\msyh.ttf",
            "C:\\Windows\\Fonts\\simhei.ttf",
            "C:\\Windows\\Fonts\\simsun.ttc",
            "C:\\Windows\\Fonts\\simsun.ttf"
    };

    public static NVGTextRenderer getFont(final String name) {
        if (TEXT_RENDERER_MAP.containsKey(name)) {
            return TEXT_RENDERER_MAP.get(name);
        }

        final InputStream pathURL = Knot.getLauncher().getTargetClassLoader().getResourceAsStream("assets/opal/fonts/" + name + ".ttf");

        if (pathURL != null) {
            final NVGTextRenderer renderer = new NVGTextRenderer(name, pathURL);
            TEXT_RENDERER_MAP.put(name, renderer);

            if (!isIconFont(name)) {
                renderer.addFallback(getCjkFallbackFont());
            }

            return TEXT_RENDERER_MAP.get(name);
        }

        throw new RuntimeException("Font not found: " + name);
    }

    private static boolean isIconFont(final String name) {
        return name.startsWith("material");
    }

    private static NVGTextRenderer getCjkFallbackFont() {
        if (TEXT_RENDERER_MAP.containsKey(CJK_FALLBACK_NAME)) {
            return TEXT_RENDERER_MAP.get(CJK_FALLBACK_NAME);
        }

        for (final String pathString : WINDOWS_CJK_FALLBACK_PATHS) {
            final Path path = Path.of(pathString);
            if (!Files.exists(path)) {
                continue;
            }

            try (final InputStream inputStream = Files.newInputStream(path)) {
                final NVGTextRenderer fallbackRenderer = new NVGTextRenderer(CJK_FALLBACK_NAME, inputStream);
                TEXT_RENDERER_MAP.put(CJK_FALLBACK_NAME, fallbackRenderer);
                return fallbackRenderer;
            } catch (IOException ignored) {
            }
        }

        return null;
    }
}
