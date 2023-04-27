package io.github.jacg311.config;

import com.google.common.base.CaseFormat;
import io.github.jacg311.DebugDelights;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import java.lang.reflect.Field;

public class ConfigScreen extends Screen {
    protected ConfigScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        GridWidget.Adder adder = new GridWidget().setColumnSpacing(10).createAdder(2);
        for (Field field : DebugDelights.CONFIG.getClass().getDeclaredFields()) {
            Text name = Text.translatable(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()));
            adder.add(new TextWidget(name, MinecraftClient.getInstance().textRenderer));
            try {
                adder.add(new CheckboxWidget(0, 0, 50, 50, name, field.getBoolean(DebugDelights.CONFIG)) {
                    @Override
                    public void onPress() {
                        super.onPress();
                        try {
                            field.set(DebugDelights.CONFIG, this.isChecked());
                        }
                        catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
