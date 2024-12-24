
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
    | plusMinusExpression
    | timesDivideExpression
    | implicitTimesExpression
    | powerExpression
    | unaryMinusExpression
    | degreeExpression
    ;

parenthesizedExpression
    : OPEN_PAREN expression CLOSE_PAREN
    ;

plusMinusExpression: plusMinusChildExpression ( operator=( PLUS | MINUS ) plusMinusChildExpression )+;
plusMinusChildExpression
    : value
    | functionExpression
    | timesDivideExpression
    | implicitTimesExpression
    | powerExpression
    | unaryMinusExpression
    | degreeExpression
    | parenthesizedExpression
    ;

timesDivideExpression: timesDivideChildExpression ( operator=( TIMES | DIVIDE ) timesDivideChildExpression )+;
timesDivideChildExpression
    : value
    | functionExpression
    | implicitTimesExpression
    | powerExpression
    | unaryMinusExpression
    | degreeExpression
    | parenthesizedExpression
    ;

implicitTimesExpression: implicitTimesFirstChildExpression ( implicitTimesChildExpression )+;
implicitTimesFirstChildExpression
    : value
    | functionExpression
    | powerExpression
    | unaryMinusExpression
    | degreeExpression
    | parenthesizedExpression
    ;
implicitTimesChildExpression
    : value
    | functionExpression
    | powerExpression
    // Can't have unary minus - think about why
    | parenthesizedExpression
    ;

powerExpression: powerChildExpression POWER powerChildExpression;
powerChildExpression
    : value
    | functionExpression
    | unaryMinusExpression
    | degreeExpression
    | parenthesizedExpression
    ;

unaryMinusExpression: MINUS unaryMinusChildExpression;
unaryMinusChildExpression
    : value
    | functionExpression
    | parenthesizedExpression
    ;

degreeExpression: degreeChildExpression DEGREE_SIGN;
degreeChildExpression
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
    : magnitude=NUMBER
    ;

constant
    : TAU
    | PI
    | PHI
    | E
    ;
