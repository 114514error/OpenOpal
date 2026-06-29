package wtf.opal.client.screen.click.dropdown.panel;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.KeyInput;
import wtf.opal.client.OpalClient;
import wtf.opal.client.feature.module.ModuleCategory;
import wtf.opal.client.renderer.NVGRenderer;
import wtf.opal.client.renderer.repository.FontRepository;
import wtf.opal.client.screen.click.OpalPanelComponent;
import wtf.opal.utility.misc.HoverUtility;
import wtf.opal.utility.render.ColorUtility;
import wtf.opal.utility.render.Scroller;
import wtf.opal.utility.render.animation.Animation;
import wtf.opal.utility.render.animation.Easing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static wtf.opal.client.Constants.mc;

public final class CategoryPanel extends OpalPanelComponent {

    private final Scroller scroller = new Scroller();
    private final ModuleCategory category;

    private Animation openAnimation;
    private final int panelIndex;

    private final boolean lastPanel;
    private boolean closing;

    private final List<ModulePanel> modulePanelList = new ArrayList<>();

    public CategoryPanel(final ModuleCategory category, final int panelIndex) {
        this.category = category;
        this.panelIndex = panelIndex;
        this.lastPanel = panelIndex == ModuleCategory.VALUES.length - 1;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        openAnimation.run(closing ? 0 : 1);

        if (lastPanel && openAnimation.isFinished() && closing) {
            mc.setScreen(null);
            return;
        }

        final float[] currentY = {y + height};
        final float totalHeight = getTotalHeight();

        final float openAnimationValue = openAnimation.getValue();
        final float scissorHeight = Math.min(mc.getWindow().getScaledHeight() - y, totalHeight * openAnimationValue);
        final float scrollOffset = scroller.getAnimation().getValue();

        NVGRenderer.globalAlpha(openAnimationValue);
        NVGRenderer.scissor(x, y, width, scissorHeight, () -> {
            NVGRenderer.roundedRectVarying(x, y + scrollOffset, width, height, 5, 5, 0, 0, NVGRenderer.BLUR_PAINT);
            NVGRenderer.roundedRectVarying(x, y + scrollOffset, width, height, 5, 5, 0, 0, ColorUtility.applyOpacity(0xff0f0f0f, 0.85F));
            FontRepository.getFont("productsans-bold").drawString(category.getName(), x + 5, y + scrollOffset + 13, 9, -1);
            FontRepository.getFont("materialicons-outlined").drawString(category.getIcon(), x + width - 15.5F, y + scrollOffset + 15, 10, -1);

            for (int i = 0; i < modulePanelList.size(); i++) {
                final ModulePanel panel = modulePanelList.get(i);
                float panelHeight = this.height + (panel.getExpandAnimation().getValue() * panel.getAddedHeight());

                panel.setDimensions(x, currentY[0] + scrollOffset, width, panelHeight);
                panel.setLastModule(i == modulePanelList.size() - 1);

                panel.render(context, mouseX, mouseY, delta);

                currentY[0] += panelHeight;
            }
        });
        NVGRenderer.globalAlpha(1);

        scroller.onScroll(getMaxOffset(totalHeight));
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        modulePanelList.forEach(modulePanel -> modulePanel.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public void keyPressed(KeyInput keyInput) {
        modulePanelList.forEach(modulePanel -> modulePanel.keyPressed(keyInput));
    }

    @Override
    public void charTyped(char chr, int modifiers) {
        modulePanelList.forEach(modulePanel -> modulePanel.charTyped(chr, modifiers));
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        modulePanelList.forEach(modulePanel -> modulePanel.mouseReleased(mouseX, mouseY, button));
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        final float totalHeight = getTotalHeight();

        if (HoverUtility.isHovering(x, y, width, totalHeight, mouseX, mouseY)) {
            scroller.addScroll(verticalAmount, getMaxOffset(totalHeight));
        }

        modulePanelList.forEach(modulePanel -> modulePanel.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount));
    }

    @Override
    public void init() {
        if (modulePanelList.isEmpty()) {
            OpalClient.getInstance().getModuleRepository().getModulesInCategory(category)
                    .forEach(module -> modulePanelList.add(new ModulePanel(module)));
            modulePanelList.sort(Comparator.comparing(p -> p.getModule().getName()));
        }

        this.openAnimation = new Animation(Easing.EASE_OUT_SINE, 100 + (panelIndex * 80L));
        closing = false;

        modulePanelList.forEach(ModulePanel::init);
    }

    @Override
    public void close() {
        closing = true;

        modulePanelList.forEach(ModulePanel::close);
    }

    private float getTotalHeight() {
        float totalHeight = this.height; // header height

        for (final ModulePanel panel : modulePanelList) {
            panel.getExpandAnimation().run(panel.isExpanded() ? 1 : 0);
            totalHeight += this.height + (panel.getExpandAnimation().getValue() * panel.getAddedHeight());
        }

        return totalHeight;
    }

    private float getMaxOffset(final float totalHeight) {
        final float relativeScreenHeight = mc.getWindow().getScaledHeight() - y;
        final float scissorHeight = Math.min(relativeScreenHeight, totalHeight * openAnimation.getValue());
        final float overflowPadding = scissorHeight == relativeScreenHeight ? y : 0;

        return Math.max(0, totalHeight - scissorHeight + overflowPadding);
    }

}
