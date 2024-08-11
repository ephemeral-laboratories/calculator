
lexer grammar ExpressionLexer;

SIN: 'sin';
COS: 'cos';
TAN: 'tan';
SEC: 'sec';
CSC: 'csc';
COT: 'cot';
SINH: 'sinh';
COSH: 'cosh';
TANH: 'tanh';
SECH: 'sech';
CSCH: 'csch';
COTH: 'coth';
ASIN: 'asin';
ACOS: 'acos';
ATAN: 'atan';
ASEC: 'asec';
ACSC: 'acsc';
ACOT: 'acot';
ASINH: 'asinh';
ACOSH: 'acosh';
ATANH: 'atanh';
ASECH: 'asech';
ACSCH: 'acsch';
ACOTH: 'acoth';
EXP: 'exp';
LOG: 'log';
SQRT: 'sqrt';
ABS: 'abs';
ARG: 'arg';
SGN: 'sgn';
RE: 'Re';
IM: 'Im';
CONJ: 'conj';
POW: 'pow';

NUMBER
  : Digit+ ( RadixSeparator Digit+ )?
  | RadixSeparator Digit+
  ;
PLUS: '+';
MINUS: '-' | '−';
TIMES: '*' | '×';
DIVIDE: '/' | '÷';
POWER: '^';
OPEN_PAREN: '(';
CLOSE_PAREN: ')';
IMAG_UNIT: 'i';
TAU: 'τ' | 't' 'a' 'u';
PI: 'π' | 'p' 'i';
E: 'e';
COMMA: ',';

fragment RadixSeparator: ( ';' | '.' | ',' );
fragment Digit: ( [0-9] | '↊' | '↋' );

WS: ( ' ' | '\t' | '\r' | '\n' ) -> skip;
