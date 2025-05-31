#ifndef LOGGING_H
#define LOGGING_H

#include <windows.h>

void DebugLog(const char *msg);
void CleanupLog();
void ShowErrorMessage(const char *action);

#endif // LOGGING_H
