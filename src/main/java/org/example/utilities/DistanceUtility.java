package org.example.utilities;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class DistanceUtility {
    private DistanceUtility() {
    }

    public static double calculate(List<Integer> point_A, List<Integer> point_B) {
        double x_component = Math.pow(point_B.get(0) - point_A.get(0), 2);
        double y_component = Math.pow(point_B.get(1) - point_A.get(1), 2);

        return Math.sqrt(x_component + y_component);
    }
}
