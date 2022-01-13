package dev.gegy.whats_that_slot.ui.state;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.gegy.whats_that_slot.query.SlotQuery;
import dev.gegy.whats_that_slot.query.SlotQueryGenerator;
import dev.gegy.whats_that_slot.ui.SlotQueryInput;
import dev.gegy.whats_that_slot.ui.draw.SlotQueryProgressBar;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;

public final class RequestingQueryState implements SlotQueryState {
    private static final Duration QUERY_TIME_PER_TICK = Duration.ofMillis(10);

    private final Player player;
    private final AbstractContainerScreen<?> screen;
    private final Slot slot;
    private final SlotQueryGenerator query;

    private final SlotQueryProgressBar progressBar;

    private int ticks;

    public RequestingQueryState(Player player, AbstractContainerScreen<?> screen, Slot slot) {
        this.player = player;
        this.screen = screen;
        this.slot = slot;
        this.query = SlotQuery.forSlot(player, slot);

        this.progressBar = new SlotQueryProgressBar(screen, slot);
    }

    @Override
    @Nonnull
    public SlotQueryState tick(@Nullable Slot focusedSlot, boolean requestingQuery) {
        var queryingSlot = requestingQuery ? focusedSlot : null;
        if (queryingSlot != this.slot) {
            return new IdleQueryState(this.player, this.screen);
        }

        this.query.advanceFor(QUERY_TIME_PER_TICK);

        if (++this.ticks >= SlotQueryInput.REQUEST_TICKS) {
            return new ActiveQueryState(this.player, this.screen, this.slot, this.query.build());
        } else {
            return this;
        }
    }

    @Override
    public void draw(PoseStack matrices, int mouseX, int mouseY, float delta) {
        float queryProgress = this.getQueryProgress(delta);
        this.progressBar.draw(matrices, queryProgress);
    }

    private float getQueryProgress(float delta) {
        return Math.min(this.ticks + delta, SlotQueryInput.REQUEST_TICKS) / SlotQueryInput.REQUEST_TICKS;
    }
}
