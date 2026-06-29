package wtf.opal.client;

import net.minecraft.client.MinecraftClient;
import wtf.opal.client.renderer.NVGRenderer;

import java.io.File;

public final class Constants {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final long VG = NVGRenderer.getContext();
    public static final File DIRECTORY = new File(mc.runDirectory, File.separator + "opal" + File.separator);

    public static final double FIRST_FALL_MOTION = 0.0784000015258789D;
}
