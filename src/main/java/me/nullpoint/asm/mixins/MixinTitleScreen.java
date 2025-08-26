package me.nullpoint.asm.mixins;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {

    private static final String MAPLE_CLIENT_TEXT = "Maple Client Loaded!";

    protected MixinTitleScreen() {
        super(Text.literal("dummy"));
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderMapleClientInfo(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // 计算文本绘制位置，现在增加了右侧边距（从2改为10）
        int textWidth = this.textRenderer.getWidth(MAPLE_CLIENT_TEXT);
        int x = this.width - textWidth - 10;
        int y = 2;

        int currentX = x;

        // 遍历字符串的每一个字符，并用不同的颜色绘制
        for (int i = 0; i < MAPLE_CLIENT_TEXT.length(); i++) {
            char character = MAPLE_CLIENT_TEXT.charAt(i);

            // 基于字符位置和时间，计算出平滑的RGB渐变色
            float hue = (System.currentTimeMillis() % 3000) / 3000.0f + i * 0.02f;
            int color = java.awt.Color.HSBtoRGB(hue, 0.8f, 1.0f);

            // 绘制单个字符
            context.drawTextWithShadow(this.textRenderer, String.valueOf(character), currentX, y, color);

            // 更新下一个字符的X坐标
            currentX += this.textRenderer.getWidth(String.valueOf(character));
        }
    }
}