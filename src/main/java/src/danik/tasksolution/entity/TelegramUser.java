package src.danik.tasksolution.entity;

public record TelegramUser(
        Long id,
        String firstName,
        String lastName,
        String username
) {}
