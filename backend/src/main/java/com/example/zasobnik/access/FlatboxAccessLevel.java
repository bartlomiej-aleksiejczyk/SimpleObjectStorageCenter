package com.example.zasobnik.access;

public enum FlatboxAccessLevel {
    /**
     * Allows the user to read content from a specified flatbox.
     */
    READER,
    /**
     * Enables the user to read and modify the content of a flatbox.
     */
    EDITOR,
    /**
     * Grants the user permissions to read and modify both the content and settings
     * of a flatbox.
     */
    OWNER
}
