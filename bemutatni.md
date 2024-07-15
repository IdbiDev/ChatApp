# INSTANT ÜZENETKÜLDŐ ALKALMAZÁS

- Chatapp alap átnézése, mit csinál.

### App inditása, server client:

- Indító file, args

### DEMO:

- Belépés, alap Beszélgető szoba
- Pár message küldése tesztből, server console mutatása
- Másik client felléptetése, ugyanabba a szobába
- Kilépés (Command bemutatása)
- Szoba létrehozása, közben a másik client marad a beszélgetőben
- Belépés a másik clienttel
- Teszt üzenetek, message history
- Hiba tesztek:
    - Client bezárás
    - Server rendes bezárás (CTRL C)
    - Server force bezárás
    - Szoba készítésnél csak a pattern karakterjeit tudja beírni
- Server újraindítása SQL bemutatása céljából

### Említések:

- Frontend:
    - Server és Client egy fájlban, testreszabható indítás ebben a két módban
    - View rendszer bővítés miatt
    - Table manager bemutatása röviden
    - Lapozható room list view
    - Hülye biztosság
    
- Backend:
    - Packet networking, nem sima text alapon
    - Event rendszer bővítés céljából
    - Client időhamisítás nincs (Client side time zone amit szerver küld)
    - Teljeskörű command library

# Rendes kód mutatása cause why not

### Mostani munkálatok:

- Görgethető chatView
- Több command (szoba kezelés)
- Szoba adminisztrátor
- Global Admin
- Több / Jobb hiba kezelés
- Értesítések megjavítása
- System messagek megjavítása
- Kettő ugyanolyan nevű szoba tiltása
- Kettő ugyanolyan nevű felhasználó megkülönböztetése

### Jövőbeli tervek:

- Üzenetek titkosítása
- Jobb UI design
- Addon support
- AutoUpdate
- File küldés
    
    
    