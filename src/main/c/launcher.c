#include <windows.h>
#include <stdio.h>

void ShowErrorMessage(const char *action) {
    DWORD errorCode = GetLastError();
    char errorMessage[512];

    // Format the error message from the error code
    FormatMessageA(
        FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS,
        NULL,
        errorCode,
        0, // Default language
        errorMessage,
        sizeof(errorMessage),
        NULL
    );

    // Display the action, error code, and message
    char fullMessage[1024];
    snprintf(
        fullMessage,
        sizeof(fullMessage),
        "Action: %s\nError Code: %lu\nMessage: %s",
        action,
        errorCode,
        errorMessage
    );

    MessageBoxA(NULL, fullMessage, "Error", MB_OK | MB_ICONERROR);
}

int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow) {
    // Path to your jlink image's "javaw.exe"
    char javawPath[MAX_PATH];
    GetModuleFileNameA(NULL, javawPath, MAX_PATH);

    // Strip the launcher.exe name from the path, leaving the directory
    char *slash = strrchr(javawPath, '\\');
    if (slash) {
        *slash = '\0'; // remove launcher.exe from the path
    }

    // Append \bin\javaw.exe
    lstrcatA(javawPath, "\\bin\\javaw.exe");

    // Build the command line we want to run
    char cmdLine[1024];
    ZeroMemory(cmdLine, sizeof(cmdLine));
    lstrcatA(cmdLine, "\"");
    lstrcatA(cmdLine, javawPath);
    lstrcatA(cmdLine, "\" -D\"jnativehook.lib.locator\"=me.jtrenaud1s.phas.overlay.util.JLibLocator");
    lstrcatA(cmdLine, " --module PhasOverlay/me.jtrenaud1s.phas.overlay.Main");

    STARTUPINFOA si;
    PROCESS_INFORMATION pi;
    ZeroMemory(&si, sizeof(si));
    si.cb = sizeof(si);
    ZeroMemory(&pi, sizeof(pi));

    // CreateProcess with javaw.exe
    if (!CreateProcessA(
            NULL,        // lpApplicationName
            cmdLine,     // lpCommandLine
            NULL,        // lpProcessAttributes
            NULL,        // lpThreadAttributes
            FALSE,       // bInheritHandles
            0,           // dwCreationFlags
            NULL,        // lpEnvironment
            NULL,        // lpCurrentDirectory
            &si,         // lpStartupInfo
            &pi          // lpProcessInformation
        ))
    {
        // Show error details if CreateProcess fails
        ShowErrorMessage("Launching Java application");
        return 1;
    }

    // We don't need the thread or process handles
    CloseHandle(pi.hProcess);
    CloseHandle(pi.hThread);
    return 0;
}