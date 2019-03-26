grammar Reconciliation;
def:(source 'on' expr 'where' join)*;
source:('match' ID 'with' ID)*;
expr: expr('and'|'or')
expr
    | ID operation ID
    ;
join: join('and'|'or')
join
    | ID operation ID
    ;
operation:'=';
ID  : [a-z]+;
WS  : [ \t\r\n]+ -> skip;