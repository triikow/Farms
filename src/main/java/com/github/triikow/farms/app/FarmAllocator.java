package com.github.triikow.farms.app;

import com.github.triikow.farms.config.ConfigManager;
import com.github.triikow.farms.config.RuntimeConfig;
import com.github.triikow.farms.domain.FarmCenter;

public final class FarmAllocator {

    private final ConfigManager configManager;

    public FarmAllocator(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public FarmCenter centerForIndex(int index) {
        RuntimeConfig cfg = configManager.runtime();

        int spacing = cfg.spacing();
        int minSteps = (int) Math.ceil(cfg.spawnBuffer() / (double) spacing);
        if (minSteps < 0) minSteps = 0;

        GridPos grid = computeGridPos(index, Math.max(0, minSteps));

        int x = cfg.spawnX() + grid.i * spacing;
        int z = cfg.spawnZ() + grid.j * spacing;

        return new FarmCenter(x, z);
    }

    private record GridPos(int i, int j) {}

    // Deterministic ring-perimeter allocation (square rings), starting outside spawn buffer.
    private GridPos computeGridPos(int index, int minRing) {
        int n = index;

        int r = Math.max(0, minRing);
        int ringCount = 8 * r;

        while (n >= ringCount) {
            n -= ringCount;
            r++;
            ringCount = 8 * r;
        }

        if (r == 0) {
            // if spawn_buffer == 0, index 0 would land on spawn; still deterministic
            return new GridPos(0, 0);
        }

        int topLen = 2 * r + 1;
        int rightLen = 2 * r;
        int bottomLen = 2 * r;
        int leftLen = 2 * r - 1;

        int i, j;

        if (n < topLen) {
            i = -r + n;
            j = r;
        } else {
            n -= topLen;
            if (n < rightLen) {
                i = r;
                j = r - 1 - n;
            } else {
                n -= rightLen;
                if (n < bottomLen) {
                    i = r - 1 - n;
                    j = -r;
                } else {
                    n -= bottomLen;
                    if (n < 0 || n >= leftLen) {
                        // Should never happen if ringCount math stays consistent
                        i = -r;
                        j = -r + 1;
                    } else {
                        i = -r;
                        j = -r + 1 + n;
                    }
                }
            }
        }

        return new GridPos(i, j);
    }
}
