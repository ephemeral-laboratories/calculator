
grammar Expression;

@header {
    package garden.ephemeral.calculator.grammar;
}

start
    : expression EOF
    ;

expression
    : value
    | functionExpression
    | parenthesizedExpression
    | plusExpression
    | minusExpression
    | timesExpression
    | implicitTimesExpression
    | divideExpression
    | powerExpression
    | unaryMinusExpression
    ;

parenthesizedExpression
    : OPEN_PAREN expression CLOSE_PAREN
    ;

plusExpression
    : (value | functionExpression | timesExpression | implicitTimesExpression | divideExpression | powerExpression | unaryMinusExpression | parenthesizedExpression)
      PLUS
      (value | functionExpression | timesExpression | implicitTimesExpression | divideExpression | powerExpression | unaryMinusExpression | parenthesizedExpression)
    ;

minusExpression
    : (value | functionExpression | timesExpression | implicitTimesExpression | divideExpression | powerExpression | unaryMinusExpression | parenthesizedExpression)
      MINUS
      (value | functionExpression | timesExpression | implicitTimesExpression | divideExpression | powerExpression | unaryMinusExpression | parenthesizedExpression)
    ;

timesExpression
    : (value | functionExpression | powerExpression | unaryMinusExpression | parenthesizedExpression)
      TIMES
      (value | functionExpression | powerExpression | unaryMinusExpression | parenthesizedExpression)
    ;

implicitTimesExpression
    : (value | functionExpression | powerExpression | unaryMinusExpression | parenthesizedExpression)
      (value | functionExpression | powerExpression | unaryMinusExpression | parenthesizedExpression)
    ;

divideExpression
    : (value | functionExpression | powerExpression | unaryMinusExpression | parenthesizedExpression)
      DIVIDE
      (value | functionExpression | powerExpression | unaryMinusExpression | parenthesizedExpression)
    ;

powerExpression
    : (value | functionExpression | unaryMinusExpression | parenthesizedExpression)
      POWER
      (value | functionExpression | unaryMinusExpression | parenthesizedExpression)
    ;

unaryMinusExpression
    : MINUS
      (value | functionExpression | parenthesizedExpression)
    ;

functionExpression
    : function1Expression
    | function2Expression
    ;

function1Expression
    : ( 'sin' | 'cos' | 'tan' | 'sec' | 'csc' | 'cot'
      | 'sinh' | 'cosh' | 'tanh' | 'sech' | 'csch' | 'coth'
      | 'asin' | 'acos' | 'atan' | 'asec' | 'acsc' | 'acot'
      | 'asinh' | 'acosh' | 'atanh' | 'asech' | 'acsch' | 'acoth'
      | 'exp' | 'log' | 'sqrt'
      | 'abs' | 'arg' | 'Re' | 'Im'
      )
      OPEN_PAREN arg=expression CLOSE_PAREN
    ;

function2Expression
    : 'pow'
      OPEN_PAREN arg1=expression COMMA arg2=expression CLOSE_PAREN
    ;

value
    : complexNumber
    | realNumber
    | constant
    ;

complexNumber
    : realSign=( PLUS | MINUS )? real=NUMBER imagSign=( PLUS | MINUS ) ( imag=NUMBER )? IMAG_UNIT
    | imagSign=( PLUS | MINUS )? ( imag=NUMBER )? IMAG_UNIT
    ;

realNumber
    : sign=( PLUS | MINUS )? magnitude=NUMBER
    ;

constant
    : TAU
    | PI
    | E
    ;

NUMBER: Digit+ ( RadixSeparator Digit+ )?;
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
