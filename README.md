# MusicShare

## Requirements
- Docker
- Android-Studio
- Android-Emulator

## System-Komponenten:

- MongoDB:
  - Speicher Posts
  - Verwendet Docker

-FastAPI:
  - Simple Schnittstelle um CRUD-Operationen durchzuführen
  - Als seperates Projekt auf GitHub zu finden
   
- Mobile App
  - Direkten Zugriff auf Spotify-API -> Nur Prototyp, da sonst anders umzusetzen
  - Verbindung zum Server über 10.0.2.2:8000 (Ermöglicht Android Simulator Zugriff auf Ports des Hostsystems)

## Verwenden der App

Erstellen eines Api-Key für Spotify unter https://rapidapi.com/Glavier/api/spotify23/
-> Einfügen in SpotifyApiConnector.kt

In Android Studio ausführen

Seiten:

1. Login
  - Es gibt keine tatsächlichen Accounts, also wird für die Anmeldung ein beliebiger Username verwendet und das Passwort "password" funktioniert immer
  - Alle Posts verwenden den beim Login verwendeten Username
  - Man kann sich in einen bereits verwendeten Account einloggen, indem man den entsprechenden username verwendet
    
2. Home
  - Zeigt von anderen Personen geteilte Beiträge
  - Es gibt noch nicht die Option einlenze user zu abonnieren, weshalb alle auf der Datenbank vorhandenen Beiträge gezeigt werden
  - Gibt die Möglichkeit Beiträge zu liken -> Likes werden auf DB gespeichert
  - Durch Swipen nach unten werden immer die nächsten Beiträge abgerufen -> Infinite Scroll
  - Schnittstelle: Backend
    
3. Discover
  - Gibt User die Möglichkeit nach Songs zu suchen
  - Aktuell nur bewusst Songs und keine Alben/Künstler, um zu vereinfachen
  - Nachdem Songs gefunden wurde, kann bei dem Drücken des "+"-Buttons der Song gepostet und in der Datenbank gespeichert werden 
  - Schnittstelle: Spotify-Api für Suche und Backend für Datenverarbeitung
    
4. Profile
  - Zeigt gepostete Beiträge von verwendetem User an
  - Bietet die Möglichkeit Songs zu löschen
  - Schnittstelle: Backend
