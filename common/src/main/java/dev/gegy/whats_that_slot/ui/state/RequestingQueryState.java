package dev.gegy.whats_that_slot.ui.state;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.gegy.whats_that_slot.query.SlotQuery;
import dev.gegy.whats_that_slot.ui.SlotQueryInput;
import dev.gegy.whats_that_slot.ui.draw.SlotQueryProgressBar;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;

public final class RequestingQueryState implements SlotQueryState {
    private static final Duration QUERY_TIME_PER_TICK = Duration.ofMillis(10);

    private final AbstractContainerScreen<?> screen;
    private final Slot slot;
    private final SlotQuery.Generator query;

    private final SlotQueryProgressBar progressBar;

    private int ticks;

    public RequestingQueryState(AbstractContainerScreen<?> screen, Slot slot) {
        this.screen = screen;
        this.slot = slot;
        this.query = SlotQuery.forSlot(slot);

        this.progressBar = new SlotQueryProgressBar(screen, slot);
    }

    @Override
    @Nonnull
    public SlotQueryState tick(@Nullable Slot focusedSlot, boolean requestingQuery) {
        var queryingSlot = requestingQuery ? focusedSlot : null;
        if (queryingSlot != this.slot) {
            return new IdleQueryState(this.screen);
        }

        this.query.advanceFor(QUERY_TIME_PER_TICK);

        if (++this.ticks >= SlotQueryInput.REQUEST_TICKS) {
            return new ActiveQueryState(this.screen, this.slot, this.query.build());
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
