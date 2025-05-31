#include "logging.h"
#include <stdio.h>
#include <time.h>
#include <windows.h>

FILE *logFile = NULL;

void DebugLog(const char *msg) {
    static int initialized = 0;
    static HANDLE hConsole = NULL;

    if (!initialized) {
        initialized = 1;

        // Try to get existing console window
        if (GetConsoleWindow()) {
            hConsole = GetStdHandle(STD_OUTPUT_HANDLE);
        } else {
            // Try to attach to parent console (if launched from console)
            if (AttachConsole(ATTACH_PARENT_PROCESS)) {
                hConsole = GetStdHandle(STD_OUTPUT_HANDLE);
            }
        }

        // Open log file if not already opened
        if (!logFile) {
            if (fopen_s(&logFile, "launcher.log", "a") != 0) {
                fopen_s(&logFile, "launcher.log", "w"); // Attempt to create the file if it doesn't exist
            }
        }
    }

    // Get the current timestamp
    time_t now = time(NULL);
    struct tm localTime;
    localtime_s(&localTime, &now);

    char timestamp[20];
    strftime(timestamp, sizeof(timestamp), "%Y-%m-%d %H:%M:%S", &localTime);

    // Log to console if available
    if (hConsole && hConsole != INVALID_HANDLE_VALUE) {
        DWORD written;
        char consoleMsg[1024];
        snprintf(consoleMsg, sizeof(consoleMsg), "[%s] %s", timestamp, msg);
        WriteConsoleA(hConsole, consoleMsg, (DWORD)strlen(consoleMsg), &written, NULL);
        WriteConsoleA(hConsole, "\r\n", 2, &written, NULL);
    }

    // Log to file if available
    if (logFile) {
        fprintf(logFile, "[%s] %s\n", timestamp, msg);
        fflush(logFile);
    }
}

void CleanupLog() {
    if (logFile) {
        fclose(logFile);
        logFile = NULL;
    }
}

void ShowErrorMessage(const char *action) {
    DWORD errorCode = GetLastError();
    char errorMessage[512];

    FormatMessageA(
        FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS,
        NULL,
        errorCode,
        0,
        errorMessage,
        sizeof(errorMessage),
        NULL
    );

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

