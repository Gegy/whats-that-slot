package dev.gegy.whats_that_slot.ui.state;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.gegy.whats_that_slot.query.SlotQuery;
import dev.gegy.whats_that_slot.query.SlotQueryGenerator;
import dev.gegy.whats_that_slot.ui.SlotQueryInput;
import dev.gegy.whats_that_slot.ui.SlotQueryProgressBar;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;

public final class RequestingQueryState implements SlotQueryState {
    private static final Duration QUERY_TIME_PER_TICK = Duration.ofMillis(10);

    private final ContainerScreen<?> screen;
    private final Slot slot;
    private final SlotQueryGenerator query;

    private final SlotQueryProgressBar progressBar;

    private int ticks;

    public RequestingQueryState(ContainerScreen<?> screen, Slot slot) {
        this.screen = screen;
        this.slot = slot;
        this.query = SlotQuery.forSlot(screen, slot);

        this.progressBar = new SlotQueryProgressBar(screen, slot);
    }

    @Override
    @Nonnull
    public SlotQueryState tick(@Nullable Slot focusedSlot, boolean requestingQuery) {
        Slot queryingSlot = requestingQuery ? focusedSlot : null;
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
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        float queryProgress = this.getQueryProgress(delta);
        this.progressBar.draw(matrices, queryProgress);
    }

    private float getQueryProgress(float delta) {
        return Math.min(this.ticks + delta, SlotQueryInput.REQUEST_TICKS) / SlotQueryInput.REQUEST_TICKS;
    }
}
