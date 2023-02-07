:- use_module(library(date_time)).
:- use_module(library(odbc)).



% INTERFACCIAMENTO AL DATABASE

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

% predicato che trasforma le row ritornate dal db, in una lista di liste
create_list(L2):- select_all_spese(L), create(L,L1),!, delete_row(L1,L2).

% perdicato che estrae dalla lista una sottolista che contiene solo le spese di Utente
sublist2(Listadilista, Utente, Listafinale):- member(Listafinale, Listadilista), member([_,Utente,_,_,_,_],[Listafinale]).



% PREDICATI PER ESEGUIRE LE QUERY AL DATABASE MEDIANTE L'INTERFACCIA ODCB

% predicato seleziona una riga alla volta dalla tabella spese
select_spesa(X):- odbc_query('prolog',"SELECT * FROM spese",X).

% predicato seleziona tutte le righe della tabella spese e le inserisce in una lista
select_all_spese(L):- findall(X,select_spesa(X),L).

%predicato che crea la lista di spese dell'utente correntemente loggato
spese_di_utente(L,User):- create_list(L2),findall(Listafinale,sublist2(L2,User,Listafinale),L).

% predicato per inserire una nuova spesa nel database
insert_spesa(User,Descrizione, Costo, Categoria, Data,X):- atom_string(C,Costo),
                odbc_prepare('prolog',"INSERT INTO spese(utente,descrizione,costo,categoria,data)
                VALUES (?, ?, ?, ?, ?)", [default, default, default, default, default], Statement),
                odbc_execute(Statement, [User,Descrizione,C,Categoria,Data]), !;
                X=1.

% predicato per eliminare una spesa dal database su base id (ossia la chiave della tabella spese)
delete_spesa(Id,X):- odbc_prepare('prolog',"DELETE FROM spese WHERE id = ?",[default], Statement),
                   atom_number(A,Id), odbc_execute(Statement, [A]), X = 1,!; X = 0.

% predicato per inserire un nuovo utente nel database
insert_utente(User, Pass):- downcase_atom(User,UserDownCase), odbc_prepare('prolog',"INSERT INTO utenti(nome,pass) VALUES (?, ?)", [default, default], Statement),
                            odbc_execute(Statement, [UserDownCase, Pass]).

% predicato seleziona una riga alla volta dalla tabella utente
select_utente(X):- odbc_query('prolog',"SELECT * FROM utenti",X).

% predicato seleziona tutte le righe e le inserisce in una lista
select_all_utenti(L):- findall(X,select_utente(X),L).


% PREDICATI PRINCIPALI IN RISPOSTA AD AZIONI DELL'UTENTE

% predicato che controlla username e password inseriti corrispondano ad una tupla nella tabella utenti del database
check_login(User,Pass,X):- select_all_utenti(L), member(row(User,Pass),L), X = 1, !;
    X = 0.

% predicato che controlla che username e password inseriti in particolare verificando che l'username non sia gia in uso,
% che la password sia abbastanza forte e che password e conferma password corrispondono; se tali controlli hanno successo
% allora viene inserita una nuova tupla nella tabella utenti
check_registration(User,Pass,PassConfirm,X):- select_all_utenti(L), downcase_atom(User,UserDownCase),member(row(UserDownCase,_),L), X = 1, !;
                           Pass \== PassConfirm, X = 2, !;
                           insert_utente(User,Pass), X = 0.


% predicato che calcola la data odierna
data_oggi(X,Y,Z):- date(Oggi), date(X,Y,Z) = Oggi.

% predicato che calcola la data di scadenza (ossia la data successiva ad oggi di una settinana)
data_scadenze(AnnoScadenza,MeseScadenza,GiornoScadenza):- date(Oggi), date_add(Oggi, 1 weeks, date(AnnoScadenza,MeseScadenza,GiornoScadenza)).

% predicato che confronta spese su data ritornando una lista in cui sono contenute le spese future
confronta_data(Lol,Lf):- data_oggi(U,_,_), member(Lf,Lol), member([_,_,_,_,_,date(X,_,_)],[Lf]),U<X;
                         data_oggi(U,V,_), member(Lf,Lol), member([_,_,_,_,_,date(X,Y,_)],[Lf]),U==X,V<Y;
                         data_oggi(U,V,W), member(Lf,Lol), member([_,_,_,_,_,date(X,Y,Z)],[Lf]),U==X,V==Y,W<Z.

% predicato che filtra le spese di un utente determinando se ci sono spese future
filtra_spese_future(L,User):- spese_di_utente(L1, User), findall(Lista, confronta_data(L1,Lista),L).

% predicato che confronta spese su data ritornando una lista in cui sono contenute le spese future
spese_nella_settimana(Lol,Lf):- data_scadenze(AnnoScadenza,MeseScadenza,GiornoScadenza), data_oggi(Anno,Mese,Giorno), member(Lf, Lol), member([_,_,_,_,_,date(X,Y,Z)],[Lf]),
                                %caso 1
                                Anno == X, X == AnnoScadenza, Mese == Y, Y == MeseScadenza, Giorno < Z, Z =< GiornoScadenza;
                                data_scadenze(AnnoScadenza,MeseScadenza,_), data_oggi(Anno,Mese,Giorno),
                                member(Lf,Lol), member([_,_,_,_,_,date(X,Y,Z)], [Lf]),
                                %caso 2
                                Anno == X, X == AnnoScadenza, Mese == Y, Y =< MeseScadenza, Z >= Giorno;
                                data_scadenze(AnnoScadenza,MeseScadenza,GiornoScadenza), data_oggi(Anno,Mese,_),
                                %caso 3
                                member(Lf,Lol), member([_,_,_,_,_,date(X,Y,Z)], [Lf]),
                                Anno == X, X == AnnoScadenza, Mese =< Y, Y == MeseScadenza, Z =< GiornoScadenza;
                                data_scadenze(AnnoScadenza,MeseScadenza,GiornoScadenza),data_oggi(Anno,_,_),
                                member(Lf,Lol), member([_,_,_,_,_,date(X,Y,Z)],[Lf]),
                                %caso 4
                                Anno < X, X == AnnoScadenza, Y =< MeseScadenza, Z =< GiornoScadenza.

% predicato che filtra le spese di un utente determinando se ci sono spese in scadenza per il pagamento nella settimana
filtra_spese_inscadenza(L,User):- spese_di_utente(L1, User), findall(Lista, spese_nella_settimana(L1,Lista),L).


% predicato che ritorna i campi categoria e costo di tutte le spese
% dell'utente
costo_categoria(SpeseCat,L):- member([_,_,_,Cos,Cat,_],SpeseCat), atom_number(Cos,C), L=[Cat,C].
costo_per_categoria(Spese,L1):- findall(L,costo_categoria(Spese,L),L1).

%predicato che, data una categoria, ritorna tutti i costi associati
costi(Spese,Cat,SpeseCat):- costo_per_categoria(Spese,L), member([Cat,Cos],L), SpeseCat=Cos.
somma_costi(Spese,Cat,Sum):- findall(SpeseCat,costi(Spese,Cat,SpeseCat),L), sum(L,Sum).

% predicato che ritorna una lista con la somma dei costi sostenuti per ogni categoria
somma_per_cat(Spese,SommaCat):- costo_per_categoria(Spese,L), member([Cat,_],L), somma_costi(Spese,Cat,Sum), SommaCat=[Cat,Sum].
somma_totale_per_cat(Spese,SpesePerCat):- setof(L1,somma_per_cat(Spese,L1),SpesePerCat).

somma_spese_future(SpesePerCat,User):- filtra_spese_future(SpeseFuture,User), somma_totale_per_cat(SpeseFuture,SpesePerCat).
