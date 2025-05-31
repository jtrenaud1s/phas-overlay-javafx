#include "logging.h"
#include <windows.h>
#include <stdio.h>
#include <stdlib.h>


// Function to initialize the launcher
void InitializeLauncher() {
    DebugLog("Launcher started.");
}

// Function to get the module file name
int GetModuleFileNameSafe(char *javawPath, size_t size) {
    if (GetModuleFileNameA(NULL, javawPath, (DWORD)size) == 0) {
        DebugLog("Failed to get module file name.");
        ShowErrorMessage("GetModuleFileNameA");
        return 0;
    }
    DebugLog("Got module file name.");
    DebugLog(javawPath);
    return 1;
}

// Function to strip the executable name from the path
int StripExecutableName(char *javawPath) {
    char *slash = strrchr(javawPath, '\\');
    if (slash) {
        *slash = '\0';
        DebugLog("Stripped executable name from path.");
        DebugLog(javawPath);
        return 1;
    } else {
        DebugLog("Failed to find backslash in path.");
        ShowErrorMessage("Path parsing");
        return 0;
    }
}

// Function to build the command line
void BuildCommandLine(char *cmdLine, const char *javawPath) {
    ZeroMemory(cmdLine, 1024);
    lstrcatA(cmdLine, "\"");
    lstrcatA(cmdLine, javawPath);
    lstrcatA(cmdLine, "\" -D\"jnativehook.lib.locator\"=me.jtrenaud1s.phas.overlay.util.JLibLocator");
    lstrcatA(cmdLine, " --module PhasOverlay/me.jtrenaud1s.phas.overlay.PhasOverlay");
    DebugLog("Built command line:");
    DebugLog(cmdLine);
}

// Function to launch the process
int LaunchProcess(const char *cmdLine) {
    STARTUPINFOA si;
    PROCESS_INFORMATION pi;
    ZeroMemory(&si, sizeof(si));
    si.cb = sizeof(si);
    ZeroMemory(&pi, sizeof(pi));

    DebugLog("Calling CreateProcessA...");
    if (!CreateProcessA(
            NULL,
            (LPSTR)cmdLine,
            NULL,
            NULL,
            FALSE,
            0,
            NULL,
            NULL,
            &si,
            &pi
        )) {
        DebugLog("CreateProcessA failed.");
        ShowErrorMessage("Launching Java application");
        return 0;
    }

    DebugLog("Process launched successfully.");
    CloseHandle(pi.hProcess);
    CloseHandle(pi.hThread);
    return 1;
}

int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow) {
    InitializeLauncher();

    char javawPath[MAX_PATH];
    if (!GetModuleFileNameSafe(javawPath, sizeof(javawPath))) {
        CleanupLog();
        return 1;
    }

    if (!StripExecutableName(javawPath)) {
        CleanupLog();
        return 1;
    }

    lstrcatA(javawPath, "\\bin\\javaw.exe");
    DebugLog("Appended \\bin\\javaw.exe:");
    DebugLog(javawPath);

    char cmdLine[1024];
    BuildCommandLine(cmdLine, javawPath);

    if (!LaunchProcess(cmdLine)) {
        CleanupLog();
        return 1;
    }

    CleanupLog();
    return 0;
}

