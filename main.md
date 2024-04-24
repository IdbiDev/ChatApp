# GYVAKK Chat App

---

A GYVAKK Chat App egy Java programnyelvben készült chat applikáció. Azonos hálózaton belüli beszélgetést teszi lehetővé. Az applikáció nagyban elősegíti beszélgetést és növeli a beszélgetés élményét.</br>
**FONTOS: CSAK ÍRÁSBAN TÁMOGATJA A BESZÉLGETÉST. HANGBESZÉLGETÉS NEM ELÉRHETŐ!**

### Funkció lista
- Globális, saját szobák létrehozása
- Szobákban lévő tagok számának korlátozása
- Állítható üzenet előzmények
- Szobák védése jelszóval
- Globális, szoba kitiltás
- Teljes körű fejlesztői API

# Használat

---

1. Indítsd el a szerver oldalt
2. Az automatikusan legenerált `config.json` fájlban állítsd be az értékeket
3. Indítsd el a szerver oldalt újra
4. Indítsd el a kliens oldalt

A szerver előszöri elindítása után automatikusan leáll, és legenerálja a saját szükséges fájlokat, mappákat.
A `config.json` fájlban találhatóak a konfigurálható értékek. **Amit itt nem találsz, azt nem tudod állítani!**




# Indítás

---

**Szerver indítóparancsa:**
```batch
java -jar chatapp.jar -server
```

**Kliens indítóparancsa:**
```batch
java -jar chatapp.jar -client
```