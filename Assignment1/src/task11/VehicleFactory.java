package task11;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/** Registry-based factory: new vehicle types are REGISTERED, the factory is never edited. */
public final class VehicleFactory {
    private static final Map<String, Function<String, Vehicle>> REGISTRY = new LinkedHashMap<>();
    static {
        register("TWOWHEELER", TwoWheeler::new);
        register("CAR", Car::new);
        register("EV", ElectricCar::new);
    }
    private VehicleFactory() { }
    public static void register(String type, Function<String, Vehicle> maker) { REGISTRY.put(type.toUpperCase(), maker); }
    public static Vehicle create(String type, String regNo) {
        Function<String, Vehicle> maker = type == null ? null : REGISTRY.get(type.trim().toUpperCase());
        if (maker == null) throw new IllegalArgumentException("Unknown vehicle type: " + type);
        return maker.apply(regNo);
    }
}
