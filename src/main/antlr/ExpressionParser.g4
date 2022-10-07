
parser grammar ExpressionParser;

options {
    tokenVocab = ExpressionLexer;
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

plusExpression: plusChildExpression PLUS plusChildExpression;
plusChildExpression
    : value
    | functionExpression
    | timesExpression
    | implicitTimesExpression
    | divideExpression
    | powerExpression
    | unaryMinusExpression
    | parenthesizedExpression
    ;

minusExpression: minusChildExpression MINUS minusChildExpression;
minusChildExpression
    : value
    | functionExpression
    | timesExpression
    | implicitTimesExpression
    | divideExpression
    | powerExpression
    | unaryMinusExpression
    | parenthesizedExpression
    ;

timesExpression: timesChildExpression TIMES timesChildExpression;
timesChildExpression
    : value
    | functionExpression
    | powerExpression
    | unaryMinusExpression
    | parenthesizedExpression
    ;

implicitTimesExpression: implicitTimesChildExpression implicitTimesChildExpression;
implicitTimesChildExpression
    : value
    | functionExpression
    | powerExpression
    | unaryMinusExpression
    | parenthesizedExpression
    ;

divideExpression: divideChildExpression DIVIDE divideChildExpression;
divideChildExpression
    : value
    | functionExpression
    | powerExpression
    | unaryMinusExpression
    | parenthesizedExpression
    ;

powerExpression: powerChildExpression POWER powerChildExpression;
powerChildExpression
    : value
    | functionExpression
    | unaryMinusExpression
    | parenthesizedExpression
    ;

unaryMinusExpression: MINUS unaryMinusChildExpression;
unaryMinusChildExpression
    : value
    | functionExpression
    | parenthesizedExpression
    ;

functionExpression
    : function1Expression
    | function2Expression
    ;

function1Expression
    : func=( SIN | COS | TAN | SEC | CSC | COT
      | SINH | COSH | TANH | SECH | CSCH | COTH
      | ASIN | ACOS | ATAN | ASEC | ACSC | ACOT
      | ASINH | ACOSH | ATANH | ASECH | ACSCH | ACOTH
      | EXP | LOG | SQRT
      | ABS | ARG | SGN | RE | IM | CONJ
      )
      OPEN_PAREN arg=expression CLOSE_PAREN
    ;

function2Expression
    : func=POW
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
