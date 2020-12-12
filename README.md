# POO Tema 1 - VideosDB

## Obiective
În cadrul acestei teme veți implementa o platformă simplificată ce oferă informații despre filme și despre seriale. 
Utilizatorii pot să primească recomandări personalizate pe baza preferințelor.

## Descriere
Platforma pe care o veți implementa simulează acțiuni pe care le pot face utilizatorii pe o platforma de vizualizare de filme: ratings, vizualizare film, căutări, recomandări etc.

**Entitățile** pe care le veți modela vor fi:
* Video
* User 
* Actor
    
Datele pentru aceste entități sunt încărcate din fișierele JSON oferite ca intrare în teste. Ele sunt ținute într-un **Repository**.

Utilizatorii pot realiza următoarele trei tipuri de **acțiuni**: *comenzi*, *query-uri* și *recomandări*.

## Execuția inputului ##

1. Se încarcă datele citite din fișierul de test, în format JSON, în obiecte.
2. Se primesc secvențial acțiuni (comenzi, queries sau recomandări) și se execută pe măsură ce sunt primite, rezultatul lor având efect asupra Repository-ului
3. După executarea unei acțiuni, se afișează rezultatul ei în fișierul JSON de ieșire
4. La terminarea tuturor acțiunilor se termina și execuția programului.

## Comenzi ##

Acestea reprezintă abilitatea unui utilizator de a realiza *acțiuni directe*, fiind de 3 tipuri diferite.

* **Favorite** - adaugă un video în lista de favorite videos ale acelui user, dacă a fost deja vizionat de user-ul respectiv.
* **View** - vizualizează un video prin marcarea lui ca văzut. Dacă l-a mai văzut anterior, se incrementează numărul de vizualizări ale acelui video de către user.
* **Rating** - oferă rating unui video care este deja văzut

## Query ## 

Acestea reprezintă *căutări* globale efectuate de utilizatori după actori, video-uri și utilizator.

**Pentru actori:**

* **Average** - primii N actori (N dat în query) sortați după media ratingurilor filmelor și a serialelor în care au jucat.
* **Awards** - toți actorii cu premiile menționate în query.
* **Filter Description** - toți actorii în descrierea cărora apar toate keywords-urile menționate în query.

**Pentru video-uri:**

* **Rating** - primele N video-uri sortate după rating.
* **Favorite** - primele N video-uri sortate după numărul de apariții în listele de video-uri favorite ale utilizatorilor
* **Longest** - primele N video-uri sortate după durata lor.
* **Most Viewed** - primele N video-uri sortate după numărul de vizualizări.

**Pentru utilizatori:**

* *Number Of Ratings:* -  primii N utilizatori sortați după numărul de ratings pe care le-au dat (practic cei mai activi utilizatori)

## Recomandări ##

Acestea reprezintă căutări după video-uri ale utilizatorilor. Ele sunt *particularizate* pe baza profilului acestora și au la bază 5 strategii.

**Pentru toți utilizatorii:**

* **Standard** - întoarce primul video nevăzut de utilizator din baza de date
* **Best Unseen** - întoarce cel mai bun video nevizualizat de acel utilizator.

**Doar pentru utilizatorii premium:**

* **Popular** - întoarce primul video nevizualizat din cel mai popular gen. 
* **Favorite** - întoarce videoclipul care e cel mai des intalnit in lista de favorite a tuturor utilizatorilor.
* **Search** - toate videoclipurile nevăzute de user dintr-un anumit gen, dat ca filtru în input.

Cerintele si detaliile complete se gasesc in [enuntul problemei](https://github.com/btudorache/videosDB/blob/master/Programare_2019___Tema_3.pdf)

## Notes ##
Clasele implementate de mine se gasesc in pachetele *models* si *database*. Celelalte sunt pachete auxiliare utilizate pentru
parsare JSON si pentru testare oferite de *echipa POO seriile CA, CD* de la *Facultatea de Automatica si Calculatoare, Universitatea Politehnica Bucuresti*.

