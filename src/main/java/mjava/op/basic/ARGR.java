package mjava.op.basic;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

import com.github.javaparser.resolution.types.ResolvedType;
import edu.ecnu.sqslab.mjava.MutantsGenerator;
import mjava.op.util.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 字符串类型的变(常)量值变为两个字符串的和; String s; method(s)->method(s+s);method(":") -> method("::")
 * 整数类型的变(常)量;int a;  method(a);  method(a*a) ,method(a+a);
 */
/**
 * Created by user on 2018/5/5.
 * @author Jian Liu
 */
public class ARGR extends MethodLevelMutator {
    private  static final Logger logger = LoggerFactory.getLogger(ARGR.class);

    public ARGR(CompilationUnit comp_unit) {
        super(comp_unit);
    }

    public void visit(MethodCallExpr me, Object obj){
        NodeList<Expression> exList =  me.getArguments();
        for(int i =0;i<exList.size();i++){
            Expression ex = exList.get(i);
            try{
                if(ex.isStringLiteralExpr()){
                    StrArgRepMutantGen(me,ex,i);
                }
                else if(ex.isMethodCallExpr()|| ex.isNameExpr()){
                    ResolvedType t = MutantsGenerator.getJavaParserFacade().getType(ex);
                    if(t.describe().endsWith("int")|| t.describe().endsWith("Long")){
                        genArgRepMutantGen(me,ex,i);
                    }
                    else if(t.describe().endsWith("String")){
                        StrArgRepMutantGen(me,ex,i);
                    }
                }
            }
            catch (Exception e){
                //System.err.println(ex.toString()+" : Unsolved Symbol Exception!!!Ignore it...");
            }
        }
        super.visit(me,obj);
    }

    private void StrArgRepMutantGen(MethodCallExpr me,Expression ex,int pos){
        MethodCallExpr mutant = me.clone();
        BinaryExpr binaryExpr = new BinaryExpr(ex,ex, BinaryExpr.Operator.PLUS);
        mutant.setArgument(pos,binaryExpr);
        outputToFile(me,mutant);
    }

    private void genArgRepMutantGen(MethodCallExpr me,Expression ex,int pos){
//        MethodCallExpr mutantPlus = me.clone();
//        BinaryExpr binaryExpr = new BinaryExpr(ex,ex, BinaryExpr.Operator.PLUS);
//        mutantPlus.setArgument(pos,binaryExpr);
//        outputToFile(me,mutantPlus);

        MethodCallExpr mutantMulti = me.clone();
        BinaryExpr binaryExpr2 = new BinaryExpr(ex,ex, BinaryExpr.Operator.MULTIPLY);
        mutantMulti.setArgument(pos,binaryExpr2);
        outputToFile(me,mutantMulti);
    }

    /**
     * Output ARGR mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(MethodCallExpr original, MethodCallExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null){
            return;
        }
        num++;
        String f_name = getSourceName("ARGR");
        String mutant_dir = getMuantID("ARGR");
        try {
            PrintWriter out = getPrintWriter(f_name);
            ARGR_Writer writer = new ARGR_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(comp_unit);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.err.println("ARGR: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
