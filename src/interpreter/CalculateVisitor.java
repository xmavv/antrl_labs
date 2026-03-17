package interpreter;

import grammar.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;
import SymbolTable.GlobalSymbols;

public class CalculateVisitor extends firstBaseVisitor<Integer> {
    private TokenStream tokStream = null;
    private CharStream input=null;
    private GlobalSymbols<Integer> globals = new GlobalSymbols<>();
    public CalculateVisitor(CharStream inp) {
        super();
        this.input = inp;
    }

    public CalculateVisitor(TokenStream tok) {
        super();
        this.tokStream = tok;
    }
    public CalculateVisitor(CharStream inp, TokenStream tok) {
        super();
        this.input = inp;
        this.tokStream = tok;
    }

    @Override
    public Integer visitFor_stat(firstParser.For_statContext ctx) {
        return super.visitFor_stat(ctx);

//        if(visit(ctx.assign_stat().ID()))
    }

    private String getText(ParserRuleContext ctx) {
        int a = ctx.start.getStartIndex();
        int b = ctx.stop.getStopIndex();
        if(input==null) throw new RuntimeException("Input stream undefined");
        return input.getText(new Interval(a,b));
    }

    @Override
    public Integer visitWhile_stat(firstParser.While_statContext ctx) {
        while(visit(ctx.cond) != 0) {
            visit(ctx.then);
        }

        return null;
    }

    @Override
    public Integer visitIf_stat(firstParser.If_statContext ctx) {
        Integer result = 0;
        if (visit(ctx.cond)!=0) {
            result = visit(ctx.then);
        }
        else {
            if(ctx.else_ != null)
                result = visit(ctx.else_);
        }
        return result;
    }

    @Override
    public Integer visitPrint_stat(firstParser.Print_statContext ctx) {
        if(ctx.expr_log()!=null) {
            var st = ctx.expr_log();
            var result =visit(st);
            if(result != 0) {
                System.out.printf("true\n");
            } else {
                System.out.printf("false\n");
            }

            return result;
        }
        else {
            var st = ctx.expr();
            var result = visit(st);
//          System.out.printf("|%s=%d|\n", st.getText(), result); //nie drukuje ukrytych ani pominiętych spacji
//          System.out.printf("|%s=%d|\n", getText(st),  result); //drukuje wszystkie spacje
            System.out.printf("|%s=%d|\n", tokStream.getText(st),  result); //drukuje spacje z ukrytego kanału, ale nie ->skip
            return result;
        }
    }

    @Override
    public Integer visitInt_tok(firstParser.Int_tokContext ctx) {
        return Integer.valueOf(ctx.INT().getText());
    }

    @Override
    public Integer visitPars(firstParser.ParsContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Integer visitAssign(firstParser.AssignContext ctx) {
        String n = ctx.ID().getText();
        Integer v = visit(ctx.expr());
        if(globals.hasSymbol(n)) globals.setSymbol(n, v);
        else globals.newSymbol(n, v);
        return v;
    }

    @Override
    public Integer visitId_tok(firstParser.Id_tokContext ctx) {
        String n = ctx.ID().getText();
        return globals.getSymbol(n);
    }

    @Override
    public Integer visitExpr_log(firstParser.Expr_logContext ctx) {
        if(ctx.op.getText().equals("=="))
            return visit(ctx.l) == visit(ctx.r) ? 1 : 0;

        if(ctx.op.getText().equals("!="))
            return visit(ctx.expr(0)) == visit(ctx.expr(1)) ? 0 : 1;

        return null;
    }

    @Override
    public Integer visitBinOp(firstParser.BinOpContext ctx) {
        Integer result=0;
        switch (ctx.op.getType()) {
            case firstLexer.ADD:
                result = visit(ctx.l) + visit(ctx.r);
                break;
            case firstLexer.SUB:
                result = visit(ctx.l) - visit(ctx.r);
                break;
            case firstLexer.MUL:
                result = visit(ctx.l) * visit(ctx.r);
                break;
            case firstLexer.DIV:
                try {
                    result = visit(ctx.l) / visit(ctx.r);
                } catch (Exception e) {
                    System.err.println("Div by zero");
                    throw new ArithmeticException();
                }
        }
        return result;
    }

}
