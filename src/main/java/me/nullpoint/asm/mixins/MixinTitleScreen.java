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

    // 假设你的屏幕宽度和高度字段也是 protected
    // private static final Text MAPLE_CLIENT_TEXT = Text.literal("Maple Client Loaded!");

    protected MixinTitleScreen() {
        super(Text.literal("dummy"));
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderMapleClientInfo(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // 由于 Mixin 是一个抽象类并继承了 Screen，
        // 我们现在可以像在 TitleScreen 类中一样直接访问 protected 字段。
        int textWidth = this.textRenderer.getWidth(Text.literal("Maple Client Loaded!"));

        // 计算文本绘制位置
        int x = 2;
        int y = this.height - this.textRenderer.fontHeight - 2;

        // 绘制带阴影的文本
        context.drawTextWithShadow(this.textRenderer, Text.literal("Maple Client Loaded!"), x, y, 0xFFFFFFFF);
    }
}