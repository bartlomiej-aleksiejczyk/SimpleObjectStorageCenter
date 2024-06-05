package com.example.zasobnik.user;

public enum UserRole {
    /**
     * Permits the user to perform all available actions on everything.
     */
    ROLE_ADMINISTRATOR,
    /**
     * Allows the user to read every log and file without making changes.
     */
    ROLE_INSPECTOR,
    /**
     * Grants the user the ability to perform actions on other users and create
     * flatboxes.
     */
    ROLE_MODERATOR,
    /**
     * Enables the user to create flatboxes.
     */
    ROLE_SUPER_EDITOR
}
