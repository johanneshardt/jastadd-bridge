package lang.ast; // The generated scanner will belong to the package lang.ast

import lang.ast.LangParser.Terminals; // The terminals are implicitly defined in the parser
import lang.ast.LangParser.SyntaxError;

%%

// define the signature for the generated scanner
%public
%final
%class LangScanner
%extends beaver.Scanner

// the interface between the scanner and the parser is the nextToken() method
%type beaver.Symbol 
%function nextToken 

// store line and column information in the tokens
%line
%column

// this code will be inlined in the body of the generated scanner class
%{
  private beaver.Symbol sym(short id) {
    return new beaver.Symbol(id, yyline + 1, yycolumn + 1, yylength(), yytext());
  }
%}

// macros
WhiteSpace = [ ] | \t | \f | \n | \r
ID = [a-zA-Z]+
Numeral = [0-9]+ "." [0-9]+

%%

// discard whitespace information
{WhiteSpace}  { }

// token definitions
"let"         { return sym(Terminals.LET); }
"in"          { return sym(Terminals.IN); }
"end"         { return sym(Terminals.END); }
"ask"         { return sym(Terminals.ASK); }
"user"        { return sym(Terminals.USER); }
"="           { return sym(Terminals.ASSIGN); }
"*"           { return sym(Terminals.MUL); }
"/"           { return sym(Terminals.DIV); }
"["           { return sym(Terminals.LBRACKET); }
"]"           { return sym(Terminals.RBRACKET); }
{ID}          { return sym(Terminals.ID); }
{Numeral}     { return sym(Terminals.NUMERAL); }
<<EOF>>       { return sym(Terminals.EOF); }

/* error fallback */
[^]           { throw new SyntaxError("Illegal character <"+yytext()+">"); }
