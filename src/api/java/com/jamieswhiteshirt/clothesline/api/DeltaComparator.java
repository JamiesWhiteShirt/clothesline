package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.Vec3i;

import java.util.Comparator;

public class DeltaComparator implements Comparator<Vec3i> {
    private static DeltaComparator ourInstance = new DeltaComparator();

    public static DeltaComparator getInstance() {
        return ourInstance;
    }

    private DeltaComparator() {
    }

    @Override
    public int compare(Vec3i a, Vec3i b) {
        // Are the XZ angle and XZ length the same? If so, compare Y
        if (a.getZ() == b.getZ() && a.getX() == b.getX()) return Integer.compare(a.getY(), b.getY());

        // Is A on the Y axis and B not?
        if (a.getZ() == 0 && a.getX() == 0) return -1;

        // Is B on the Y axis and A not?
        if (b.getZ() == 0 && b.getX() == 0) return 1;

        // The vectors are in one of two halves split across the XY plane
        int aHalf = (a.getZ() == 0 && a.getX() > 0) || a.getZ() > 0 ? 0 : 1;
        int bHalf = (b.getZ() == 0 && b.getX() > 0) || b.getZ() > 0 ? 0 : 1;

        // Are the vectors in different halves?
        int comp = Integer.compare(aHalf, bHalf);
        if (comp != 0) return comp;

        // Do the vectors have different XZ angles?
        comp = Integer.compare(b.getX() * a.getZ(), a.getX() * b.getZ());
        if (comp != 0) return comp;

        // Do the vectors have different XZ lengths?
        // Exploit that A.x / A.z == B.x / B.z
        comp = Integer.compare(Math.abs(a.getX()), Math.abs(b.getX()));
        if (comp != 0) return comp;

        // A.x and B.x were both zero
        return Integer.compare(Math.abs(a.getZ()), Math.abs(b.getZ()));
    }
}
