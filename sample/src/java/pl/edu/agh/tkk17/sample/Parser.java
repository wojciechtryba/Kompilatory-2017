package pl.edu.agh.tkk17.sample;

import java.util.Arrays;
import java.util.Iterator;

public class Parser
{
    private Iterator<Token> tokens;
    private Token ctoken;

    public Parser(Iterable<Token> tokens)
    {
        this.tokens = tokens.iterator();
        this.forward();
    }

    private void forward()
    {
        this.ctoken = this.tokens.next();
    }

    private String value()
    {
        return this.ctoken.getValue();
    }

    private boolean check(TokenType type)
    {
        return this.ctoken.getType() == type;
    }

    private void expect(TokenType type)
    {
        if (!this.check(type)) {
            throw new UnexpectedTokenException(this.ctoken, type);
        }
    }

    private Node parseFactor()
    {
	if (this.check(TokenType.LBR)) {
		this.forward();
		Node n = this.parseExpression();	// <--------------- tu usuwać zamknięcia
		if ( this.check(TokenType.RBR) ) {
			this.forward();
			return n;
		} else throw new UnexpectedTokenException(this.ctoken);
	} else if (this.check(TokenType.NUM)) {
		Node n = parseNumber();
//		while (this.check(TokenType.RBR)) this.forward();
		return n;
	}// else if (this.check(TokenType.RBR)) {
	//	this.forward();
	//	return this.parseExpression();
	//} 
	else throw new UnexpectedTokenException(this.ctoken);
    }

    private Node parseNumber()
    {
        this.expect(TokenType.NUM);
        String value = this.value();
        this.forward();
        return new NodeNumber(value);
    }

    private Node parseTerm()
    {
/*
        Node left = this.parseNumber();
        if (this.check(TokenType.MUL)) {
            this.forward();
            Node right = this.parseTerm();
            return new NodeMul(left, right);
        } else {
            return left;
        }
*/
	Node left = this.parseFactor(); //parseNumber();
        if (this.check(TokenType.MUL)) {
            this.forward();
            Node right = this.parseTerm();
            return new NodeMul(left, right);
        } else if (this.check(TokenType.DIV)) {
            this.forward();
            Node right = this.parseTerm();
            return new NodeDiv(left, right);
	} else {
            return left;
        }
    }

    private Node parseExpression()
    {
/*
        Node left = this.parseTerm();
        if (this.check(TokenType.ADD)) {
            this.forward();
            Node right = this.parseExpression();
            return new NodeAdd(left, right);
        } else {
            return left;
        }
*/
	Node left = this.parseTerm();
        if (this.check(TokenType.ADD)) {
            this.forward();
            Node right = this.parseExpression();
            return new NodeAdd(left, right);
        } else if (this.check(TokenType.SUB)) {
            this.forward();
            Node right = this.parseExpression();
            return new NodeSub(left, right);
	} else {
            return left;
        }
    }

    private Node parseProgram()
    {
        Node root = this.parseExpression();
        this.expect(TokenType.END);
        return root;
    }

    public static Node parse(Iterable<Token> tokens)
    {
        Parser parser = new Parser(tokens);
        Node root = parser.parseProgram();
        return root;
    }
}
