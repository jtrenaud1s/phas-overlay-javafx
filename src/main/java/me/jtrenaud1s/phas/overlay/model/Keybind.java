package me.jtrenaud1s.phas.overlay.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public final class Keybind {
    private String name;
    private List<String> keys;

    @Override
    public String toString() {
        return String.join(" + ", keys);
    }
}