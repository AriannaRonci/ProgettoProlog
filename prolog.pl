% LIBRERIE ESTERNE CHE SONO UTILIZZATE
:- use_module(library(date_time)).
:- use_module(library(odbc)).



% PREDICATI INTERFACCIAMENTO AL DATABASE

% predicato che effettua la connessione al database tramite il driver ODBC
connessione :- odbc_connect('Prolog',_,
                          [user(root),password(''),
                           alias(prolog),open(once)]).

% predicato che chiude la connessione aperta con il database
chiusura :- odbc_disconnect('prolog').


% PREDICATI DI BASE PER LA CREAZIONE E PER LA MANIPOLAZIONE DI LISTE E LISTE DI LISTE

%predicato ricorsivo che trasforma una lista in una lista di liste
create([],[]).
create([H|T],[[H]|T2]) :- create(T,T2).

% predicato che somma gli elementi di una lista
sum([], 0).
sum([H|T], Sum) :- sum(T, Rest), Sum is H + Rest.

% predicato ricorsivo che permette di eliminare la struttura row() dal risultato di una query al database
delete_row([],[]).
delete_row([[row(Id,Nome,Descrizione,Costo,Categoria,Data)]|T],[[Id,Nome,Descrizione,Costo,Categoria,Data]|T2]):- delete_row(T,T2).

% predicato che trasforma le row ritornate dal db in una lista di liste
create_list(L2):- select_all_spese(L), create(L,L1),!, delete_row(L1,L2).

% perdicato che estrae dalla lista una sottolista che contiene solo le spese di Utente
sottolista(Listadilista, Utente, Listafinale):- member(Listafinale, Listadilista), member([_,Utente,_,_,_,_],[Listafinale]).



% PREDICATI PER ESEGUIRE LE QUERY AL DATABASE MEDIANTE L'INTERFACCIA ODCB

% predicato seleziona una riga alla volta dalla tabella spese
select_spesa(X):- odbc_query('prolog',"SELECT * FROM spese",X).

% predicato seleziona tutte le righe della tabella spese e le inserisce in una lista
select_all_spese(L):- findall(X,select_spesa(X),L).

%predicato che crea la lista di spese dell'utente correntemente loggato
spese_di_utente(ListaSpeseUtente,Utente):- create_list(ListaSpese),findall(Spesa,sottolista(ListaSpese,Utente,Spesa),ListaSpeseUtente).

% predicato per inserire una nuova spesa nel database
insert_spesa(Utente,Descrizione, Costo, Categoria, Data,X):- atom_string(C,Costo),
                odbc_prepare('prolog',"INSERT INTO spese(utente,descrizione,costo,categoria,data)
                VALUES (?, ?, ?, ?, ?)", [default, default, default, default, default], Statement),
                odbc_execute(Statement, [Utente,Descrizione,C,Categoria,Data]), !;
                X=1.

% predicato per eliminare una spesa dal database su base id (ossia la chiave della tabella spese)
delete_spesa(Id,X):- odbc_prepare('prolog',"DELETE FROM spese WHERE id = ?",[default], Statement),
                   atom_number(I,Id), odbc_execute(Statement, [I]), X = 1,!; X = 0.

% predicato per inserire un nuovo utente nel database
insert_utente(Utente, Pass):- downcase_atom(Utente,UserDownCase), odbc_prepare('prolog',"INSERT INTO utenti(nome,pass) VALUES (?, ?)", [default, default], Statement),
                            odbc_execute(Statement, [UserDownCase, Pass]).

% predicato seleziona una riga alla volta dalla tabella utente
select_utente(X):- odbc_query('prolog',"SELECT * FROM utenti",X).

% predicato seleziona tutte le righe e le inserisce in una lista
select_all_utenti(L):- findall(X,select_utente(X),L).



% PREDICATI PER LOGIN E REGISTRAZIONE

% predicato che controlla username e password inseriti corrispondano ad una tupla nella tabella utenti del database
check_login(User,Pass,X):- select_all_utenti(L), member(row(User,Pass),L), X = 1, !;
    X = 0.

% predicato che controlla che username e password inseriti in particolare verificando che l'username non sia gia in uso,
% che la password sia abbastanza forte e che password e conferma password corrispondono; se tali controlli hanno successo
% allora viene inserita una nuova tupla nella tabella utenti
check_registration(User,Pass,PassConfirm,X):- select_all_utenti(L), downcase_atom(User,UserDownCase),member(row(UserDownCase,_),L), X = 1, !;
                           Pass \== PassConfirm, X = 2, !;
                           insert_utente(User,Pass), X = 0.



% PREDICATI FILTRAGGIO SPESE FUTURE

% predicato che calcola la data odierna
data_oggi(Anno,Mese,Giorno):- date(Oggi), date(Anno,Mese,Giorno) = Oggi.

% predicato che calcola la data di scadenza (ossia la data successiva ad oggi di una settinana)
data_scadenze(AnnoScadenza,MeseScadenza,GiornoScadenza):- date(Oggi), date_add(Oggi, 1 weeks, date(AnnoScadenza,MeseScadenza,GiornoScadenza)).

% predicato che confronta spese su data ritornando una lista in cui sono contenute le spese future
confronta_data(Listaspese,Spesafiltrata):- data_oggi(Anno,_,_), member(Spesafiltrata, Listaspese), member([_,_,_,_,_,date(A,_,_)],[Spesafiltrata]), Anno<A;
                         data_oggi(Anno,Mese,_), member(Spesafiltrata, Listaspese), member([_,_,_,_,_,date(A,M,_)],[Spesafiltrata]), Anno==A, Mese<M;
                         data_oggi(Anno,Mese,Giorno), member(Spesafiltrata, Listaspese), member([_,_,_,_,_,date(A,M,G)],[Spesafiltrata]), Anno==A, Mese==M, Giorno<G.


% predicato che filtra le spese di un utente determinando se ci sono spese future
filtra_spese_future(Listafiltrata,Utente):- spese_di_utente(Speseutente, Utente), findall(Spesafiltrata, confronta_data(Speseutente,Spesafiltrata),Listafiltrata).



% PREDICATI PER LE STATISTICHE RELATIVE ALLE SPESE FUTURE

% predicati per ottenere una lista di liste contenenti i campi categoria e costo
% di tutte le spese dell'utente
costo_categoria(Spese,CostoCategoria):- member([_,_,_,Costo,Categoria,_],Spese), atom_number(Costo,C), CostoCategoria=[Categoria,C].
costo_per_categoria(Spese,CostiCategorie):- findall(CostoCategoria,costo_categoria(Spese, CostoCategoria), CostiCategorie).

% predicati per determinare la somma dei costi di tutte le spese di una data categoria
costi(Spese,Categoria,Costo):- costo_per_categoria(Spese, Costicategorie),member([Categoria,C], Costicategorie), Costo=C.
somma_costi(Spese,Categoria,Sommacosticategoria):- findall(Costo,costi(Spese,Categoria, Costo), Costi),sum(Costi,Sommacosticategoria).

% predicati per ottenere una lista con la somma dei costi per ogni categoria
somma_per_cat(Spese,SommaCat):- costo_per_categoria(Spese,L), member([Cat,_],L), somma_costi(Spese,Cat,Sum), SommaCat=[Cat,Sum].
somma_totale_per_cat(Spese,SpesePerCat):- setof(L1,somma_per_cat(Spese,L1),SpesePerCat).

% predicato che deternina il costo totale associato ad ogni categoria considerando solo le date future
somma_spese_future(SpesePerCat,Utente):- filtra_spese_future(SpeseFuture,Utente), somma_totale_per_cat(SpeseFuture,SpesePerCat).


% PREDICATI FILTRAGGIO SPESE SU BASE CATEGORIE

% predicato che data una lista di spese e una data categoria specificata estrae un elemento alla volta in base a che tale spesa riguardi quella categoria
spesa_con_categoria(Spese,Categoria,Spesafiltrata,Utente):- member(Spesafiltrata, Spese), member([_,Utente,_,_,Categoria,_],[Spesafiltrata]).

% predicato che effettua il filtraggio su base categoria delle spese dell'utente e le stampa
filtra_categoria(Categoria,Utente,ListaSpeseFiltrata):- create_list(ListaSpese), findall(Spesafiltrata,spesa_con_categoria(ListaSpese,Categoria,Spesafiltrata,Utente),ListaSpeseFiltrata).


% PREDICATI FILTRAGGIO SPESE SU BASE RANGE DI PREZZO

% predicato che data la lista di liste contenete tutte le spese di un utente
% esamina la singola spesa e verifica se il costo è compreso tra valore minimo e un valore massimo
prezzi(Spese,Min,Max,Spesa):- member(Spesa, Spese), member([_,_,_,Costo,_,_],[Spesa]),
                                      atom_number(Costo,C),C>=Min, C=<Max.

% predicato che popola la lista SpeseFiltrate con le spese il cui costo è compreso tra valore minimo e un valore massimo
filtra_prezzi(SpeseFiltrate,Utente,Min,Max):- spese_di_utente(Spese,Utente), findall(Lista, prezzi(Spese,Min,Max,Lista),SpeseFiltrate).