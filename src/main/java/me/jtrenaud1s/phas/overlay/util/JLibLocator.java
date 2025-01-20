package me.jtrenaud1s.phas.overlay.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import com.github.kwhat.jnativehook.NativeLibraryLocator;
import com.github.kwhat.jnativehook.NativeSystem;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is used to locate the native libraries.
 */
@Slf4j
public class JLibLocator implements NativeLibraryLocator {
    /**
     * Locates the native libraries.
     */
    @Override
    public Iterator<File> getLibraries() {
        var libs = new ArrayList<File>(1);
        var os = NativeSystem.getFamily().toString().toLowerCase();
        var arch = NativeSystem.getArchitecture().toString().toLowerCase();
        var jhome = System.getProperty("java.home");
        var libName = System.mapLibraryName("JNativeHook");
        var lib = jhome + File.separator + "bin" + File.separator + "native-libs" + File.separator + os + File.separator + arch + File.separator + libName;
        var libFile = new File(lib);
        log.info("JNativeHook found: {}", libFile.getAbsolutePath());

        libs.add(libFile);

        return libs.iterator();
    }
}