package org.mule.galaxy.test;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.jackrabbit.core.fs.mem.MemoryFileSystem;
import org.springframework.util.ReflectionUtils;

public class ClearableMemoryFileSystem extends MemoryFileSystem {

    @Override
    public void close() {
        try {
            Field field = MemoryFileSystem.class.getDeclaredField("entries");
            ReflectionUtils.makeAccessible(field);
            Map entries = (Map) field.get(this);
            entries.clear();
        } catch (Exception e) {
            throw new RuntimeException();
        }
        super.close();
    }

}
