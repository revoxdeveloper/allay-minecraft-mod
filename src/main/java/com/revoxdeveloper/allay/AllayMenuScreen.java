package com.revoxdeveloper.allay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

public class AllayMenuScreen extends Screen {

    private static final int DARK_BG = 0xFF0A0E27;
    private static final int ACCENT_COLOR = 0xFF00D9FF;
    private static final int HOVER_COLOR = 0xFF00FFFF;
    private static final int TEXT_COLOR = 0xFFFFFFFF;

    private static final int MENU_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 40;
    private static final int BUTTON_WIDTH = 180;

    private int centerX;
    private int centerY;
    private float animationProgress = 0.0f;

    public AllayMenuScreen() {
        super(Text.literal("Allay Client Menu"));
    }

    @Override
    protected void init() {
        super.init();
        
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;

        // Menu Buttons
        this.addDrawableChild(new AllayButtonWidget(
                this.centerX - BUTTON_WIDTH / 2,
                this.centerY - 60,
                BUTTON_WIDTH,
                BUTTON_HEIGHT,
                Text.literal("Singleplayer"),
                button -> this.closeMenu()
        ));

        this.addDrawableChild(new AllayButtonWidget(
                this.centerX - BUTTON_WIDTH / 2,
                this.centerY,
                BUTTON_WIDTH,
                BUTTON_HEIGHT,
                Text.literal("Multiplayer"),
                button -> this.closeMenu()
        ));

        this.addDrawableChild(new AllayButtonWidget(
                this.centerX - BUTTON_WIDTH / 2,
                this.centerY + 60,
                BUTTON_WIDTH,
                BUTTON_HEIGHT,
                Text.literal("Settings"),
                button -> this.closeMenu()
        ));

        this.addDrawableChild(new AllayButtonWidget(
                this.centerX - BUTTON_WIDTH / 2,
                this.centerY + 120,
                BUTTON_WIDTH,
                BUTTON_HEIGHT,
                Text.literal("Quit Game"),
                button -> MinecraftClient.getInstance().stop()
        ));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        // Animate menu entrance
        this.animationProgress = Math.min(this.animationProgress + 0.1f, 1.0f);

        // Semi-transparent dark background
        context.fill(0, 0, this.width, this.height, 0x80000000);

        // Menu panel background
        int panelX = this.centerX - MENU_WIDTH / 2;
        int panelY = this.centerY - 120;
        int panelWidth = MENU_WIDTH;
        int panelHeight = 280;

        // Draw glowing border
        this.drawGlowingBorder(context, panelX, panelY, panelWidth, panelHeight);

        // Draw panel background
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, DARK_BG);

        // Draw title with glow effect
        this.drawGlowingText(context, "ALLAY CLIENT", this.centerX, panelY + 15, TEXT_COLOR);

        super.render(context, mouseX, mouseY, tickDelta);
    }

    private void drawGlowingBorder(DrawContext context, int x, int y, int width, int height) {
        // Top border with gradient glow
        for (int i = 0; i < 3; i++) {
            int alpha = 150 - (i * 50);
            int glowColor = (alpha << 24) | (ACCENT_COLOR & 0xFFFFFF);
            context.fill(x - i, y - i, x + width + i, y - i + 1, glowColor);
        }

        // Bottom border
        for (int i = 0; i < 3; i++) {
            int alpha = 150 - (i * 50);
            int glowColor = (alpha << 24) | (ACCENT_COLOR & 0xFFFFFF);
            context.fill(x - i, y + height + i - 1, x + width + i, y + height + i, glowColor);
        }

        // Left border
        context.fill(x - 2, y, x, y + height, 0xFF00D9FF);

        // Right border
        context.fill(x + width, y, x + width + 2, y + height, 0xFF00D9FF);
    }

    private void drawGlowingText(DrawContext context, String text, int x, int y, int color) {
        // Glow effect
        for (int i = 2; i > 0; i--) {
            int glowColor = (0x33 << 24) | (ACCENT_COLOR & 0xFFFFFF);
            context.drawCenteredTextWithShadow(this.textRenderer, text, x, y, glowColor);
        }
        // Main text
        context.drawCenteredTextWithShadow(this.textRenderer, text, x, y, color);
    }

    private void closeMenu() {
        this.client.setScreen(null);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void close() {
        super.close();
        this.client.setScreen(null);
    }

    private static class AllayButtonWidget extends ButtonWidget {

        private boolean hovered = false;
        private float hoverAnimation = 0.0f;

        public AllayButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float tickDelta) {
            this.hovered = mouseX >= this.getX() && mouseY >= this.getY() &&
                    mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

            // Animate hover
            this.hoverAnimation += this.hovered ? 0.1f : -0.1f;
            this.hoverAnimation = Math.max(0.0f, Math.min(1.0f, this.hoverAnimation));

            // Button background color interpolation
            int bgColor = this.interpolateColor(DARK_BG, 0xFF1A1F3A, this.hoverAnimation);

            // Draw button background
            context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, bgColor);

            // Draw border
            int borderColor = this.hovered ? HOVER_COLOR : ACCENT_COLOR;
            context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + 2, borderColor);
            context.fill(this.getX(), this.getY() + this.height - 2, this.getX() + this.width, this.getY() + this.height, borderColor);

            // Draw button text
            int textColor = this.hovered ? HOVER_COLOR : TEXT_COLOR;
            context.drawCenteredTextWithShadow(
                    MinecraftClient.getInstance().textRenderer,
                    this.getMessage(),
                    this.getX() + this.width / 2,
                    this.getY() + (this.height - 8) / 2,
                    textColor
            );
        }

        private int interpolateColor(int color1, int color2, float progress) {
            int r1 = (color1 >> 16) & 0xFF;
            int g1 = (color1 >> 8) & 0xFF;
            int b1 = color1 & 0xFF;

            int r2 = (color2 >> 16) & 0xFF;
            int g2 = (color2 >> 8) & 0xFF;
            int b2 = color2 & 0xFF;

            int r = (int) (r1 + (r2 - r1) * progress);
            int g = (int) (g1 + (g2 - g1) * progress);
            int b = (int) (b1 + (b2 - b1) * progress);

            return (r << 16) | (g << 8) | b;
        }
    }
}
