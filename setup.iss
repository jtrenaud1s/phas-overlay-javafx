[Setup]
AppName=PhasOverlay
AppVersion=1.2
DefaultDirName={commonpf}\PhasOverlay
DefaultGroupName=PhasOverlay
OutputBaseFilename=PhasOverlay-1.2-installer
OutputDir=target
Compression=lzma
SolidCompression=yes
Uninstallable=yes
WizardStyle=modern
UninstallDisplayIcon={app}\PhasOverlay.exe
DisableWelcomePage=no

[Files]
Source: "target\image\*"; DestDir: "{app}"; Flags: recursesubdirs createallsubdirs
Source: "target\PhasOverlay.exe"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
; Add a desktop shortcut for the application
Name: "{autodesktop}\PhasOverlay"; Filename: "{app}\PhasOverlay.exe";  Tasks: "desktop"
; Add an uninstall shortcut
Name: "{group}\Uninstall PhasOverlay"; Filename: "{uninstallexe}"
; Add a Start Menu entry
Name: "{group}\PhasOverlay"; Filename: "{app}\PhasOverlay.exe"; Tasks: "startmenu"

[Tasks]
; Allow the user to opt in/out of shortcuts
Name: "desktop"; Description: "Create a &Desktop shortcut"; GroupDescription: "Additional shortcuts:"
Name: "startmenu"; Description: "Create a &Start Menu shortcut"; GroupDescription: "Additional shortcuts:"
