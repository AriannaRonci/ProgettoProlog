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

% predicato che ritorna i campi categoria e costo di tutte le spese dell'utente
costo_categoria(L1,L):- member([_,_,_,Cos,Cat,_],L1),atom_number(Cos,C),L=[Cat,C].
costo_per_categoria(L2,L1):- findall(L,costo_categoria(L2,L),L1).

%predicato che, data una categoria, ritorna tutti i costi associati
costi(L2,Cat,L1):- costo_per_categoria(L2,L),member([Cat,Cos],L), L1=Cos.
somma_costi(L2,Cat,Sum):- findall(L,costi(L2,Cat,L),L1),sum(L1,Sum).

% predicato che ritorna una lista con la somma dei costi sostenuti per ogni categoria
somma_per_cat(L2,L1):- costo_per_categoria(L2,L), member([Cat,_],L), somma_costi(L2,Cat,Sum), L1=[Cat,Sum].
somma_totale_per_cat(L2,L):- setof(L1,somma_per_cat(L2,L1),L).
