%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%   Two-Room Blocks World Planner
%
%   University of Central Florida
%   CAP4630 Artificial Intelligence
%	Spring 2019
%   Author:  Justin Powell
%
%   Extended from the planner.pl that was provided
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

:- module( planner,
	   [
	       plan/4,change_state/3,conditions_met/2,member_state/2,
	       move/3,go/2,test1/0,test2/0
	   ]).

:- [utils].

plan(State, Goal, _, Moves) :-	equal_set(State, Goal),
				write('moves are'), nl,
				reverse_print_stack(Moves).
plan(State, Goal, Been_list, Moves) :-
				move(Name, Preconditions, Actions),
				conditions_met(Preconditions, State),
				change_state(State, Actions, Child_state),
				not(member_state(Child_state, Been_list)),
				stack(Child_state, Been_list, New_been_list),
				stack(Name, Moves, New_moves),
			plan(Child_state, Goal, New_been_list, New_moves),!.

change_state(S, [], S).
change_state(S, [add(P)|T], S_new) :-	change_state(S, T, S2),
					add_to_set(P, S2, S_new), !.
change_state(S, [del(P)|T], S_new) :-	change_state(S, T, S2),
					remove_from_set(P, S2, S_new), !.
conditions_met(P, S) :- subset(P, S).

member_state(S, [H|_]) :-	equal_set(S, H).
member_state(S, [_|T]) :-	member_state(S, T).

/* move types */

/* pickup room 1 */
% crane is empty in room 1, pick up block X, which was on top of Y
move(pickup(X), [handempty, room1, clear(X), onroom1(X, Y)],
		[del(handempty), del(clear(X)), del(onroom1(X, Y)), add(clear(Y)),	add(holding(X))]).

% pick up block X from the table in room 1
move(pickup(X), [handempty, room1, clear(X), ontable1(X)],
		[del(handempty), del(clear(X)), del(ontable1(X)), add(holding(X))]).

/* pickup room 2 */
% crane is empty in room 2, pick up block X, Y is underneath
move(pickup(X), [handempty, room2, clear(X), onroom2(X, Y)],
		[del(handempty), del(clear(X)), del(onroom2(X, Y)), add(clear(Y)),	add(holding(X))]).

% pick up block X from the table in room 2
move(pickup(X), [handempty, room2, clear(X), ontable2(X)],
		[del(handempty), del(clear(X)), del(ontable2(X)), add(holding(X))]).

/* putdown room 1, 2 */
% putdown block X onto the table in room 1
move(putdown(X), [holding(X), room1],
		[del(holding(X)), add(ontable1(X)), add(clear(X)), add(handempty)]).

% putdown block X onto the table in room 2
move(putdown(X), [holding(X), room2],
		[del(holding(X)), add(ontable2(X)), add(clear(X)), add(handempty)]).

/* stack room 1 */
move(stack(X,Y), [holding(X), clear(Y), ontable1(Y), room1],
                [del(holding(X)), del(clear(Y)), add(handempty), add(onroom1(X,Y)), add(clear(X))]).

move(stack(X,Y), [holding(X), clear(Y), onroom1(Y, _), room1],
                [del(holding(X)), del(clear(Y)), add(handempty), add(onroom1(X,Y)), add(clear(X))]).

/* stack room 2 */
move(stack(X,Y), [holding(X), clear(Y), ontable2(Y), room2],
                [del(holding(X)), del(clear(Y)), add(handempty), add(onroom2(X,Y)), add(clear(X))]).

move(stack(X,Y), [holding(X), clear(Y), onroom2(Y, _), room2],
                [del(holding(X)), del(clear(Y)), add(handempty), add(onroom2(X,Y)), add(clear(X))]).


/* moves */
% alternate between the rooms
move(moveroom1, [room2], [del(room2), add(room1)]).

move(moveroom2, [room1], [del(room1), add(room2)]).

/* run commands */

go(S, G) :- plan(S, G, [S], []).

test1 :- go([handempty, room1, ontable1(b), ontable1(c), onroom1(a, b), clear(c), clear(a)],
	          [handempty, room1, ontable1(c), onroom1(a, b), onroom1(b, c), clear(a)]).

test2 :- go([handempty, room1, ontable1(b), ontable1(c), onroom1(a, b), clear(c), clear(a)],
	          [handempty, room1, ontable2(b), onroom2(c, b), onroom2(a, c), clear(a)]).
