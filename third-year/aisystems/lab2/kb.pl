:- discontiguous name/2, male/1, female/1, birth/2, death/2, parent/2, married/3, divorced/3.

% Правила, описывающие всех людей в древе
name(ivan_ivanov_sr, 'Иван Иванов').
male(ivan_ivanov_sr).
birth(ivan_ivanov_sr, 1935). death(ivan_ivanov_sr, 2004).

name(elena_ivanova_sr, 'Елена Иванова').
female(elena_ivanova_sr).
birth(elena_ivanova_sr, 1937). death(elena_ivanova_sr, 2012).

name(sergey_petrov_sr, 'Сергей Петров').
male(sergey_petrov_sr).
birth(sergey_petrov_sr, 1938). death(sergey_petrov_sr, 2010).

name(galina_petrova, 'Галина Петрова').
female(galina_petrova).
birth(galina_petrova, 1940). death(galina_petrova, 2015).

name(alexey_ivanov, 'Алексей Иванов').
male(alexey_ivanov).
birth(alexey_ivanov, 1958).

name(olga_sokolova, 'Ольга Соколова').
female(olga_sokolova).
birth(olga_sokolova, 1960).

name(natalya_ivanova, 'Наталья Иванова').
female(natalya_ivanova).
birth(natalya_ivanova, 1962).

name(viktor_smirnov, 'Виктор Смирнов').
male(viktor_smirnov).
birth(viktor_smirnov, 1961).

name(pavel_ivanov, 'Павел Иванов').
male(pavel_ivanov).
birth(pavel_ivanov, 1965). death(pavel_ivanov, 2012).

name(tatyana_kuznetsova, 'Татьяна Кузнецова').
female(tatyana_kuznetsova).
birth(tatyana_kuznetsova, 1966).

name(irina_petrova, 'Ирина Петрова').
female(irina_petrova).
birth(irina_petrova, 1960).

name(mikhail_orlov, 'Михаил Орлов').
male(mikhail_orlov).
birth(mikhail_orlov, 1959).

name(yuri_petrov, 'Юрий Петров').
male(yuri_petrov).
birth(yuri_petrov, 1963).

name(svetlana_romanova, 'Светлана Романова').
female(svetlana_romanova).
birth(svetlana_romanova, 1965).

name(marina_vlasova, 'Марина Власова').
female(marina_vlasova).
birth(marina_vlasova, 1970).

name(denis_ivanov, 'Денис Иванов').
male(denis_ivanov).
birth(denis_ivanov, 1981).

name(ksenia_melnikova, 'Ксения Мельникова').
female(ksenia_melnikova).
birth(ksenia_melnikova, 1983).

name(alina_grebneva, 'Алина Гребнева').
female(alina_grebneva).
birth(alina_grebneva, 1987).

name(elena_ivanova_jr, 'Елена Иванова').
female(elena_ivanova_jr).
birth(elena_ivanova_jr, 1984).

name(roman_zakharov, 'Роман Захаров').
male(roman_zakharov).
birth(roman_zakharov, 1982).

name(kirill_smirnov, 'Кирилл Смирнов').
male(kirill_smirnov).
birth(kirill_smirnov, 1984).

name(darya_lebedeva, 'Дарья Лебедева').
female(darya_lebedeva).
birth(darya_lebedeva, 1986).

name(polina_smirnova, 'Полина Смирнова').
female(polina_smirnova).
birth(polina_smirnova, 1988).

name(artyom_ivanov, 'Артём Иванов').
male(artyom_ivanov).
birth(artyom_ivanov, 1989).

name(anna_yureva, 'Анна Юрьева').
female(anna_yureva).
birth(anna_yureva, 1990).

name(oksana_orlova, 'Оксана Орлова').
female(oksana_orlova).
birth(oksana_orlova, 1983).

name(ilya_orlov, 'Илья Орлов').
male(ilya_orlov).
birth(ilya_orlov, 1986).

name(ekaterina_nikolaeva, 'Екатерина Николаева').
female(ekaterina_nikolaeva).
birth(ekaterina_nikolaeva, 1987).

name(igor_petrov, 'Игорь Петров').
male(igor_petrov).
birth(igor_petrov, 1988).

name(stepan_petrov, 'Степан Петров').
male(stepan_petrov).
birth(stepan_petrov, 2000).

name(sofya_petrova, 'Софья Петрова').
female(sofya_petrova).
birth(sofya_petrova, 2004).

name(mark_ivanov, 'Марк Иванов').
male(mark_ivanov).
birth(mark_ivanov, 2007).

name(eva_ivanova, 'Ева Иванова').
female(eva_ivanova).
birth(eva_ivanova, 2019).

name(vera_ivanova, 'Вера Иванова').
female(vera_ivanova).
birth(vera_ivanova, 2012).

name(gleb_ivanov, 'Глеб Иванов').
male(gleb_ivanov).
birth(gleb_ivanov, 2015).

name(timofey_smirnov, 'Тимофей Смирнов').
male(timofey_smirnov).
birth(timofey_smirnov, 2013).

name(lev_ivanov, 'Лев Иванов').
male(lev_ivanov).
birth(lev_ivanov, 2018).

name(nina_ivanova, 'Нина Иванова').
female(nina_ivanova).
birth(nina_ivanova, 2021).

name(matvey_orlov, 'Матвей Орлов').
male(matvey_orlov).
birth(matvey_orlov, 2016).

name(agata_orlova, 'Агата Орлова').
female(agata_orlova).
birth(agata_orlova, 2020).

name(alena_smirnova, 'Алёна Смирнова').
female(alena_smirnova).
birth(alena_smirnova, 2010).

% married(Person1, Person2, Year). divorced(Person1, Person2, Year).

married(ivan_ivanov_sr, elena_ivanova_sr, 1956).
married(sergey_petrov_sr, galina_petrova, 1959).
married(alexey_ivanov, olga_sokolova, 1980).
married(natalya_ivanova, viktor_smirnov, 1983).
married(pavel_ivanov, tatyana_kuznetsova, 1987).
married(irina_petrova, mikhail_orlov, 1982).

married(yuri_petrov, svetlana_romanova, 1986).
divorced(yuri_petrov, svetlana_romanova, 1995).
married(yuri_petrov, marina_vlasova, 1998).

married(denis_ivanov, ksenia_melnikova, 2006).
divorced(denis_ivanov, ksenia_melnikova, 2015).
married(denis_ivanov, alina_grebneva, 2018).

married(elena_ivanova_jr, roman_zakharov, 2010).
married(kirill_smirnov, darya_lebedeva, 2011).
married(artyom_ivanov, anna_yureva, 2017).
married(ilya_orlov, ekaterina_nikolaeva, 2014).

% parent(Parent, Child).
parent(ivan_ivanov_sr, alexey_ivanov).
parent(elena_ivanova_sr, alexey_ivanov).

parent(ivan_ivanov_sr, natalya_ivanova).
parent(elena_ivanova_sr, natalya_ivanova).

parent(ivan_ivanov_sr, pavel_ivanov).
parent(elena_ivanova_sr, pavel_ivanov).

parent(sergey_petrov_sr, irina_petrova).
parent(galina_petrova, irina_petrova).

parent(sergey_petrov_sr, yuri_petrov).
parent(galina_petrova, yuri_petrov).

parent(alexey_ivanov, denis_ivanov).
parent(olga_sokolova, denis_ivanov).

parent(alexey_ivanov, elena_ivanova_jr).
parent(olga_sokolova, elena_ivanova_jr).

parent(natalya_ivanova, kirill_smirnov).
parent(viktor_smirnov, kirill_smirnov).

parent(natalya_ivanova, polina_smirnova).
parent(viktor_smirnov, polina_smirnova).

parent(pavel_ivanov, artyom_ivanov).
parent(tatyana_kuznetsova, artyom_ivanov).

parent(irina_petrova, oksana_orlova).
parent(mikhail_orlov, oksana_orlova).

parent(irina_petrova, ilya_orlov).
parent(mikhail_orlov, ilya_orlov).

parent(yuri_petrov, igor_petrov).
parent(svetlana_romanova, igor_petrov).

parent(yuri_petrov, stepan_petrov).
parent(marina_vlasova, stepan_petrov).

parent(yuri_petrov, sofya_petrova).
parent(marina_vlasova, sofya_petrova).

parent(denis_ivanov, mark_ivanov).
parent(ksenia_melnikova, mark_ivanov).

parent(denis_ivanov, eva_ivanova).
parent(alina_grebneva, eva_ivanova).

parent(elena_ivanova_jr, vera_ivanova).
parent(roman_zakharov, vera_ivanova).

parent(elena_ivanova_jr, gleb_ivanov).
parent(roman_zakharov, gleb_ivanov).

parent(kirill_smirnov, timofey_smirnov).
parent(darya_lebedeva, timofey_smirnov).

parent(artyom_ivanov, lev_ivanov).
parent(anna_yureva, lev_ivanov).

parent(artyom_ivanov, nina_ivanova).
parent(anna_yureva, nina_ivanova).

parent(ilya_orlov, matvey_orlov).
parent(ekaterina_nikolaeva, matvey_orlov).

parent(ilya_orlov, agata_orlova).
parent(ekaterina_nikolaeva, agata_orlova).

parent(polina_smirnova, alena_smirnova).


% Мертва ли персона P в году Y
deceased_in(P,Y) :-
        death(P,DY), DY =< Y.

% Жива ли персона P в году Y
alive_in(P,Y) :-
        birth(P,BY),
        BY =< Y,
        \+ deceased_in(P,Y) .

% Возраст персоны P в году Y, переменная Age
age_in(P,Y,Age) :-
        birth(P,BY),
        Y >= BY,
        Age is Y - BY.

% Переменная S - супруг у X в году Year
spouse_in(X,Year,S) :- 
        (married(X,S,MYear); married(S,X,MYear)),
        MYear =< Year,
        \+ ((divorced(X,S,DYear); divorced(S,X,DYear)), DYear =< Year),
        \+ (death(X,DX), DX =< Year),
        \+ (death(S,DS), DS =< Year).

% Женаты ли X и Y в году Year
married_in(X,Y,Year) :-
        spouse_in(X,Year,Y).

% Могут ли персоны P1 и P2 вступить в брак в году Y. Условия: оба живы, не в браке, возраст >=18
can_marry_in_year(P1,P2,Y) :-
        P1 \= P2,
        alive_in(P1,Y), alive_in(P2,Y),
        \+ spouse_in(P1,Y,_), \+ spouse_in(P2,Y,_),
        age_in(P1,Y,A1), A1 >= 18,
        age_in(P2,Y,A2), A2 >= 18.

% Родство

% Отец
father(F,C) :-
        parent(F,C),
        male(F).

% Мать
mother(M,C) :-
        parent(M,C),
        female(M).

% Братья
sibling(A,B) :-
        parent(P,A), parent(P,B), A \= B.

% Бабушки/дедушки
grandparent(G,C) :-
        parent(G,P), parent(P,C).

% Внуки
grandchild(C,G) :-
        grandparent(G,C).
