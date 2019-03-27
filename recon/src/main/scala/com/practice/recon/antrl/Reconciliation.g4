grammar Reconciliation;
def:(source 'where' join 'on' expr|group)*?;
source:('match' ID format 'with' ID format)*?;
join: join('and'|'or')
join
    | ID operator ID
    ;
expr: expr('and'|'or')
expr
    | ID operator ID
    | operation operator operation
    ;
group: ('group by' ID+);
operator:'=';
operation: ('sum'|'avg')*?('('ID')');
format: ('csv'|'parquet'|'stream')*?('('PATH')');
ID: [a-z]+;
WS: [ \t\r\n]+ -> skip;
PATH: [-.a-zA-Z:/]+;