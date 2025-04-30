package org.example.utilities;

import org.springframework.stereotype.Component;

@Component
public final class DistanceUtility {
    private DistanceUtility() {
    }

    public static double calculate(int point_A_x, int point_A_y, int point_B_x, int point_B_y) {
        double x_component = Math.pow(point_B_x - point_A_x, 2);
        double y_component = Math.pow(point_B_y - point_A_y, 2);

        return Math.sqrt(x_component + y_component);
    }
}
