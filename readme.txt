1. Repository factory si baza de date
In aplicatie, crearea instantelor concrete de repository este complet 
incapsulata in clasa Repositoryfactory. Aceasta este singura clasa din intregul sistem care 
cunoaste implementarile concrete.
- Factory-ul citeste fisierul de configurare src/main/resources/config.properties
- Determina tipul persistenței pe baza proprietatii repository.type
- Expune metodele createUserRepository(validator) si 
  createFriendshipRepository(validator).

Pt trecerea la baza de date:
1. In src/main/resources/config.properties:
   Se schimba valoarea proprietatii:
   repository.type = DATABASE`
   si se configureaza parametrii conexiunii:
   db.url,db.user,db.password
2. In clasele schelet UserDbRepository si FriendshipDbRepository:
   Se inlocuieste aruncarea `UnsupportedOperationException` cu implementarea 
   metodelor necesare.

2.Tratarea fisierelor inexistente si liniilor corupte:
a) Fisier inexistent:
- La pornirea aplicatiei, repository-ul verifica daca fisierul 
  indicat exista pe disc. Daca fisierul nu exista (sau este gol), aplicatia 
  nu se blocheaza, ci porneste cu repository-ul in memorie gol. Fisierul va fi 
  creat automat pe disc la prima operatie de salvare (add/save).
b) Linii corupte / invalide in fisier:
- In timpul incarcarii datelor din fisier,fiecare linie este procesata separat. Daca o linie contine date invalide 
linia este raportata cu numarul exact al liniei si motivul erorii.

3. Principii SOLID:
- SRP (Single Responsibility Principle):
  Fiecare clasa are o responsabilitate unica:
    - Entitatile pastreaza starea si datele domeniului.
    - Validatorii au responsabilitatea 
      exclusiva a verificarii corectitudinii regulilor de business.
    - Repository-urile se ocupa exclusiv de persistenta si gestiunea colectiilor de date.
    - Service orchestreaza fluxul de lucru si algoritmii de retea.
    - Ui se ocupa exclusiv de interactiunea cu consola si prezentarea mesajelor.

- OCP (Open / Closed Principle):
  Clasele sunt deschise pentru extindere, dar inchise pentru modificare:
    - Adaugarea unui nou tip de persistenta (de exemplu baza de date in Lab 4) 
      necesita doar adaugarea noilor clase repository,fara a modifica codul din Service, Ui sau entitati.
    - AbstractFileRepository este inchis pentru logica de citire/scriere, 
      dar deschis extinderii prin Template Method (createEntity, createLine).

- LSP (Liskov Substitution Principle):
  Orice implementare a interfetei Repo<E> poate inlocui complet interfata Repo in cadrul 
    Service-ului fara ca acesta sa observe vreo diferenta de functionalitate 
    sau sa apara comportamente neasteptate.

- DIP (Dependency Inversion Principle):
   Modulele de nivel inalt nu depind de modulele de nivel jos, ci ambele depind de abstractiuni. 
Injectarea dependentelor se realizeaza prin constructor.
4. Design patterns:
- Factory Pattern (Simple Factory):
   Regasit in: Repositoryfactory.
   Rol: Incapsuleaza decizia de instantiere a claselor concrete de repository 
    pe baza fisierului config.properties. 
- Template Method:
   Regasit in: AbstractFileRepository si subclasele UserFileRepository, 
    FriendshipFileRepository.
  Rol: Clasa de baza AbstractFileRepository defineste scheletul algoritmului 
    de citire (loadFile) si scriere (writeToFile) in fisier, iar operatiile 
    primitive de conversie linie <-> entitate (createEntity, createLine) 
    sunt delegate subclaselor.

- Strategy:
  Regasit in: Interfata Validator<E> si implementarile concrete 
    UserValidator, FriendshipValidator.
  Rol: Algoritmul de validare este incapsulat intr-o strategie separata 
    transmisa prin constructor repository-ului, putand fi schimbat sau extins 
    independent.

- Decorator:
   Regasit in: Fluxurile I/O pe caractere.


------------------------------------------------------------------------------------
Lab 4
Am ales sa pastrez in repo conversia linie <-> entitate din mai multe motive:
-izolarea logicii de conversie intr-o clasa face mai simpla scrierea
-construirea de repo-uri este mai curata
Am ales ca interfata repo sa returneze in continuare null pentru a nu schimba contractul initial

inainte/dupa stream api:

AbstractFileRepository - writeToFile :
Inainte:  for (E entity : mem.values()) { bw.write(createLine(entity)); bw.newLine(); } 
Dupa:List<String> lines = super.getAll().values().stream().map(this::createLine).collect(Collectors.toList()); 

Service-RemoveUser
Inainte: for-each peste toate prieteniile cu if (uid1 == id || uid2 == id) si colectare pentru stergere  Dupa: friendshipRepo.getAll().values().stream().filter(...).collect(Collectors.toList()); apoi toDelete.forEach

Service-buildGraph
Inainte: 2 bucle for-each: una pentru initializarea listelor din adj si una pentru adaugarea muchiilor
Dupa: userRepo...stream().collect(Collectors.toMap(...)) si friendshipRepo...stream().filter(...).forEach(...)  

Service-dfsComponent
Inainte: for (int n : adj.get(node)) { if (!visited.contains(n)) component.addAll(dfsComponent(...)); } 
Dupa:adj.get(node).stream().filter(...).flatMap(...).collect(...) combinat cu Stream.concat 
                   
Service-longestPathFrom 
Inainte: for (int v : adj.getOrDefault(...)) { if (!visitedInPath.contains(v)) { calcul maxDist; } } 
Dupa: adj.getOrDefault(...).stream().filter(...).mapToInt(v -> 1 + longestPathFrom(...)).max().orElse(0);      