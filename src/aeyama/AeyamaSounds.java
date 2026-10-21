package aeyama;

import arc.audio.*;
import arc.util.*;

import mindustry.gen.*;

import java.lang.reflect.*;

/**
 * Mindustry v8 renamed most sound effects (and removed some block sound fields).
 * Sounds are looked up by name at runtime, so a rename can never break compilation or class loading:
 * exact name first, then a name that contains it, then the silent "none" sound.
 */
public class AeyamaSounds {

    public static Sound get(String... names) {
        for (String name : names) {
            try {
                Object o = Sounds.class.getField(name).get(null);
                if (o instanceof Sound s) return s;
            } catch (Throwable ignored) {}
        }
        for (String name : names) {
            for (Field f : Sounds.class.getFields()) {
                try {
                    if (Modifier.isStatic(f.getModifiers()) && Sound.class.isAssignableFrom(f.getType())
                        && f.getName().toLowerCase().contains(name.toLowerCase())) {
                        Object o = f.get(null);
                        if (o instanceof Sound s) return s;
                    }
                } catch (Throwable ignored) {}
            }
        }
        try {
            Object o = Sounds.class.getField("none").get(null);
            if (o instanceof Sound s) return s;
        } catch (Throwable ignored) {}
        for (Field f : Sounds.class.getFields()) {
            try {
                if (Modifier.isStatic(f.getModifiers()) && Sound.class.isAssignableFrom(f.getType())) {
                    Object o = f.get(null);
                    if (o instanceof Sound s) return s;
                }
            } catch (Throwable ignored) {}
        }
        Log.warn("[Aeyama] No sound found for @", String.join("/", names));
        return null;
    }

    /** Sets Block.ambientSound if this game version still has that field, otherwise does nothing. */
    public static void ambient(Object block, String... names) {
        try {
            Sound s = get(names);
            if (s != null) block.getClass().getField("ambientSound").set(block, s);
        } catch (Throwable ignored) {}
    }
}
