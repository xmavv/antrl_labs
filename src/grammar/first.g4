grammar first;

prog:	stat* EOF ;

stat: expr #expr_stat
    | FOR '(' start=expr ';' cond=expr_log ';' stop=expr ')' then=block #for_stat
    | WHILE '(' cond=expr_log ')' then=block #while_stat
    | IF_kw '(' cond=expr_log ')' then=block  ('else' else=block)? #if_stat
    | '>' expr #print_stat
    | '>' expr_log #print_stat
    ;

expr_log: l=expr op=('==' | '!=') r=expr
    ;

block : stat #block_single
    | '{' block* '}' #block_real
    ;

expr:
        l=expr op=(MUL|DIV) r=expr #binOp
    |	l=expr op=(ADD|SUB) r=expr #binOp
    |	INT #int_tok
    |   ID #id_tok
    |	'(' expr ')' #pars
    | <assoc=right> ID '=' expr #assign
    ;

WHILE : 'while' ;

FOR : 'for' ;

IF_kw : 'if' ;

DIV : '/' ;

MUL : '*' ;

SUB : '-' ;

ADD : '+' ;

//NEWLINE : [\r\n]+ -> skip;
NEWLINE : [\r\n]+ -> channel(HIDDEN);

//WS : [ \t]+ -> skip ;
WS : [ \t]+ -> channel(HIDDEN) ;

INT     : [0-9]+ ;


ID : [a-zA-Z_][a-zA-Z0-9_]* ;

COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;
LINE_COMMENT : '//' ~'\n'* '\n' -> channel(HIDDEN) ;