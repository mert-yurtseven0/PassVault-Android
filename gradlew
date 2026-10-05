#!/bin/sh
# Gradle startup script for POSIX systems
exec gradle "$@" 2>/dev/null || {
    echo "Sisteminizde Gradle veya Android Studio yüklü olmalıdır."
    echo "Detaylar için README.md dosyasını inceleyin."
    exit 1
}
