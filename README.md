# PhasOverlay

A JavaFX-based overlay for the game **Phasmophobia**, providing convenient features like a **smudge timer**, **crosshair**, and customizable **keybinds**. This overlay runs as a transparent window on top of Phasmophobia, helping you track events in the game without switching windows.

---

## Table of Contents

- [Features](#features)
- [Installation & Running](#installation--running)
- [Keybinds](#keybinds)
- [Settings](#settings)
  - [General Settings](#general-settings)
  - [Overlay Settings](#overlay-settings)
  - [Application Settings](#application-settings)
- [Using the Overlay](#using-the-overlay)
- [FAQ / Troubleshooting](#faq--troubleshooting)
- [Contributing](#contributing)
- [License](#license)

---

## Features

- **Smudge Timer**  
  - Counts down (or up) from 3 minutes (180s) by default.  
  - Highlights critical ghost “hunt start” thresholds (e.g., 120s for Demon, 90s Standard).  
  - Plays optional audio cues at certain time points.

- **Crosshair**  
  - Toggle a simple dot crosshair at the center of the screen for more precise aiming (e.g., throwing items).

- **Global Keybinds**  
  - Uses JNativeHook to capture keyboard/mouse input globally (even if Phasmophobia is the active window).  
  - Default keybinds for toggling the overlay, starting/stopping the smudge timer, toggling settings, etc.

- **Settings Window**  
  - Customize your keybinds via an in-game UI.  
  - Adjust smudge timer volume, crosshair, default overlay visibility, and more.  
  - Changes automatically save to a JSON file.

- **Overlay Position Reset**  
  - Attempt to detect the Phasmophobia window and resize/move the overlay automatically.

---

## Installation & Running

1. **Download and install** the **PhasOverlay Installer** (`PhasOverlay-x.x.x-installer.exe`) from the latest [GitHub Release](../../releases).  
2. **Run** the installer. It places a “PhasOverlay” folder in `C:\Program Files (x86)\PhasOverlay` (by default) and creates shortcuts if you choose.
3. **Start** Phasmophobia (optional: you can start it first or second).
4. **Run** `PhasOverlay.exe` from your Start Menu or desktop shortcut.  
5. The overlay appears automatically and attempts to size itself to your Phasmophobia window.

> **Note**: The overlay is currently **Windows-only** (due to native hooks and window layering).

---

## Keybinds

By default, these are the **actions** and **keys** (defined in `src/main/java/me/jtrenaud1s/phas/overlay/model/SettingsModel.java`):

| Action                    | Default Keys                    | Description                                           |
|---------------------------|---------------------------------|-------------------------------------------------------|
| **Toggle Overlay**        | <kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>A</kbd> | Shows/hides the overlay window entirely              |
| **Start/Reset Smudge**    | <kbd>Space</kbd> + <kbd>Mouse 2</kbd>            | Starts or resets the smudge timer to 3:00 (or current default) |
| **Stop Smudge Timer**     | <kbd>Ctrl</kbd> + <kbd>Space</kbd> + <kbd>Mouse 2</kbd> | Stops the smudge timer (resets to 3:00)              |
| **Toggle Crosshair**      | <kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>C</kbd> | Shows/hides the center crosshair                     |
| **Toggle Settings**       | <kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>S</kbd> | Opens or closes the settings window                  |
| **Quit**                  | <kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>Q</kbd> | Immediately closes PhasOverlay                       |

**Recording or Changing Keybinds**:
1. Open **Settings** (default: <kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>S</kbd>).
2. Click the **Keybinds** tab.
3. Double-click on a keybind row to begin “recording.”
4. Press your desired key combination (keyboard or mouse).  
5. Once you release, the new combination is saved.  

---

## Settings

Access the **Settings** window via <kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>S</kbd> (by default). It has two main tabs:

### General Settings

- **Timer Settings**  
  - *Reverse Timer (CountUp?)* – Instead of counting down from 3:00, it counts up.  
  - *Smudge Timer Volume* – Adjust the volume of the audio cue that plays at certain intervals (5s, 95s, 125s, etc.).

### Overlay Settings

- *Show Overlay By Default* – If checked, the overlay will appear automatically upon application start.  
- *Enable Crosshair* – Shows or hides the center dot.  
- **Reset Overlay Position** – Click this to auto-resize the overlay to match Phasmophobia’s window.

### Application Settings

- *Hide Settings By Default* – If checked, the settings window won’t appear unless toggled via keybind.

All settings are saved in a `settings.json` file under your system’s AppData folder (e.g., `C:\Users\<User>\AppData\Roaming\PhasOverlay\settings.json`).

---

## Using the Overlay

1. **Launch** Phasmophobia in **windowed** or **borderless** mode (preferred).  
2. **Run** PhasOverlay.  
3. If “Show Overlay By Default” is enabled, the overlay appears. Otherwise, press <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>A</kbd> to show it.  
4. **Smudge Timer** usage:  
   - Press <kbd>Space</kbd>+<kbd>Mouse 2</kbd> to start/reset the timer.  
   - When the timer hits certain thresholds (120s, 90s, 5s), you may hear an audio cue.  
   - Stop the timer with <kbd>Ctrl</kbd>+<kbd>Space</kbd>+<kbd>Mouse 2</kbd>.  
5. **Crosshair**: Toggle on/off with <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>C</kbd>.  
6. **Settings**: <kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>S</kbd> opens the configuration UI.  
7. **Quit**: <kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>Q</kbd> or close the window normally.

> **Tip**: If you need to reposition the overlay to match the game window, open **Settings** -> **Reset Overlay**.

---

## FAQ / Troubleshooting

1. **The overlay is not visible.**  
   - Ensure you’re using a windowed or borderless mode in Phasmophobia.  
   - Check your keybinds (maybe the overlay was toggled off).  
   - Verify “Show Overlay By Default” is enabled, or press your toggle overlay keybind.

2. **Global Keybinds Not Working.**  
   - Some antivirus software can block global input hooks. Temporarily disable or add an exception.  
   - Make sure the native library extraction finished properly (look for `native-libs` folder under `bin`).

3. **Audio cues not playing.**  
   - Check your volume slider in **Settings** -> **Smudge Volume**.  
   - Ensure the `countdown.mp3` is present in `src/main/resources/audio`.

4. **Overlay flickers or hides behind the game.**  
   - Make sure you have “Always on Top” set in the code (the default) or in the compiled version.  
   - Windows sometimes imposes restrictions if you alt-tab or switch focus.

5. **I want a different crosshair** (size, color, shape).  
   - Modify `OverlayView.fxml` or `OverlayView.java` to change the `Circle` properties.

---

## Contributing

1. **Fork** the repository.  
2. **Create** a feature branch off `develop`.  
3. **Commit** your changes with clear messages.  
4. **Open** a Pull Request targeting `develop`.  
5. The CI workflow will build & test your code. After review, changes can be merged.

---

## License

This project is provided under an MIT-style license (or whatever license you choose). See `LICENSE` file for details.

---  

**Enjoy ghost hunting with an easy-to-use smudge timer and overlay!**
