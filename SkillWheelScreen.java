package com.thunderaura.client;

import com.thunderaura.network.SelectSkillPacket;
import com.thunderaura.network.UseSkillPacket;
import com.thunderaura.skill.SkillType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Simple radial skill wheel.
 * Hold the Open Wheel key, move mouse to a sector, release or click to select + use.
 */
public class SkillWheelScreen extends Screen {
    private static final SkillType[] SKILLS = {
            SkillType.AURA,
            SkillType.STRIKE,
            SkillType.DASH
            // SkillType.CHAIN when implemented
    };

    private int hoveredIndex = -1;

    public SkillWheelScreen() {
        super(Component.translatable("screen.thunderaura.skill_wheel"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Semi-transparent background
        graphics.fill(0, 0, this.width, this.height, 0x66000000);

        int cx = this.width / 2;
        int cy = this.height / 2;
        int radius = 90;
        int inner = 28;

        double angle = Math.atan2(mouseY - cy, mouseX - cx);
        if (angle < 0) angle += Math.PI * 2;

        int segments = SKILLS.length;
        double segAngle = (Math.PI * 2) / segments;
        hoveredIndex = (int) (angle / segAngle) % segments;

        // Draw segments
        for (int i = 0; i < segments; i++) {
            double start = i * segAngle - Math.PI / 2; // start from top
            double end = start + segAngle;
            boolean hover = i == hoveredIndex;

            int color = hover ? 0xAA00E5FF : 0x88003355;
            drawSegment(graphics, cx, cy, inner, radius, start, end, color);

            // Label
            double mid = (start + end) / 2;
            int lx = cx + (int) (Math.cos(mid) * (radius * 0.65));
            int ly = cy + (int) (Math.sin(mid) * (radius * 0.65));
            String name = SKILLS[i].getDisplayName().getString();
            int tw = this.font.width(name);
            graphics.drawString(this.font, name, lx - tw / 2, ly - 4, hover ? 0xFFFFFF : 0xA0E0FF, true);
        }

        // Center text
        String center = hoveredIndex >= 0 ? SKILLS[hoveredIndex].getDisplayName().getString() : "Thunder";
        int tw = this.font.width(center);
        graphics.drawString(this.font, center, cx - tw / 2, cy - 4, 0xFFFFFF, true);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void drawSegment(GuiGraphics g, int cx, int cy, int inner, int outer, double start, double end, int argb) {
        // Approximate arc with triangles (simple, works for few segments)
        int steps = 12;
        for (int s = 0; s < steps; s++) {
            double a0 = start + (end - start) * s / steps;
            double a1 = start + (end - start) * (s + 1) / steps;
            int x0 = cx + (int) (Math.cos(a0) * outer);
            int y0 = cy + (int) (Math.sin(a0) * outer);
            int x1 = cx + (int) (Math.cos(a1) * outer);
            int y1 = cy + (int) (Math.sin(a1) * outer);
            int ix0 = cx + (int) (Math.cos(a0) * inner);
            int iy0 = cy + (int) (Math.sin(a0) * inner);
            int ix1 = cx + (int) (Math.cos(a1) * inner);
            int iy1 = cy + (int) (Math.sin(a1) * inner);

            // Two triangles per step
            fillTriangle(g, cx, cy, x0, y0, x1, y1, argb); // rough, better with proper fill later
            // Use fill for a simpler ring look
        }
        // Simpler visual: just colored arcs via lines / circles approximation
        // For readability we already have labels; full geometry can be improved later.
    }

    private void fillTriangle(GuiGraphics g, int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        // Very rough approximation – real production code would use BufferBuilder
        g.fill((x1 + x2 + x3) / 3 - 2, (y1 + y2 + y3) / 3 - 2,
                (x1 + x2 + x3) / 3 + 2, (y1 + y2 + y3) / 3 + 2, color);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hoveredIndex >= 0 && hoveredIndex < SKILLS.length) {
            SkillType chosen = SKILLS[hoveredIndex];
            PacketDistributor.sendToServer(new SelectSkillPacket(chosen));
            PacketDistributor.sendToServer(new UseSkillPacket(chosen));
            this.onClose();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Close if the open-wheel key is released is handled in ClientEvents
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
